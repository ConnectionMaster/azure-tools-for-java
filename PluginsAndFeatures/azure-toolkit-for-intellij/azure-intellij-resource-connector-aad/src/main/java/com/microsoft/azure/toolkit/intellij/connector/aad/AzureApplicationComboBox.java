/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.intellij.connector.aad;

import com.microsoft.azure.toolkit.intellij.common.AzureComboBox;
import com.microsoft.graph.models.Application;

/**
 * Combobox listing Azure AD applications.
 */
class AzureApplicationComboBox extends AzureComboBox<Application> {
    AzureApplicationComboBox() {
        super(false);

        this.setEditable(false);
    }

    @Override
    protected String getItemText(Object item) {
        if (item instanceof Application) {
            return ((Application) item).displayName;
        }
        return super.getItemText(item);
    }
}
