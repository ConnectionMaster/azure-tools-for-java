/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azuretools.utils;


import com.microsoft.azure.toolkit.lib.appservice.model.Runtime;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WebAppUtils {

    // todo: move to app service library utils
    public static boolean isSupportedArtifactType(Runtime runtime, String ext) {
        final String container = runtime.getWebContainer().getValue();
        if (StringUtils.startsWithIgnoreCase(container, "java")) {
            return "jar".equalsIgnoreCase(ext);
        } else if (StringUtils.startsWithIgnoreCase(container, "tomcat")) {
            return "war".equalsIgnoreCase(ext);
        } else if (StringUtils.startsWithIgnoreCase(container, "jboss")) {
            return StringUtils.equalsAnyIgnoreCase(ext, "war", "ear");
        }
        return true;
    }

    public static String getFileType(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        String fileType = "";
        int index = fileName.lastIndexOf(".");
        if (index >= 0 && (index + 1) < fileName.length()) {
            fileType = fileName.substring(index + 1);
        }
        return fileType;
    }

    public static String encodeURL(String fileName) {
        try {
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return StringUtils.EMPTY;
        }
    }
}
