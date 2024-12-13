package com.example.mprominecraftplugins.handlers;

import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Handler for displaying information about the server's worlds.
 */
public class WorldInfoHandler implements PageHandler {

    @Override
    public String handleRequest(NanoHTTPD.IHTTPSession session) {
        // DateTimeFormatter for GMT time display
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss 'GMT'")
                .withZone(ZoneId.of("UTC"));

        // Gather world information
        String worldInfoTable = Bukkit.getWorlds().stream()
                .map(world -> {
                    long time = world.getTime();
                    int loadedChunks = world.getLoadedChunks().length;
                    String formattedTime = timeFormatter.format(Instant.ofEpochSecond(time));
                    return """
                            <tr>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%d</td>
                            </tr>
                            """.formatted(world.getName(), formattedTime, loadedChunks);
                })
                .collect(Collectors.joining());

        // Generate the HTML response
        return """
            <html>
                <head>
                    <title>World Information</title>
                </head>
                <body>
                    <h2>World Information</h2>
                    <table border="1" style="width: 100%%; text-align: left;">
                        <thead>
                            <tr>
                                <th>World Name</th>
                                <th>Last Update Time (GMT)</th>
                                <th>Loaded Chunks</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>
                </body>
            </html>
            """.formatted(worldInfoTable);
    }
}
