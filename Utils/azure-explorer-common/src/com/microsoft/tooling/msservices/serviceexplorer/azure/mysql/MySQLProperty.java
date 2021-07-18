/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.tooling.msservices.serviceexplorer.azure.mysql;

import com.microsoft.azure.toolkit.lib.mysql.service.MySqlFirewallRule;
import com.microsoft.azure.toolkit.lib.mysql.service.MySqlServer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MySQLProperty {

    private MySqlServer server;
    private String subscriptionId;
    private List<MySqlFirewallRule> firewallRules;

}
