/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.tooling.msservices.serviceexplorer.azure.appservice.file;

import com.microsoft.azure.toolkit.lib.appservice.service.IAppService;
import com.microsoft.azure.toolkit.lib.appservice.service.IFunctionAppBase;
import com.microsoft.azure.toolkit.lib.common.operation.AzureOperation;
import com.microsoft.azuretools.azurecommons.helpers.NotNull;
import com.microsoft.azuretools.azurecommons.helpers.Nullable;
import com.microsoft.azuretools.telemetry.AppInsightsConstants;
import com.microsoft.azuretools.telemetry.TelemetryConstants;
import com.microsoft.azuretools.telemetry.TelemetryProperties;
import com.microsoft.azuretools.telemetrywrapper.EventUtil;
import com.microsoft.tooling.msservices.components.DefaultLoader;
import com.microsoft.tooling.msservices.serviceexplorer.AzureRefreshableNode;
import com.microsoft.tooling.msservices.serviceexplorer.Node;
import com.microsoft.tooling.msservices.serviceexplorer.azure.webapp.WebAppModule;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.Collections;
import java.util.Map;

public class AppServiceUserFilesRootNode extends AzureRefreshableNode implements TelemetryProperties {
    private static final String MODULE_ID = WebAppModule.class.getName();
    private static final String MODULE_NAME = "Files";
    private static final String ROOT_PATH = "/site/wwwroot";

    protected IAppService appService;
    protected final String subscriptionId;

    public AppServiceUserFilesRootNode(@Nonnull final Node parent, @Nonnull final String subscriptionId, @Nonnull final IAppService appService) {
        this(MODULE_NAME, parent, subscriptionId, appService);
    }

    public AppServiceUserFilesRootNode(@Nonnull final String name, @Nonnull final Node parent, @Nonnull final String subscriptionId,
                                       @Nonnull final IAppService appService) {
        super(MODULE_ID, name, parent, null);
        this.subscriptionId = subscriptionId;
        this.appService = appService;
    }

    @Override
    public void removeNode(final String sid, final String name, Node node) {
    }

    @Override
    @AzureOperation(name = "appservice|file.list", params = {"this.app.name()"}, type = AzureOperation.Type.ACTION)
    protected void refreshItems() {
        EventUtil.executeWithLog(getServiceName(), TelemetryConstants.LIST_FILE, operation -> {
            operation.trackProperty(TelemetryConstants.SUBSCRIPTIONID, subscriptionId);
            appService.getFilesInDirectory(getRootPath()).stream()
                    .map(file -> new AppServiceFileNode(file, this, appService))
                    .forEach(this::addChildNode);
        });
    }

    @NotNull
    protected String getRootPath() {
        return ROOT_PATH;
    }

    @Override
    public String getServiceName() {
        return appService instanceof IFunctionAppBase ? TelemetryConstants.FUNCTION : TelemetryConstants.WEBAPP;
    }

    @Override
    public Map<String, String> toProperties() {
        return Collections.singletonMap(AppInsightsConstants.SubscriptionId, subscriptionId);
    }

    @Override
    public @Nullable Icon getIcon() {
        return DefaultLoader.getIdeHelper().getFileTypeIcon("/", true);
    }
}
