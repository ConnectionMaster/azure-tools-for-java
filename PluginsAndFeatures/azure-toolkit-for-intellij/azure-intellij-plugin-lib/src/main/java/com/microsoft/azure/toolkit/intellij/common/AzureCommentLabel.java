/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.intellij.common;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.LabelUI;
import java.awt.*;

public class AzureCommentLabel extends JBLabel {
    public AzureCommentLabel(@NotNull String text) {
        super(text);
        setForeground(UIUtil.getContextHelpForeground());
    }

    @Override
    public void setUI(LabelUI ui) {
        super.setUI(ui);

        if (SystemInfo.isMac) {
            Font font = getFont();
            float size = font.getSize2D();
            font = new FontUIResource(font.deriveFont(size - JBUIScale.scale(2))); // Allow to reset the font by UI
            setFont(font);
        }
    }
}
