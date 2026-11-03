# Magic Box Initial Specification

## Goal and Scope

Build an offline Android host application that lets users import and manage independent web-based functions. The host starts without preinstalled functions. Follow Acode's local plugin approach: develop each function independently and deliver it as a single importable package.

## Host Application

- Name the application Magic Box and support Android 10 (API 29) and later
- Use English interface text and require no account or network connection
- Provide Plugins, Search, and Settings tabs in that order in the bottom navigation, with Search selected at startup
- Place the About action on the Settings tab
- In the Settings > About dialog, show the project GitHub address, https://github.com/hiSean0909/Android-Magic-Box, and place lowercase copy and close buttons in that order at the bottom right; copy places the address on the clipboard and confirms the action without requiring network access
- Show all installed plugins on Plugins in A–Z name order, including disabled plugins with their state; allow only enabled plugins to open
- Show only a search field initially on Search; after input, match enabled plugin names without regard to English letter case and allow opening results
- Place an Import button at the upper right of Plugins; its dialog offers Import plugin from market, which displays https://github.com/hiSean0909/Android-Magic-Box-Plugins, and Import plugin from local, which opens the system file picker
- In the Plugins > Import > Import plugin from market URL dialog, place lowercase copy and close buttons in that order at the bottom right; copy places the plugin repository address on the clipboard and confirms the action, and close dismisses the dialog
- Reserve the top and bottom 10% of screen height as background-only areas in the host and plugin runtime, including fullscreen; place content and controls in the remaining area. Keep system bars hidden while Magic Box is active; the system manages them after leaving the app. System gestures may temporarily reveal system bars.
- Center each tab's main content within the available area, including the search field and settings actions; keep longer content scrollable
- Let users browse accessible phone storage directories through the Android file picker, select a function ZIP package, and import it into application storage
- Validate package metadata, entry point, and host interface compatibility before installation; report invalid or incompatible packages without changing installed functions
- Show a plugin card in Plugins and Search with an icon on the left, name, description, and developer on the right, an official badge when declared by the package, and a settings button at the lower right
- Let the card settings enable or disable the plugin and uninstall it after confirmation; keep settings available for disabled plugins
- Accept older packages without card metadata, using a default icon and placeholder text; identify updates by the plugin's unique identifier
- Enable newly installed and reinstalled functions by default; preserve the enabled state when updating an installed function
- Make a successfully imported or enabled function available without reinstalling or restarting the host
- Close a running function before applying its update, disabling it, or removing it
- Allow users to return from a function to the host

## Function Package and Runtime

- Deliver each function as a single ZIP package containing metadata, an HTML entry point, JavaScript, and any other resources required for offline operation
- Metadata identifies the function, its name, category, version, entry point, and required host interface version; optional card metadata supplies an icon, description, developer, and self-declared official status
- Run function pages locally and expose only the host capabilities required by the function
- Provide screen measurement and persistent settings capabilities for Ruler through a defined host interface
- Provide a defined host interface for a function to enter and exit fullscreen display; keep system bars hidden in both modes while Magic Box is active
- Require host interface version 2 for functions using fullscreen; continue to accept version 1 functions
- Isolate function settings by function identifier and preserve them across application restarts
- Preserve installed functions, their enabled states, and their settings when the host is updated in place
- Handle initialization and cleanup when opening and closing a function
- Accept packages from trusted sources; do not grant functions unrestricted access to host capabilities

## Delivery and Acceptance

- Keep the application ID and signing certificate consistent across APK deliveries so a newer version can update the installed application in place
- Accept the product when the user explicitly confirms acceptance; the user chooses how to assess it.
