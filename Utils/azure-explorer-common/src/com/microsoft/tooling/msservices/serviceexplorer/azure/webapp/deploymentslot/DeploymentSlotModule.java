/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.tooling.msservices.serviceexplorer.azure.webapp.deploymentslot;

import com.microsoft.azure.toolkit.lib.appservice.model.OperatingSystem;
import com.microsoft.azure.toolkit.lib.appservice.service.IWebApp;
import com.microsoft.azure.toolkit.lib.appservice.service.IWebAppDeploymentSlot;
import com.microsoft.azure.toolkit.lib.common.operation.AzureOperation;
import com.microsoft.azuretools.azurecommons.helpers.NotNull;
import com.microsoft.azuretools.azurecommons.helpers.Nullable;
import com.microsoft.tooling.msservices.serviceexplorer.AzureIconSymbol;
import com.microsoft.tooling.msservices.serviceexplorer.AzureRefreshableNode;
import com.microsoft.tooling.msservices.serviceexplorer.Node;
import com.microsoft.tooling.msservices.serviceexplorer.azure.webapp.WebAppModule;

import java.util.List;

public class DeploymentSlotModule extends AzureRefreshableNode implements DeploymentSlotModuleView {
    private static final String MODULE_ID = WebAppModule.class.getName();
    private static final String ICON_PATH = "Slot_16.png";

    private final DeploymentSlotModulePresenter presenter;
    public static final String MODULE_NAME = "Deployment Slots";
    protected final String subscriptionId;
    protected final IWebApp webapp;

    public DeploymentSlotModule(final Node parent, final String subscriptionId, final IWebApp webapp) {
        super(MODULE_ID, MODULE_NAME, parent, ICON_PATH);
        this.subscriptionId = subscriptionId;
        this.webapp = webapp;
        presenter = new DeploymentSlotModulePresenter<>();
        presenter.onAttachView(this);
    }

    @Override
    public @Nullable AzureIconSymbol getIconSymbol() {
        boolean isLinux = webapp.getRuntime().getOperatingSystem() != OperatingSystem.WINDOWS;
        return isLinux ? AzureIconSymbol.DeploymentSlot.MODULE_ON_LINUX : AzureIconSymbol.DeploymentSlot.MODULE;
    }

    @Override
    @AzureOperation(name = "webapp|deployment.delete", params = {"name", "this.webapp.name()"}, type = AzureOperation.Type.ACTION)
    public void removeNode(final String sid, final String name, Node node) {
        presenter.onDeleteDeploymentSlot(sid, this.webapp.id(), name);
        removeDirectChildNode(node);
    }

    @Override
    @AzureOperation(name = "webapp|deployment.reload", params = {"this.webapp.name()"}, type = AzureOperation.Type.ACTION)
    protected void refreshItems() {
        presenter.onRefreshDeploymentSlotModule(this.subscriptionId, this.webapp.id());
    }

    @Override
    public void renderDeploymentSlots(@NotNull final List<IWebAppDeploymentSlot> slots) {
        slots.forEach(slot -> addChildNode(new DeploymentSlotNode(slot, DeploymentSlotModule.this)));
    }
}
