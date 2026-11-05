# Magic Box Initial Specification

## Goal and Scope

Build an offline Android host application that lets users import and manage independent web-based functions. The host includes plugins from the project directory `plugins/built-in plugins` for automatic initialization. Follow Acode's local plugin approach: develop each function independently and deliver it as a single importable package.

# APK Development Specifications

## General Specifications

- Name the application Magic Box and support Android 10 (API 29) and later
- Use the project artwork `assets/magic-box-icon.jpg` as the application launcher icon, with Android launcher resources generated from that artwork
- Use English interface text and require no account or network connection
- Provide Plugins, Search, and Settings tabs in that order in the bottom navigation, with Search selected at startup
- Reserve the top and bottom 10% of screen height as background-only areas in the host and plugin runtime, including fullscreen; place content and controls in the remaining area. Keep system bars hidden while Magic Box is active; the system manages them after leaving the app. System gestures may temporarily reveal system bars.
- Center each tab's main content within the available area, including the search field and settings actions; keep longer content scrollable
- Discover plugin directories under `plugins/built-in plugins` at build time, bundle them with the APK, and initialize them when the application opens, including after an in-place upgrade; discover bundled plugins without a hard-coded list of plugin names or IDs
- For each bundled plugin not yet initialized, install it only when no plugin with the same ID is already installed; mark it initialized after successful installation or after detecting an existing plugin with that ID
- Preserve existing plugins, their enabled states, and their data; do not automatically replace installed plugins with bundled copies during APK updates
- Retain each bundled plugin's initialization record after the user uninstalls it, so later application starts and APK updates do not automatically reinstall it; allow manual import
- If a bundled plugin fails to install, leave it uninitialized for retry on a later application start and allow the application to continue opening
- Build bundled packages from the plugin source files and validate each manifest, entry point, and plugin ID; fail the build for invalid plugins or duplicate IDs
- Require bundled plugins to include a description and declare `developer: "Official"` and `official: true` in their manifests; the official badge remains based on the package declaration
- Require each bundled plugin to declare an `icon` pointing to a readable PNG image inside its package; reject missing or invalid icons during the build
- Accept older packages without card metadata, using a default icon and placeholder text; identify updates by the plugin's unique identifier
- Enable newly installed and reinstalled functions by default; preserve the enabled state when updating an installed function
- Make a successfully imported or enabled function available without reinstalling or restarting the host
- Close a running function before applying its update, disabling it, or removing it
- Allow users to return from a function to the host

## Tab Specifications

### Plugins

- Show all installed plugins on Plugins in A–Z name order, including disabled plugins with their state; allow only enabled plugins to open
- Place an Import button at the upper right of Plugins; its dialog offers Import plugin from market, which displays https://github.com/hiSean0909/Android-Magic-Box-Plugins, and Import plugin from local, which opens the system file picker
- In the Plugins > Import > Import plugin from market URL dialog, place lowercase copy and close buttons in that order at the bottom right; copy places the plugin repository address on the clipboard and confirms the action, and close dismisses the dialog
- Let users browse accessible phone storage directories through the Android file picker, select a function ZIP package, and import it into application storage
- Validate package metadata, entry point, and host interface compatibility before installation; leave installed functions unchanged if installation fails
- For a local import with an installed plugin's ID, compare the declared plugin version strings for equality; if they match, show a dialog and leave the installed plugin unchanged
- If the plugin version strings differ, show the installed and incoming versions in a dialog and let the user confirm replacement or cancel; leave the installed plugin unchanged on cancel
- Record the plugin standard version supported by the APK, initially 1.0; before a local import, compare it with the plugin declaration numerically by major and minor version
- Attempt installation regardless of the standard version comparison; retain existing package and host interface checks
- On successful import, show `import plugin succeed`
- On failed import, show `import plugin failed! please update the plugin` if the declared standard version is lower than the supported version, or `import plugin failed! please update the software` if it is higher; show `import plugin failed! please check plugin again` if versions match or the plugin version is missing, unreadable, or invalid

### Search

- Show only a search field initially on Search; after input, match enabled and disabled plugins by name or description without regard to English letter case, list enabled plugins before disabled plugins, prioritize name matches over description-only matches within each state group, and allow only enabled plugins to open

### Settings

- Place the About action on the Settings tab
- In the Settings > About dialog, show the project GitHub address, https://github.com/hiSean0909/Android-Magic-Box, and place lowercase copy and close buttons in that order at the bottom right; copy places the address on the clipboard and confirms the action without requiring network access

## Cross-Tab Specifications

### Plugin Card

