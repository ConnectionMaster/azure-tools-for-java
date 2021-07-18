package com.microsoft.azure.toolkit.intellij.connector.sql;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.microsoft.azure.toolkit.intellij.connector.ConnectionManager;
import com.microsoft.azure.toolkit.intellij.connector.ModuleResource;
import com.microsoft.azure.toolkit.intellij.connector.ResourceManager;
import com.microsoft.azure.toolkit.intellij.connector.database.DatabaseResource;
import com.microsoft.azure.toolkit.intellij.connector.database.DatabaseResourceConnection;
import org.jetbrains.annotations.NotNull;

public class SQLDatabaseResourceConnectionStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        final String resourceType = DatabaseResource.Definition.SQL_SERVER.getType();
        final String consumerType = ModuleResource.TYPE;
        ResourceManager.registerDefinition(DatabaseResource.Definition.SQL_SERVER);
        ConnectionManager.registerDefinition(resourceType, consumerType, DatabaseResourceConnection.Definition.MODULE_SQL, project);
    }
}
