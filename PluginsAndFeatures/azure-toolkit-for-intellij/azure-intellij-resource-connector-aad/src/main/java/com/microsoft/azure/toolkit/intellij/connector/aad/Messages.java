/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.intellij.connector.aad;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.function.Supplier;

class Messages extends AbstractBundle {
    private static final Messages ourInstance = new Messages();
    private static final String BUNDLE = "messages.aadBundle";

    private Messages() {
        super(BUNDLE);
    }

    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
        return ourInstance.getMessage(key, params);
    }

    public static Supplier<String> lazy(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
        return () -> ourInstance.getMessage(key, params);
    }
}
