# Architecture

The app is prepared for clean growth across the v0.1-v0.8 roadmap.

- `domain`: pure Kotlin models and logic such as server state, install checks, IPv4 selection, and v0.2 network detection rules.
- `data`: Android adapters and process runners, including `AndroidNetworkInterfaceProvider` and `TerrariaServerRunner`.
- `service`: Android foreground service that owns long-running server lifecycle and refreshes detected connection addresses when hosting starts.
- `ui`: Jetpack Compose screens and theme.

v0.2 keeps hosting user-managed: the app can show LAN and existing VPN/overlay addresses, but it does not create VPN accounts, run paid relay servers, or bundle Terraria server files.
