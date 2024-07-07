package org.tiathefairyland.tiagrindstoneenhanced.Config;

import org.tiathefairyland.tiagrindstoneenhanced.TiaGrindstoneEnhanced;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigManager {
    private TiaGrindstoneEnhanced plugin;

    public String configVersion = "1.1.0";

    public ConfigManager(TiaGrindstoneEnhanced plugin){
        this.plugin = plugin;
    }

    public void configUpdate(){
        String configVersion = plugin.getConfig().getString("version").replace(".", "");
        int pluginVersion = Integer.parseInt(configVersion.replace(".", "")) ;
        if(Integer.parseInt(configVersion) < pluginVersion){
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            if(configFile.exists()){
                File old = new File(plugin.getDataFolder(), "config-old-" + configVersion + ".yml");
                if(old.exists()){
                    old.delete();
                }
                configFile.renameTo(old);

                plugin.saveDefaultConfig();
                plugin.getLogger().warning(plugin.replacePlaceholder(
                        plugin.getConfig().getString("i18n.consoles.config.update"),
                        new ArrayList<>(Arrays.asList("%version%")),
                        new ArrayList<>(Arrays.asList(configVersion))
                ));

                plugin.reloadConfig();
            }
        }
    }
}
