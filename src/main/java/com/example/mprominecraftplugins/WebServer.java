package com.example.mprominecraftplugins;

import com.example.mprominecraftplugins.handlers.*;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class WebServer extends NanoHTTPD {
    private final Map<String, PageHandler> handlers = new HashMap<>();
    private final Map<String, String> templateCache = new HashMap<>();
    private final JavaPlugin plugin;

    public WebServer(JavaPlugin plugin, int port, String bindIp) throws IOException {
        super(bindIp, port);
        this.plugin = plugin;
        registerHandlers();
    }

    private void registerHandlers() {
        handlers.put("/monitor", new ServerMonitorHandler());
        handlers.put("/online-players", new OnlinePlayersHandler());
        handlers.put("/plugin-list", new PluginListHandler());
        handlers.put("/server-status", new ServerStatusHandler());
        handlers.put("/world-info", new WorldInfoHandler());
    }

    public static WebServer fromConfig(MproMinecraftPlugins plugin) throws IOException {
        FileConfiguration config = plugin.getConfig();
        boolean enabled = config.getBoolean("webserver.enabled", false);

        if (!enabled) {
            throw new IllegalStateException("Web server is disabled in the configuration.");
        }

        int port = config.getInt("webserver.port", 8005);
        String bindIp = config.getString("webserver.bind-ip", "0.0.0.0");

        return new WebServer(plugin, port, bindIp);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        // Handle favicon.ico
        if (uri.equals("/favicon.ico")) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 - Not Found");
        }

        // Serve static files
        if (uri.startsWith("/static/")) {
            return serveStaticFile(uri);
        }

        String header = loadTemplate("header.html");
        String footer = loadTemplate("footer.html");

        // Ensure header and footer are not null
        header = header != null ? header : "<!-- Missing Header -->";
        footer = footer != null ? footer : "<!-- Missing Footer -->";

        // Dynamic rendering for index page
        if (uri.equals("/") || uri.equals("/index")) {
            String content = """
                %s
                <main>
                    <section>%s</section>
                    <section>%s</section>
                    <section>%s</section>
                </main>
                %s
            """.formatted(
                    header,
                    safeHandle(new OnlinePlayersHandler(), session),
                    safeHandle(new ServerStatusHandler(), session),
                    safeHandle(new WorldInfoHandler(), session),
                    footer
            );

            return newFixedLengthResponse(content);
        }

        // Serve individual pages
        PageHandler handler = handlers.get(uri);
        if (handler != null) {
            String content = """
                %s
                <main>
                    %s
                </main>
                %s
            """.formatted(
                    header,
                    safeHandle(handler, session),
                    footer
            );

            return newFixedLengthResponse(content);
        }

        // Log missing handler
        plugin.getLogger().warning("404 - Not Found for URI: " + uri);

        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 - Not Found");
    }

    private Response serveStaticFile(String uri) {
        String resourcePath = "static" + uri.substring(7); // Remove "/static/"
        try (var stream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                plugin.getLogger().warning("Static file not found: " + resourcePath);
                return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 - Not Found");
            }
            byte[] content = stream.readAllBytes();
            String mimeType = getMimeTypeForFile(uri);
            return newFixedLengthResponse(Response.Status.OK, mimeType, new String(content));
        } catch (IOException e) {
            plugin.getLogger().severe("Error serving static file: " + resourcePath);
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "500 - Internal Server Error");
        }
    }

    public static String getMimeTypeForFile(String uri) {
        if (uri.endsWith(".css")) return "text/css";
        if (uri.endsWith(".js")) return "application/javascript";
        if (uri.endsWith(".png")) return "image/png";
        if (uri.endsWith(".jpg") || uri.endsWith(".jpeg")) return "image/jpeg";
        if (uri.endsWith(".html") || uri.endsWith(".htm")) return "text/html";
        return "application/octet-stream";
    }

    private String loadTemplate(String templateName) {
        // Check if the template is already cached
        if (templateCache.containsKey(templateName)) {
            return templateCache.get(templateName);
        }

        try {
            Path path = Path.of("src", "main", "resources", "templates", templateName);
            String content;
            if (Files.exists(path)) {
                content = Files.readString(path);
            } else {
                // Fallback for JAR resources
                try (var stream = getClass().getClassLoader().getResourceAsStream("templates/" + templateName)) {
                    if (stream == null) {
                        plugin.getLogger().warning("Template not found: " + templateName);
                        content = "<!-- Template not found: %s -->".formatted(templateName);
                    } else {
                        content = new String(stream.readAllBytes());
                    }
                }
            }

            // Cache the loaded content
            templateCache.put(templateName, content);
            return content;
        } catch (IOException e) {
            plugin.getLogger().severe("Error loading template: " + templateName);
            e.printStackTrace();
            return "<!-- Failed to load template: %s -->".formatted(templateName);
        }
    }

    private String safeHandle(PageHandler handler, IHTTPSession session) {
        try {
            return handler.handleRequest(session);
        } catch (Exception e) {
            plugin.getLogger().severe("Error handling request: " + e.getMessage());
            e.printStackTrace();
            return "<!-- Error processing request -->";
        }
    }
}
