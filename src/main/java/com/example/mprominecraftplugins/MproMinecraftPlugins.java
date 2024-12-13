package com.example.mprominecraftplugins;

import org.bukkit.plugin.java.JavaPlugin;

public class MproMinecraftPlugins extends JavaPlugin {
    private WebServer webServer;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            webServer = WebServer.fromConfig(this);
            webServer.start();
            getLogger().info("Web server started.");
        } catch (Exception e) {
            getLogger().severe("Failed to start web server: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        if (webServer != null) {
            webServer.stop();
            getLogger().info("Web server stopped.");
        }
    }
}
