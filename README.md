# JJC Progression

A Forge mod source project for a server-authoritative progression system designed for **Minecraft 1.20.1**, **Forge 47.4.10**, and **Jujutsu Craft 50.1**. Jujutsu Craft must be installed on both the server and connecting clients.

> **Build status:** This source has not yet completed a successful ForgeGradle compile in the environment where it was prepared. A distributable mod JAR is not included. The GitHub Actions workflow in this repository will attempt a build on push; use its result to confirm compilation before installing the JAR on a live server.

## What is included

- Java source for Fame, grades, stat points, stats, CT assignment and tier rolls, curse/reincarnation flow, Prestige, operator commands, server-validated networking, stats screen, and HUD.
- Forge metadata, language resources, Gradle build files, and the Gradle 8.8 wrapper.
- A GitHub Actions workflow that builds with Java 17 and saves `build/libs/*.jar` as a workflow artifact.

Optional integration code recognizes FTB Quests when present. The mod does not replace or provide a server plugin system.

## Requirements

- Minecraft 1.20.1
- Minecraft Forge 47.4.10 (the target used by the Just Adder profile)
- Jujutsu Craft 50.1 on the server and all clients
- Java/JDK 17 to build

The `JJKArai` addon may interact with Jujutsu Craft login, respawn, and CE update code. This combination has not been runtime-tested with this mod. Test on a copy of your server world first and keep backups.

## Build locally

On Windows, open PowerShell in the repository folder and run:

```powershell
.\gradlew.bat --no-daemon build
```

On macOS or Linux:

```sh
./gradlew --no-daemon build
```

The JAR, if the build succeeds, will be at:

```text
build/libs/jjcprogression-0.1.0.jar
```

## Put this project on GitHub

1. Extract the ZIP or open the `JJC-Progression-GitHub` folder.
2. Create an empty GitHub repository.
3. Upload the **contents of this folder**, including `.github`, `gradle`, and the Gradle wrapper files. Do not upload the parent folder as an extra nested layer.
4. Push/commit the files. In the repository's **Actions** tab, open the “Build mod” workflow.
5. If it passes, download the `jjcprogression-jar` artifact from that workflow run. If it fails, open the failed build log; the JAR is not usable until those errors are fixed and a build passes.

## Install after a successful build

Copy the same built JAR to the `mods` folder on the dedicated Forge server and each player's matching Forge 1.20.1 client. Keep the Jujutsu Craft version consistent. Start from a backed-up test world first. The mod creates `config/jjcprogression-common.toml` on first startup.

## Player commands

- `/stats` opens the stats screen. Press **P** to open it as well.
- `/stats upgrade <dmg|health|ce|speed|durability>` spends one Stat Point.
- `/prestige`, then `/prestige confirm` to confirm within the configured time window.

## Operator commands

All `/jjcadmin` commands require operator permission level 2:

- `/jjcadmin fame set|add|remove <player> <amount>`
- `/jjcadmin grade set <player> <grade>`
- `/jjcadmin points set|add|remove <player> <amount>`
- `/jjcadmin stats set|add <player> <stat> <amount>`
- `/jjcadmin stats reset <player>`
- `/jjcadmin ct set <player> <technique>`
- `/jjcadmin ct roll <player> [tier]`
- `/jjcadmin ct clear|info <player>`
- `/jjcadmin prestige set <player> <amount>`
- `/jjcadmin reload` and `/jjcadmin debug`

## Configuration

The server-side common config is generated at `config/jjcprogression-common.toml`. It contains the progression thresholds/rewards, caps and stat scaling, CT tier weights, HUD settings, disabled progression items, optional quest rewards, Prestige settings, and debug logging controls. Stop the server before editing the file, then restart it.

## Scope and known limitations

- Source code and configuration are provided, but **no successful compile or live multiplayer validation has been completed**. Do not treat this as a released or production-verified mod yet.
- The local build attempt could not finish ForgeGradle's Minecraft artifact setup in its restricted environment. GitHub Actions is included as another build route, but its first run still needs to pass before a JAR can be considered build-verified.
- The supplied Just Adder files are a client profile export, not a dedicated server pack. This project does not create a server installation or configure server hosting.
- The profile had no permissions, kits, homes, economy, or `/back` plugin. The project exposes grade and back-related Forge events, but it does not itself supply those plugin commands or rank benefits.
- `/back` command interception is not a substitute for integration with a specific teleport plugin; verify its behavior against the plugin you choose.
- Jujutsu Craft internals used for CT and CE integration can change between versions. This project targets JJC 50.1 and should be re-inspected for newer releases.
- Faction/hostility changes for NPCs and secret task mechanics are not implemented as standalone plugin systems.

## License

See [LICENSE](LICENSE).
