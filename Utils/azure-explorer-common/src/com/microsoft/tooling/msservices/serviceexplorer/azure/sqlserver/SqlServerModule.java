/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.tooling.msservices.serviceexplorer.azure.sqlserver;

import com.microsoft.azure.toolkit.lib.Azure;
import com.microsoft.azure.toolkit.lib.sqlserver.service.AzureSqlServer;
import com.microsoft.azure.toolkit.lib.sqlserver.service.ISqlServer;
import com.microsoft.azuretools.azurecommons.helpers.AzureCmdException;
import com.microsoft.azuretools.azurecommons.helpers.Nullable;
import com.microsoft.azuretools.utils.AzureUIRefreshCore;
import com.microsoft.azuretools.utils.AzureUIRefreshListener;
import com.microsoft.tooling.msservices.serviceexplorer.AzureIconSymbol;
import com.microsoft.tooling.msservices.serviceexplorer.AzureRefreshableNode;
import com.microsoft.tooling.msservices.serviceexplorer.Node;

import java.util.List;
import java.util.Objects;

public class SqlServerModule extends AzureRefreshableNode {

    public static final String MODULE_NAME = "SQL Server";
    private static final String SQL_SERVER_DATABASE_MODULE_ID = SqlServerModule.class.getName();

    public SqlServerModule(final Node parent) {
        super(SQL_SERVER_DATABASE_MODULE_ID, MODULE_NAME, parent);
        createListener();
    }

    @Override
    public @Nullable AzureIconSymbol getIconSymbol() {
        return AzureIconSymbol.SqlServer.MODULE;
    }

    @Override
    protected void refreshItems() throws AzureCmdException {
        List<ISqlServer> servers = Azure.az(AzureSqlServer.class).sqlServers();
        servers.stream()
            .filter(server -> Objects.nonNull(server.entity()))
            .map(server -> new SqlServerNode(this, server.entity().getSubscriptionId(), server))
            .forEach(this::addChildNode);
    }

    @Override
    public void removeNode(String sid, String id, Node node) {
        Azure.az(AzureSqlServer.class).sqlServer(id).delete();
        removeDirectChildNode(node);
    }

    private void createListener() {
        AzureUIRefreshListener listener = new AzureUIRefreshListener() {
            @Override
            public void run() {
                if (event.opsType == null) {
                    return;
                }
                switch (event.opsType) {
                    case SIGNIN:
                    case SIGNOUT:
                        removeAllChildNodes();
                        break;
                    case REFRESH:
                        if (isCurrentModuleEvent(event.object)) {
                            load(true);
                        }
                        break;
                    default:
                        if (isCurrentModuleEvent(event.object) && hasChildNodes()) {
                            load(true);
                        }
                        break;
                }
            }
        };
        AzureUIRefreshCore.addListener("SQL_SERVER_MODULE", listener);
    }

    private boolean isCurrentModuleEvent(Object eventObject) {
        return eventObject != null && eventObject instanceof ISqlServer;
    }

}

