# Notyet Terraria Host

Notyet Terraria Host is an open-source, non-profit Android app that helps turn an Android device into a local Terraria host for friends on the same Wi‑Fi/LAN or via VPN/overlay networks that users already operate.

## Current scope: v0.2

- Android app written in Kotlin and Jetpack Compose.
- Foreground service for server lifetime management.
- Start/Stop controls.
- Local IPv4 address and Terraria port (`7777`).
- In-app logs.
- Clear LAN/VPN messaging and limitations.
- NetworkDetector for LAN and existing VPN/overlay interfaces.
- Detection hints for Tailscale/Headscale, ZeroTier, WireGuard-like, and unknown VPN addresses.
- Copyable connection addresses for detected interfaces.

## Important legal and connectivity limits

This project does **not** include, host, or distribute copyrighted Terraria files. To start a real server, users must provide their own legally obtained compatible Terraria server executable/script at the app files path shown in logs.

v0.2 supports LAN plus address discovery for existing VPN/overlay tools. It does not create a VPN, does not operate paid relay servers, and does not promise connectivity across countries or the public internet. Friends must already be on the same Wi‑Fi/LAN or the same user-managed VPN/overlay network.

## Roadmap

- v0.2: NetworkDetector for LAN/VPN interfaces and copyable addresses. ✅
- v0.3: IPv6 global detection and local port listening checks.
- v0.4: WireGuard helper, key generation, QR code, NAT/CGNAT warnings.
- v0.5: Headscale self-host guide and Tailscale/Headscale IP detection.
- v0.6: NetBird self-host guide and unknown VPN IP detection.
- v0.7: Connection Wizard with clear guarantee levels.
- v0.8: `selfhost/` examples and architecture docs.

## Server file setup for v0.1

Notyet Terraria Host expects a user-provided server launcher at this app-private relative path:

```text
server/TerrariaServer
```

The app logs the full absolute path on first Start if the file is missing. The launcher must be provided by the user and marked executable. This repository intentionally contains no Terraria binaries or protected assets.

## Build

```bash
gradle :app:assembleDebug
```

## Test

```bash
gradle :app:testDebugUnitTest
```
