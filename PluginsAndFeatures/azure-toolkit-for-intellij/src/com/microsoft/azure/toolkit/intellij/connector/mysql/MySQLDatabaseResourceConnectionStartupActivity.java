package com.microsoft.azure.toolkit.intellij.connector.mysql;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.microsoft.azure.toolkit.intellij.connector.ConnectionManager;
import com.microsoft.azure.toolkit.intellij.connector.database.DatabaseResourceConnection;
import com.microsoft.azure.toolkit.intellij.connector.ModuleResource;
import com.microsoft.azure.toolkit.intellij.connector.ResourceManager;
import com.microsoft.azure.toolkit.intellij.connector.database.DatabaseResource;
import org.jetbrains.annotations.NotNull;

public class MySQLDatabaseResourceConnectionStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        final String resourceType = DatabaseResource.Definition.AZURE_MYSQL.getType();
        final String consumerType = ModuleResource.TYPE;
        ResourceManager.registerDefinition(DatabaseResource.Definition.AZURE_MYSQL);
        ConnectionManager.registerDefinition(resourceType, consumerType, DatabaseResourceConnection.Definition.MODULE_MYSQL, project);
    }
}
