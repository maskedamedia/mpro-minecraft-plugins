# mpro-minecraft-plugins

## Overview

pro-minecraft-plugins is a Minecraft plugin designed to provide a web-based
dashboard for monitoring your Minecraft server. The plugin utilizes the
NanoHTTPD library to host a lightweight web server, displaying real-time server
statistics such as player information, CPU usage, memory usage, and disk usage.
In future updates, the plugin will be expanded with additional features
beyond server management and monitoring.

---

## Features

- **Server Monitor**
  - Displays server uptime
  - CPU details, including usage, physical/logical cores, and frequencies
  - Memory usage (used/total)
  - Disk usage per filesystem

- **Player Information**
  - Displays online players with details (name, health, location)

- **World Information**
  - Provides information about loaded chunks and last update times for each world

- **Plugin List**
  - Lists installed plugins with details (name, version, description, and whether
  they are enabled)

---

### Prerequisites

- [Java 21+](https://adoptium.net/temurin/releases/)
- [Apache Maven](https://maven.apache.org/)

---

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/maskedamedia/mpro-minecraft-plugins.git
   ```

2. Build the plugin:

   ```bash
   mvn clean package
   ```

3. Copy the generated JAR file (`target/MproMinecraftPlugins-1.0-ALPHA-shaded.jar`)
to your server's `plugins` directory.

4. Start your Minecraft server.

5. Access the web dashboard at:

   ```bash
   http://<server-ip>:<configured-port>
   ```

   Default port: 8005 (configurable via `config.yml`).

---

## Configuration

Edit the `config.yml` file located in the plugin's `resources` folder:

```yaml
webserver:
  enabled: true
  port: 8005
  bind-ip: 0.0.0.0
```

---

## Future Features

- Enhanced player statistics and tracking
- Server performance optimization insights
- Integration with third-party analytics tools
- Customizable web dashboard themes
- Notifications for server events

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Support

For support or feature requests,
[open an issue](https://github.com/maskedamedia/mpro-minecraft-plugins/issues)
in the repository or contact the maintainers at `info@maskeda.com`.

---

## Acknowledgments

- **NanoHTTPD**: Lightweight HTTP server
- **OSHI**: Operating system and hardware information library
- **Bukkit/Spigot API**: Minecraft server plugin development framework

---

![Java](https://img.shields.io/badge/Java-21%2B-blue)
![License](https://img.shields.io/github/license/maskedamedia/mpro-minecraft-plugins)
![GitHub release](https://img.shields.io/github/v/release/maskedamedia/mpro-minecraft-plugins)
![Downloads](https://img.shields.io/github/downloads/maskedamedia/mpro-minecraft-plugins/total)
