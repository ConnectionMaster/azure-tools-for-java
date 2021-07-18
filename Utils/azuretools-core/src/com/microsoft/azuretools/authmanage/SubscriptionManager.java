/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azuretools.authmanage;

import com.microsoft.azure.toolkit.lib.Azure;
import com.microsoft.azure.toolkit.lib.auth.AzureAccount;
import com.microsoft.azure.toolkit.lib.common.exception.AzureToolkitRuntimeException;
import com.microsoft.azure.toolkit.lib.common.operation.AzureOperation;
import com.microsoft.azure.toolkit.lib.common.utils.Utils;
import com.microsoft.azuretools.adauth.JsonHelper;
import com.microsoft.azuretools.authmanage.models.SubscriptionDetail;
import com.microsoft.azuretools.sdkmanage.IdentityAzureManager;
import com.microsoft.azuretools.utils.AzureUIRefreshCore;
import com.microsoft.azuretools.utils.AzureUIRefreshEvent;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by shch on 10/3/2016.
 */
@Log
public class SubscriptionManager {
    private final Set<ISubscriptionSelectionListener> listeners = new HashSet<>();

    private static final String FILE_NAME_SUBSCRIPTIONS_DETAILS = "subscriptionsDetails.json";

    public void setSubscriptionDetails(List<SubscriptionDetail> subscriptionDetails) {
        System.out.println(Thread.currentThread().getId() + " SubscriptionManager.setSubscriptionDetails()");
        synchronized (this) {
            try {
                saveSubscriptions(subscriptionDetails);
                notifyAllListeners(CollectionUtils.isEmpty(subscriptionDetails));
            } catch (final IOException e) {
                final String error = "Failed to update local subscriptions cache while updating";
                final String action = "Retry later";
                throw new AzureToolkitRuntimeException(error, e, action);
            }
        }
    }

    public synchronized void cleanSubscriptions() {
        System.out.println(Thread.currentThread().getId() + " SubscriptionManager.cleanSubscriptions()");
        deleteSubscriptions();
    }

    @AzureOperation(name = "account|subscription.clear_cache", type = AzureOperation.Type.TASK)
    public static synchronized void deleteSubscriptions() {
        System.out.println("cleaning " + FILE_NAME_SUBSCRIPTIONS_DETAILS + " file");
        FileStorage fs = null;
        try {
            fs = new FileStorage(FILE_NAME_SUBSCRIPTIONS_DETAILS, CommonSettings.getSettingsBaseDir());
        } catch (final IOException e) {
            log.warning(FILE_NAME_SUBSCRIPTIONS_DETAILS + " is not found when try to clean subscriptions");
        }
        if (Objects.nonNull(fs)) {
            try {
                fs.cleanFile();
            } catch (final IOException e) {
                final String error = "Failed to clear local cached subscriptions";
                throw new AzureToolkitRuntimeException(error, e);
            }
        }
    }

    @AzureOperation(name = "account|subscription.load_cache", type = AzureOperation.Type.TASK)
    public static List<SubscriptionDetail> loadSubscriptions() {
        System.out.println("SubscriptionManager.loadSubscriptions()");
        //subscriptionDetails.clear();
        try {
            final FileStorage file = new FileStorage(FILE_NAME_SUBSCRIPTIONS_DETAILS, CommonSettings.getSettingsBaseDir());
            final byte[] data = file.read();
            final String json = new String(data, StandardCharsets.UTF_8);
            if (json.isEmpty()) {
                System.out.println(FILE_NAME_SUBSCRIPTIONS_DETAILS + " file is empty");
                return Collections.emptyList();
            }
            final SubscriptionDetail[] sda = JsonHelper.deserialize(SubscriptionDetail[].class, json);
            return new ArrayList<>(Arrays.asList(sda));
        } catch (final IOException e) {
            final String error = "Failed to load local cached subscriptions";
            final String action = "Retry later or logout to clear local cached subscriptions";
            throw new AzureToolkitRuntimeException(error, e);
        }
    }

    @AzureOperation(name = "account|subscription.persist", type = AzureOperation.Type.TASK)
    private static void saveSubscriptions(List<SubscriptionDetail> sdl)
            throws IOException {
        System.out.println("SubscriptionManager.saveSubscriptions()");
        String sd = JsonHelper.serialize(sdl);
        FileStorage subscriptionsDetailsFileStorage = new FileStorage(FILE_NAME_SUBSCRIPTIONS_DETAILS,
                CommonSettings.getSettingsBaseDir());
        subscriptionsDetailsFileStorage.write(sd.getBytes(StandardCharsets.UTF_8));
    }

    public synchronized Map<String, SubscriptionDetail> getSubscriptionIdToSubscriptionDetailsMap() {
        System.out.println(Thread.currentThread().getId() + " SubscriptionManager.getSubscriptionIdToSubscriptionDetailsMap()");
        updateSubscriptionDetailsIfNull();
        return Utils.groupByIgnoreDuplicate(IdentityAzureManager.getInstance().getSubscriptionDetails(), d -> d.getSubscriptionId());
    }

    @AzureOperation(name = "account|subscription.get_details", type = AzureOperation.Type.TASK)
    public synchronized List<SubscriptionDetail> getSubscriptionDetails() {
        System.out.println(Thread.currentThread().getId() + " SubscriptionManager.getSubscriptionDetails()");
        updateSubscriptionDetailsIfNull();
        return IdentityAzureManager.getInstance().getSubscriptionDetails();
    }

    @AzureOperation(name = "account|subscription.get_detail.selected", type = AzureOperation.Type.TASK)
    public synchronized List<SubscriptionDetail> getSelectedSubscriptionDetails() {
        System.out.println(Thread.currentThread().getId() + " SubscriptionManager.getSelectedSubscriptionDetails()");
        updateSubscriptionDetailsIfNull();

        final List<SubscriptionDetail> selectedSubscriptions =
                IdentityAzureManager.getInstance().getSubscriptionDetails().stream().filter(SubscriptionDetail::isSelected).collect(Collectors.toList());

        return selectedSubscriptions;
    }

    public void updateSubscriptionDetailsIfNull() {
    }

    public synchronized void addListener(ISubscriptionSelectionListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public synchronized void removeListener(ISubscriptionSelectionListener l) {
        listeners.remove(l);
    }

    public void notifySubscriptionListChanged() {
        notifyAllListeners(false);
    }

    protected void notifyAllListeners(boolean isRefresh) {
        for (ISubscriptionSelectionListener l : listeners) {
            l.update(isRefresh);
        }
        if (AzureUIRefreshCore.listeners != null) {
            AzureUIRefreshCore.execute(new AzureUIRefreshEvent(AzureUIRefreshEvent.EventType.UPDATE, null));
        }
    }

    public String getSubscriptionTenant(String sid) {
        System.out.println(Thread.currentThread().getId() + " SubscriptionManager.getSubscriptionTenant()");
        return Azure.az(AzureAccount.class).account().getSubscription(sid).getTenantId();
    }
}
