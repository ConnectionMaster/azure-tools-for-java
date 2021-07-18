/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.intellij.azuresdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class AzureSdkCategoryEntity {
    @JsonProperty("ServiceName")
    private String serviceName;
    @Setter
    @JsonProperty("Category")
    private String category;
    @Setter
    @JsonProperty("Description")
    private String description;
    @JsonProperty("MapTo")
    private String mapTo;
}