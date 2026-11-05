# Magic Box

## About

Magic Box is an offline Android app for installing and managing plugins. It includes built-in plugins.

## Install

Download the latest release APK from the `release` folder and install it on an Android 10 or later device. Install a newer compatible APK over the existing app to keep imported plugins, their enabled states, and saved settings. Uninstalling the app or clearing its data can remove them.

The current release includes the features described below.

## Use Plugins

1. Open **Plugins > Import > Import plugin from local** and select a plugin ZIP file.
2. Find plugins under **Plugins** or by name in **Search**, then tap an enabled plugin to open it.
3. Use the settings button on a plugin card to enable, disable, or uninstall it.

Newly installed and reinstalled plugins are enabled by default. When importing a plugin with an installed ID, matching versions are left unchanged. For different versions, review both versions and confirm replacement or cancel. Replacement preserves the enabled state and can install an older version; older versions may not be compatible with saved data.

## Get Plugins

- Download plugins from the [official plugin repository](https://github.com/hiSean0909/Android-Magic-Box-Plugins), then import them manually.
- Create your own plugins using the [plugin development guide](<plugins/plugin development standards/PLUGIN_DEVELOPMENT_v1.0.md>).

## About the App

Open **Settings > About** to view and copy the Magic Box GitHub address.

## Built-in Plugins

The app bundles plugins from `plugins/built-in plugins` at build time. On opening the app, it installs each bundled plugin once unless a plugin with the same ID already exists. Uninstalling a bundled plugin keeps it uninstalled across app restarts and updates; it can still be imported manually. Existing plugins and their settings are preserved.

Use **Export** in a plugin card's settings to save its installed files as an importable ZIP. Runtime settings and game data are not included.

Plugin cards show a single-line description, a square icon area aligned with the text area, and a background-free settings gear aligned with the developer row. Their settings menu lists **Enable**, **Disable**, **Uninstall**, **Export**, and **About** in that order; the action for the current state is unavailable. **About** shows the full description and plugin version. Bundled plugin cards include a PNG icon, a description, Official as the developer, and a self-declared official badge.

The application launcher artwork is maintained in `assets/magic-box-icon.jpg`. The Android resource is generated from it during the build.

Search matches plugin names and descriptions without regard to case. It lists enabled plugins first, then disabled plugins, with name matches before description-only matches within each group. Disabled plugins cannot be opened.
