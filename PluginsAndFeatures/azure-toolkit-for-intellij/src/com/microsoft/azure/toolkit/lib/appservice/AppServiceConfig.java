/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.lib.appservice;

import com.microsoft.azure.toolkit.lib.appservice.entity.AppServicePlanEntity;
import com.microsoft.azure.toolkit.lib.appservice.model.PricingTier;
import com.microsoft.azure.toolkit.lib.appservice.model.Runtime;
import com.microsoft.azure.toolkit.lib.common.model.Region;
import com.microsoft.azure.toolkit.lib.common.model.ResourceGroup;
import com.microsoft.azure.toolkit.lib.common.model.Subscription;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@SuperBuilder(toBuilder = true)
public class AppServiceConfig {
    public static final Region DEFAULT_REGION = Region.US_WEST;
    @Builder.Default
    private MonitorConfig monitorConfig = MonitorConfig.builder().build();
    private String name;
    private String resourceId;
    private Path application;
    private Subscription subscription;
    private ResourceGroup resourceGroup;
    private AppServicePlanEntity servicePlan;
    private Region region;
    private PricingTier pricingTier;
    @Builder.Default
    private Map<String, String> appSettings = new HashMap<>();

    protected Runtime runtime;

    public Map<String, String> getTelemetryProperties() {
        final Map<String, String> result = new HashMap<>();
        result.put("subscriptionId", Optional.ofNullable(subscription).map(Subscription::getId).orElse(StringUtils.EMPTY));
        result.put("region", Optional.ofNullable(region).map(Region::getName).orElse(StringUtils.EMPTY));
        result.put("pricingTier", Optional.ofNullable(pricingTier).map(PricingTier::getSize).orElse(StringUtils.EMPTY));
        return result;
    }
}
