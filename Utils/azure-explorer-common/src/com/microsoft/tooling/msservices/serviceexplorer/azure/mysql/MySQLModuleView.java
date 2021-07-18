/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.tooling.msservices.serviceexplorer.azure.mysql;

import com.microsoft.azure.toolkit.lib.mysql.service.MySqlServer;
import com.microsoft.azuretools.core.mvp.ui.base.MvpView;

import java.util.List;

public interface MySQLModuleView extends MvpView {

    void renderChildren(List<MySqlServer> servers);

}
