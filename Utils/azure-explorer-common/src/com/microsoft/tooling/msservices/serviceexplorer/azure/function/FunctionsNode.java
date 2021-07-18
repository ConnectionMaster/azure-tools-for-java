/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */
package com.microsoft.tooling.msservices.serviceexplorer.azure.function;

import com.microsoft.azure.toolkit.lib.appservice.service.IFunctionApp;
import com.microsoft.azure.toolkit.lib.common.operation.AzureOperation;
import com.microsoft.tooling.msservices.serviceexplorer.Node;
import com.microsoft.tooling.msservices.serviceexplorer.RefreshableNode;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;

public class FunctionsNode extends RefreshableNode {

    private static final String ID = FunctionsNode.class.getName();
    private static final String NAME = "Functions";
    private static final String ICON_PATH = "azure-functions-small.png";
    private static final String EMPTY_POSTFIX = " (Empty)";

    private final IFunctionApp functionApp;

    public FunctionsNode(@Nonnull final Node parent, @Nonnull final IFunctionApp functionApp) {
        super(ID, NAME, parent, ICON_PATH);
        this.functionApp = functionApp;
    }

    @Override
    @AzureOperation(name = "function.refresh", type = AzureOperation.Type.ACTION)
    protected void refreshItems() {
        functionApp.listFunctions(true).stream()
                .map(envelope -> new FunctionNode(envelope, functionApp, this))
                .forEach(this::addChildNode);
        setName(CollectionUtils.isEmpty(childNodes) ? NAME + EMPTY_POSTFIX : NAME);
    }

    @Override
    public int getPriority() {
        return HIGH_PRIORITY;
    }
}
