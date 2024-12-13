package com.example.mprominecraftplugins.handlers;

import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

/**
 * Handler for displaying online players and their details in a table format.
 */
public class OnlinePlayersHandler implements PageHandler {

    @Override
    public String handleRequest(NanoHTTPD.IHTTPSession session) {
        // Get the list of online players
        var onlinePlayers = Bukkit.getOnlinePlayers();
        var maxPlayers = Bukkit.getServer().getMaxPlayers();

        // Count of online players
        String playerCount = String.format("<h2>Online Players: %d/%d</h2>", onlinePlayers.size(), maxPlayers);

        // Check if there are any online players
        if (onlinePlayers.isEmpty()) {
            return playerCount + "<p>No players are currently online.</p>";
        }

        // Generate an HTML table with player details
        String tableHeader = """
            <table border='1' style='width: 100%; border-collapse: collapse; text-align: center;'>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Health</th>
                        <th>Location</th>
                    </tr>
                </thead>
                <tbody>
        """;

        String tableBody = onlinePlayers.stream()
                .map(player -> {
                    Location loc = player.getLocation();
                    return String.format("""
                        <tr>
                            <td>%s</td>
                            <td>%.1f</td>
                            <td>(X: %.2f, Y: %.2f, Z: %.2f)</td>
                        </tr>
                    """,
                            player.getName(),
                            player.getHealth(),
                            loc.getX(),
                            loc.getY(),
                            loc.getZ());
                })
                .collect(Collectors.joining());

        String tableFooter = "</tbody></table>";

        // Combine all parts of the HTML
        return playerCount + tableHeader + tableBody + tableFooter;
    }
}
