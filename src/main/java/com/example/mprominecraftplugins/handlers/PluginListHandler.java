package com.example.mprominecraftplugins.handlers;

import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Handler for displaying the list of active plugins.
 */
public class PluginListHandler implements PageHandler {

    @Override
    public String handleRequest(NanoHTTPD.IHTTPSession session) {
        // Start HTML table
        StringBuilder response = new StringBuilder("<html><body><h2>Active Plugins</h2>");
        response.append("<table border='1' style='width: 100%; border-collapse: collapse;'>")
                .append("<thead><tr>")
                .append("<th>Name</th><th>Version</th><th>Description</th><th>Enabled</th>")
                .append("</tr></thead><tbody>");

        // Fetch and sort plugin details alphabetically by name
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        Arrays.sort(plugins, Comparator.comparing(Plugin::getName));

        for (Plugin plugin : plugins) {
            response.append("<tr>")
                    .append("<td>").append(plugin.getName()).append("</td>")
                    .append("<td>").append(plugin.getDescription().getVersion()).append("</td>")
                    .append("<td>").append(plugin.getDescription().getDescription()).append("</td>")
                    .append("<td>").append(plugin.isEnabled() ? "Yes" : "No").append("</td>")
                    .append("</tr>");
        }

        response.append("</tbody></table>");
        response.append("</body></html>");
        return response.toString();
    }
}
