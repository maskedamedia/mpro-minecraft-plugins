package com.example.mprominecraftplugins.handlers;

import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;

/**
 * Handler for displaying server status information.
 */
public class ServerStatusHandler implements PageHandler {

    @Override
    public String handleRequest(NanoHTTPD.IHTTPSession session) {
        // Fetch server information
        String motd = Bukkit.getServer().getMotd();
        int maxPlayers = Bukkit.getServer().getMaxPlayers();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        // Generate the HTML response
        return "<html><body><h2>Server Status</h2>" +
               "<p><strong>MOTD:</strong> " + motd + "</p>" +
               "<p><strong>Players Online:</strong> " + onlinePlayers + "/" + maxPlayers + "</p>" +
               "</body></html>";
    }
}
