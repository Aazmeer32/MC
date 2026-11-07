# Spawn Surge (Forge 1.20.1)

Multiplies mob spawns (default x100) for vanilla mobs and mobs from other mods.

## Build
Requires JDK 17 and internet access (to download Forge).

    ./gradlew build        (Windows: gradlew.bat build)

The jar is created at `build/libs/spawnsurge-forge-1.20.1-1.0.0.jar`. Put it in your `mods` folder
(Forge 1.20.1, 47.x). Install it on the server, or on both sides for singleplayer.

## Config
After the first launch edit `config/spawnsurge-common.toml`.
Set `multiplier = 5` for testing.
