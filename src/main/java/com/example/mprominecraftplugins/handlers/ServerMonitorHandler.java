package com.example.mprominecraftplugins.handlers;

import fi.iki.elonen.NanoHTTPD;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

public class ServerMonitorHandler implements PageHandler {

    private final SystemInfo systemInfo;

    public ServerMonitorHandler() {
        this.systemInfo = new SystemInfo();
    }

    @Override
    public String handleRequest(NanoHTTPD.IHTTPSession session) {
        StringBuilder response = new StringBuilder("<html><body>");
        response.append("<h2>Server Monitoring</h2>");

        try {
            // Uptime
            long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
            long uptimeSeconds = uptimeMillis / 1000;
            long hours = uptimeSeconds / 3600;
            long minutes = (uptimeSeconds % 3600) / 60;
            long seconds = uptimeSeconds % 60;
            response.append("<p><strong>Minecraft Server Uptime:</strong> ")
                    .append(hours).append("h ")
                    .append(minutes).append("m ")
                    .append(seconds).append("s</p>");

            // CPU Details
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            long[] prevTicks = processor.getSystemCpuLoadTicks();
            Util.sleep(100);
            long[] ticks = processor.getSystemCpuLoadTicks();
            double cpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;

            int physicalCores = processor.getPhysicalProcessorCount();
            int logicalCores = processor.getLogicalProcessorCount();
            long maxFreq = processor.getMaxFreq();
            long currentFreq = processor.getCurrentFreq()[0];

            response.append(String.format("<p><strong>CPU Usage:</strong> %.2f%%</p>", cpuUsage));
            response.append("<p><strong>Physical Cores:</strong> ").append(physicalCores).append("</p>");
            response.append("<p><strong>Logical Cores:</strong> ").append(logicalCores).append("</p>");
            response.append("<p><strong>Max Frequency:</strong> ").append(formatFrequency(maxFreq)).append("</p>");
            response.append("<p><strong>Current Frequency:</strong> ").append(formatFrequency(currentFreq)).append("</p>");

            // Memory Usage
            GlobalMemory memory = systemInfo.getHardware().getMemory();
            long totalMemory = memory.getTotal();
            long availableMemory = memory.getAvailable();
            long usedMemory = totalMemory - availableMemory;
            response.append("<p><strong>Memory Usage:</strong> ")
                    .append(formatSize(usedMemory)).append(" / ").append(formatSize(totalMemory)).append("</p>");

            // Disk Usage
            response.append("<h3>Disk Usage</h3><table border='1' style='width: 100%; border-collapse: collapse;'>");
            response.append("<thead><tr><th>Drive</th><th>Total</th><th>Used</th><th>Available</th></tr></thead><tbody>");

            OperatingSystem os = systemInfo.getOperatingSystem();
            List<OSFileStore> fileStores = os.getFileSystem().getFileStores();
            Set<String> uniqueFileSystems = new HashSet<>();

            for (OSFileStore store : fileStores) {
                // Create a unique identifier combining volume and mount point
                String uniqueId = store.getVolume() + ":" + store.getMount();
                if (!uniqueFileSystems.add(uniqueId)) {
                    continue; // Skip if already added
                }

                long totalSpace = store.getTotalSpace();
                long usableSpace = store.getUsableSpace();
                long usedSpace = totalSpace - usableSpace;

                response.append("<tr><td>").append(store.getMount()).append("</td>")
                        .append("<td>").append(formatSize(totalSpace)).append("</td>")
                        .append("<td>").append(formatSize(usedSpace)).append("</td>")
                        .append("<td>").append(formatSize(usableSpace)).append("</td></tr>");
            }
            response.append("</tbody></table>");
        } catch (Exception e) {
            response.append("<p style='color: red;'>Error fetching server monitoring data: ").append(e.getMessage()).append("</p>");
            e.printStackTrace();
        }

        response.append("</body></html>");
        return response.toString();
    }

    private String formatSize(long bytes) {
        double size = bytes;
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    private String formatFrequency(long hertz) {
        if (hertz >= 1_000_000_000) {
            return String.format("%.2f GHz", hertz / 1_000_000_000.0);
        } else if (hertz >= 1_000_000) {
            return String.format("%.2f MHz", hertz / 1_000_000.0);
        } else {
            return String.format("%d Hz", hertz);
        }
    }
}
