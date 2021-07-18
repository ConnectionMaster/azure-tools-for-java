/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azuretools.wasdkjava.ui.classpath;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.microsoft.azuretools.wasdkjava.ui.classpath.messages"; //$NON-NLS-1$
    public static String containerID;
    public static String containerDesc;
    public static String desc;
    public static String excp;
    public static String lblLocation;
    public static String lblVersion;
    public static String libNotAvail;
    public static String notFound;
    public static String sdkID;
    public static String sdkJar;
    public static String title;
    public static String verNotAvail;
    public static String version1;
    public static String version2;
    public static String depLocation;
    public static String depChkBox;
    public static String jstDep;
    public static String edtLbrTtl;
    public static String src;
    public static String appInsightMng;
    public static String adAuth;
    public static String srvExp;
    public static String azureStorage;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
        super();
    }
}
