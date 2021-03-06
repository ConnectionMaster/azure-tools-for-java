/*
 * Copyright (c) Microsoft Corporation
 *
 * All rights reserved.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.microsoft.azure.toolkit.intellij.function.runner.deploy;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.microsoft.azure.common.exceptions.AzureExecutionException;
import com.microsoft.azure.common.utils.AppServiceUtils;
import com.microsoft.azure.management.appservice.AppServicePlan;
import com.microsoft.azure.management.appservice.FunctionApp;
import com.microsoft.azure.management.appservice.WebAppBase;
import com.microsoft.azure.toolkit.lib.common.exception.AzureToolkitRuntimeException;
import com.microsoft.azure.toolkit.lib.common.operation.AzureOperation;
import com.microsoft.azure.toolkit.lib.common.task.AzureTaskManager;
import com.microsoft.azuretools.core.mvp.model.function.AzureFunctionMvpModel;
import com.microsoft.azuretools.telemetry.TelemetryConstants;
import com.microsoft.azuretools.telemetrywrapper.Operation;
import com.microsoft.azuretools.telemetrywrapper.TelemetryManager;
import com.microsoft.azuretools.utils.AzureUIRefreshCore;
import com.microsoft.azuretools.utils.AzureUIRefreshEvent;
import com.microsoft.azure.toolkit.intellij.common.AzureRunProfileState;
import com.microsoft.intellij.RunProcessHandler;
import com.microsoft.azure.toolkit.intellij.function.runner.core.FunctionUtils;
import com.microsoft.azure.toolkit.intellij.function.runner.library.function.CreateFunctionHandler;
import com.microsoft.azure.toolkit.intellij.function.runner.library.function.DeployFunctionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static com.microsoft.intellij.ui.messages.AzureBundle.message;

public class FunctionDeploymentState extends AzureRunProfileState<WebAppBase> {

    private FunctionDeployConfiguration functionDeployConfiguration;
    private final FunctionDeployModel deployModel;
    private File stagingFolder;

    /**
     * Place to execute the Web App deployment task.
     */
    public FunctionDeploymentState(Project project, FunctionDeployConfiguration functionDeployConfiguration) {
        super(project);
        this.functionDeployConfiguration = functionDeployConfiguration;
        this.deployModel = functionDeployConfiguration.getModel();
    }

    @Nullable
    @Override
    @AzureOperation(value = "deploy function app", type = AzureOperation.Type.ACTION)
    public WebAppBase executeSteps(@NotNull RunProcessHandler processHandler
            , @NotNull Map<String, String> telemetryMap) throws Exception {
        updateTelemetryMap(telemetryMap);
        // Update run time information by function app
        final FunctionApp functionApp;
        if (deployModel.isNewResource()) {
            functionApp = createFunctionApp(processHandler);
            functionDeployConfiguration.setFunctionId(functionApp.id());
        } else {
            functionApp = AzureFunctionMvpModel.getInstance()
                                               .getFunctionById(functionDeployConfiguration.getSubscriptionId(), functionDeployConfiguration.getFunctionId());
        }
        if (functionApp == null) {
            throw new AzureExecutionException(message("function.deploy.error.functionNonexistent"));
        }
        final AppServicePlan appServicePlan = AppServiceUtils.getAppServicePlanByAppService(functionApp);
        functionDeployConfiguration.setOs(appServicePlan.operatingSystem().name());
        functionDeployConfiguration.setPricingTier(appServicePlan.pricingTier().toSkuDescription().size());
        // Deploy function to Azure
        stagingFolder = FunctionUtils.getTempStagingFolder();
        deployModel.setDeploymentStagingDirectoryPath(stagingFolder.getPath());
        prepareStagingFolder(stagingFolder, processHandler);
        final DeployFunctionHandler deployFunctionHandler = new DeployFunctionHandler(deployModel, message -> {
            if (processHandler.isProcessRunning()) {
                processHandler.setText(message);
            }
        });
        return deployFunctionHandler.execute();
    }

    @AzureOperation(
        value = "get or create function[%s] details from Azure server",
        params = {"@deployModel.getAppName()"},
        type = AzureOperation.Type.SERVICE
    )
    private FunctionApp createFunctionApp(RunProcessHandler processHandler) {
        FunctionApp functionApp =
                AzureFunctionMvpModel.getInstance().getFunctionByName(functionDeployConfiguration.getSubscriptionId(),
                                                                      functionDeployConfiguration.getResourceGroup(),
                                                                      functionDeployConfiguration.getAppName());
        if (functionApp != null) {
            functionDeployConfiguration.setNewResource(false);
            return functionApp;
        }
        processHandler.setText(message("function.create.hint.creating", functionDeployConfiguration.getAppName()));
        final CreateFunctionHandler createFunctionHandler = new CreateFunctionHandler(functionDeployConfiguration.getModel());
        functionApp = createFunctionHandler.execute();
        processHandler.setText(message("function.create.hint.created", functionDeployConfiguration.getAppName()));
        return functionApp;
    }

    @AzureOperation(
        value = "prepare staging folder[%s] for function[%s]",
        params = {"$stagingFolder.getName()", "@deployModel.getAppName()"},
        type = AzureOperation.Type.TASK
    )
    private void prepareStagingFolder(File stagingFolder, RunProcessHandler processHandler) throws Exception {
        AzureTaskManager.getInstance().read(() -> {
            final Path hostJsonPath = FunctionUtils.getDefaultHostJson(project);
            final PsiMethod[] methods = FunctionUtils.findFunctionsByAnnotation(functionDeployConfiguration.getModule());
            final Path folder = stagingFolder.toPath();
            try {
                FunctionUtils.prepareStagingFolder(folder, hostJsonPath, functionDeployConfiguration.getModule(), methods);
            } catch (AzureExecutionException | IOException e) {
                final String error = String.format("failed prepare staging folder[%s]", folder);
                throw new AzureToolkitRuntimeException(error, e);
            }
        });
    }

    @Override
    protected Operation createOperation() {
        return TelemetryManager.createOperation(TelemetryConstants.FUNCTION, TelemetryConstants.DEPLOY_FUNCTION_APP);
    }

    @Override
    @AzureOperation(
        value = "complete the deployment of function[%s] and refresh Azure Explorer",
        params = {"@deployModel.getAppName()"},
        type = AzureOperation.Type.TASK
    )
    protected void onSuccess(WebAppBase result, @NotNull RunProcessHandler processHandler) {
        processHandler.setText(message("appService.deploy.hint.succeed"));
        processHandler.notifyComplete();
        FunctionUtils.cleanUpStagingFolder(stagingFolder);
        if (functionDeployConfiguration.isNewResource() && AzureUIRefreshCore.listeners != null) {
            AzureUIRefreshCore.execute(new AzureUIRefreshEvent(AzureUIRefreshEvent.EventType.REFRESH, result));
        }
    }

    @Override
    protected void onFail(@NotNull Throwable error, @NotNull RunProcessHandler processHandler) {
        super.onFail(error, processHandler);
        FunctionUtils.cleanUpStagingFolder(stagingFolder);
    }

    @Override
    protected String getDeployTarget() {
        return "FUNCTION";
    }

    @Override
    protected void updateTelemetryMap(@NotNull Map<String, String> telemetryMap) {
        telemetryMap.putAll(functionDeployConfiguration.getModel().getTelemetryProperties(telemetryMap));
    }
}