- Show a plugin card in Plugins and Search with an icon on the left, name, description, and developer on the right, an official badge when declared by the package, and a settings button at the lower right
- Display the card description on one line, truncating overflow with an ellipsis
- Size the icon area as a square matching the text area height, align their top and bottom edges, and center the image without changing its aspect ratio
- Show the settings action as a gear without a gray background or shadow; make its height match the developer row and open the plugin settings menu when tapped
- Show plugin card settings in this order: Enable, Disable, Uninstall, Export, About
- In each plugin card settings menu, always show Enable and Disable; disable the action that matches the current state and allow the other action
- Provide About in each plugin card settings menu, showing the plugin name, its full description, and then its declared plugin version; use `No description` when the description is absent
- Let the card settings uninstall the plugin after confirmation; keep settings available for disabled plugins
- Provide an Export action in each plugin card's settings, including for disabled and bundled plugins
- On Export, open the Android system save dialog with `<plugin-id>.zip` as the suggested file name and allow the user to choose the destination and name
- Create the ZIP from the currently installed plugin files, placing the manifest, entry point, and resources at their existing package paths; exclude runtime settings and game data
- Allow an exported ZIP to be imported through Import plugin from local; report export success or failure and preserve the installed plugin

### Dialog Standards

#### Copy and Close

- Show lowercase `copy` and `close` buttons in that order at the bottom right
- Copy the content specified by the dialog and confirm the action; close dismisses the dialog

#### Close

- Show a lowercase `close` button at the bottom right to dismiss the dialog

#### Confirm and Cancel

- Show a lowercase `confirm` button at the bottom left and a lowercase `cancel` button at the bottom right
- Confirm performs the action described in the dialog; cancel dismisses the dialog without performing it

#### Dialog Scenarios

| Scenario | Standard |
| --- | --- |
| Settings > About | Copy and Close |
| Plugins > Import > Import plugin from market | Copy and Close |
| Plugin card > About | Close |
| Local import with the same plugin ID and version | Close |
| Local import with the same plugin ID and a different version | Confirm and Cancel |
| Plugin card > Uninstall | Confirm and Cancel |

- The import menu and plugin settings menu are selection dialogs and do not use these button layouts
- Android system file picker and save dialogs are controlled by the system and do not use these button layouts

# Plugin Development Specifications

## Function Package and Runtime

- Deliver each function as a single ZIP package containing metadata, an HTML entry point, JavaScript, and any other resources required for offline operation
- Metadata identifies the function, its name, category, version, entry point, and required host interface version; optional card metadata supplies an icon, description, developer, and self-declared official status
- New plugins declare the plugin development standard they follow in `pluginStandardVersion`, independently of the plugin version and `hostApi`; the initial standard version is 1.0
- Retain a declared plugin standard version and continue to attempt installation of existing packages without it; do not reject a package solely based on this field, and continue to check `hostApi` compatibility
- Retain published plugin standard versions as separate documents and record changes; increment the minor version for backward-compatible additions or clarifications and the major version for incompatible changes
- Run function pages locally and expose only the host capabilities required by the function
- Provide screen measurement and persistent settings capabilities for Ruler through a defined host interface
- Provide a defined host interface for a function to enter and exit fullscreen display; keep system bars hidden in both modes while Magic Box is active
- Require host interface version 2 for functions using fullscreen; continue to accept version 1 functions
- Isolate function settings by function identifier and preserve them across application restarts
- Preserve installed functions, their enabled states, and their settings when the host is updated in place
- Handle initialization and cleanup when opening and closing a function
- Accept packages from trusted sources; do not grant functions unrestricted access to host capabilities

# Delivery and Acceptance

- Keep the application ID and signing certificate consistent across APK deliveries so a newer version can update the installed application in place
- Accept the product when the user explicitly confirms acceptance; the user chooses how to assess it.

# Appendix

## Current Search Strategy

### Preprocessing

- Trim leading and trailing whitespace from the search input
- Match text without regard to case

### Similarity Calculation

- Use substring matching on plugin names and descriptions; do not calculate a fuzzy similarity score
- Classify a plugin as a name match when its name contains the input, even if its description also matches; otherwise classify it as a description match when only its description contains the input

### Sorting

- List enabled plugins before disabled plugins
- Within each state group, list name matches before description-only matches
- Within each match category, sort by name without regard to case

### Boundary Handling

- Show no results while the trimmed input is empty
- When a description is missing, match only the plugin name
- Show `No plugins found.` when a nonempty search has no matches

## Plugin Development Specifications

The highest-numbered plugin development standard is effective, whether it is a dev or release document. The current standard is [Plugin Development Guide v1.0](<plugins/plugin development standards/PLUGIN_DEVELOPMENT_v1.0.md>).
