package com.example.mprominecraftplugins.handlers;

import fi.iki.elonen.NanoHTTPD;

/**
 * Represents a handler for serving a specific page on the web server.
 */
public interface PageHandler {
    /**
     * Handles the request and generates an HTML response.
     *
     * @param session The HTTP session containing request details.
     * @return The HTML content as a String.
     */
    String handleRequest(NanoHTTPD.IHTTPSession session);
}

