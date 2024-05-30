package nsk.enhanced.Methods;

import nsk.enhanced.EnhancedFacilities;

public class PluginInstance {

    private static EnhancedFacilities instance;

    public static EnhancedFacilities getInstance() {
        return instance;
    }

    public static void setInstance(EnhancedFacilities pluginInstance) {
        instance = pluginInstance;
    }

}
