package com.awslabs.aws.greengrass.provisioner.interfaces.helpers;

import com.typesafe.config.Config;
import software.amazon.awssdk.services.greengrass.model.Function;

import java.io.File;

public interface GGConstants {
    String CONF_FROM_CLOUD_SUBSCRIPTIONS = "conf.fromCloudSubscriptions";
    String CONF_TO_CLOUD_SUBSCRIPTIONS = "conf.toCloudSubscriptions";
    String CONF_OUTPUT_TOPICS = "conf.outputTopics";
    String CONF_INPUT_TOPICS = "conf.inputTopics";

    String getRootCaUrl();

    String getRootCaName();

    String getConfigFileName();

    String getGreengrassDaemonName();

    String getCorePublicCertificateName();

    String getCorePrivateKeyName();

    String getGgIpDetectorArn();

    Function getGgIpDetectorFunction();

    String getGgShadowServiceName();

    String getBuildDirectory();

    String getCertsDirectoryPrefix();

    String getConfigDirectoryPrefix();

    String getOfficialGreengrassAccountId();

    String getOfficialGreengrassEcrEndpoint();

    String getOfficialGreengrassDockerImageName();

    String getDockerHubGreengrassDockerImageName();

    String getOfficialGreengrassDockerImage();

    String getDefaultsConf();

    File getFunctionDefaultsConf();

    File getConnectorDefaultsConf();

    String getConfGreengrassContainer();

    File getDeploymentDefaultsConf();

    Config getDeploymentDefaults();
}
