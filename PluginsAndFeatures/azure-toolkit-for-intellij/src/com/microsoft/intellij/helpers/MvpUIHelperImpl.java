/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.intellij.helpers;

import com.microsoft.azuretools.azurecommons.helpers.NotNull;
import com.microsoft.azuretools.core.mvp.ui.base.MvpUIHelper;
import com.microsoft.intellij.secure.IdeaSecureStore;
import com.microsoft.tooling.msservices.components.DefaultLoader;

import javax.swing.*;

public class MvpUIHelperImpl implements MvpUIHelper {

    @Override
    public void showError(String msg) {
        DefaultLoader.getUIHelper().showError(msg, msg);
    }

    @Override
    public void showException(String msg, Exception e) {
        DefaultLoader.getUIHelper().showError(e.getMessage(), msg);
    }

    @Deprecated
    @Override
    public String loadPasswordFromSecureStore(@NotNull String key) {
        return IdeaSecureStore.getInstance().loadPassword(key);
    }

    @Deprecated
    public void forgetPasswordFromSecureStore(@NotNull String key) {
        IdeaSecureStore.getInstance().forgetPassword(key);
    }
}
