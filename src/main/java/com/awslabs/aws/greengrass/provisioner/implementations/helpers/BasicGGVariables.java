package com.awslabs.aws.greengrass.provisioner.implementations.helpers;

import com.awslabs.aws.greengrass.provisioner.interfaces.helpers.GGConstants;
import com.awslabs.aws.greengrass.provisioner.interfaces.helpers.GGVariables;
import com.awslabs.iot.data.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.greengrass.model.FunctionIsolationMode;

import javax.inject.Inject;

public class BasicGGVariables implements GGVariables {
    @Inject
    GGConstants ggConstants;

    @Inject
    public BasicGGVariables() {
    }

    @Override
    public ThingName getCoreThingName(GreengrassGroupName greengrassGroupName) {
        return ImmutableThingName.builder().name(greengrassGroupName.getGroupName() + "_Core").build();
    }

    @Override
    public String getCoreDefinitionName(GreengrassGroupName greengrassGroupName) {
        return getCoreThingName(greengrassGroupName) + "_Definition";
    }

    @Override
    public PolicyName getCorePolicyName(GreengrassGroupName greengrassGroupName) {
        return ImmutablePolicyName.builder().name(getCoreThingName(greengrassGroupName).getName() + "_Policy").build();
    }

    @Override
    public String getDeviceShadowTopicFilterName(ThingName thingName) {
        return "$aws/things/" + thingName.getName() + "/shadow/#";
    }

    @Override
    public String getGgHost(Region region) {
        return "greengrass-ats.iot." + region.toString() + ".amazonaws.com";
    }

    @Override
    public String getDeviceDefinitionName(GreengrassGroupName greengrassGroupName) {
        return greengrassGroupName.getGroupName() + "_DeviceDefinition";
    }

    @Override
    public String getGgdArchiveName(GreengrassGroupName greengrassGroupName) {
        return String.join("/", ggConstants.getBuildDirectory(), String.join(".", "ggd", greengrassGroupName.getGroupName(), "tar"));
    }

    @Override
    public String getOemArchiveName(GreengrassGroupName greengrassGroupName) {
        return String.join("/", ggConstants.getBuildDirectory(), String.join(".", "oem", greengrassGroupName.getGroupName(), "tar"));
    }

    @Override
    public String getGgShScriptName(GreengrassGroupName greengrassGroupName) {
        return String.join("/", ggConstants.getBuildDirectory(), getBaseGgScriptName(greengrassGroupName));
    }

    @Override
    public String getBaseGgScriptName(GreengrassGroupName greengrassGroupName) {
        return String.join(".", "gg", greengrassGroupName.getGroupName(), "sh");
    }

    @Override
    public Config getFunctionDefaults() {
        return ConfigFactory.parseFile(ggConstants.getFunctionDefaultsConf());
    }

    @Override
    public Config getConnectorDefaults() {
        return ConfigFactory.parseFile(ggConstants.getConnectorDefaultsConf());
    }

    @Override
    public FunctionIsolationMode getDefaultFunctionIsolationMode() {
        boolean greengrassContainer = getFunctionDefaults().getBoolean(ggConstants.getConfGreengrassContainer());

        return (greengrassContainer ? FunctionIsolationMode.GREENGRASS_CONTAINER : FunctionIsolationMode.NO_CONTAINER);
    }
}
