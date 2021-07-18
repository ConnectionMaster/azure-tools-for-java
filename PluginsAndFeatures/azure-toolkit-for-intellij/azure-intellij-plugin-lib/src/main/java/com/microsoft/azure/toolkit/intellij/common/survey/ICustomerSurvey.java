/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.intellij.common.survey;

import javax.swing.*;

public interface ICustomerSurvey {
    String getType();
    String getLink();
    String getDescription();
    Icon getIcon();
}
