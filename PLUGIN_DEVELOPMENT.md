# Plugin Development Guide

Magic Box runs plugins as local web pages. Each plugin is distributed as a ZIP file containing its metadata, HTML entry point, scripts, styles, and other resources needed offline. Import packages only from trusted sources.

## Create a Plugin

Place `manifest.json` at the root of the ZIP. For example:

```text
manifest.json
index.html
```

Use a manifest such as:

```json
{
  "id": "hello-magic-box",
  "name": "Hello Magic Box",
  "category": "tool",
  "version": "1.0.0",
  "entry": "index.html",
  "hostApi": 1,
  "icon": "icon.png",
  "description": "A short description of the plugin.",
  "developer": "Example Developer",
  "official": false
}
```

| Field | Requirement |
| --- | --- |
| `id` | Unique identifier, 1–64 characters, starting with a lowercase ASCII letter or digit and containing only lowercase ASCII letters, digits, and hyphens |
| `name` | Nonempty display name |
| `category` | `tool` or `game` |
| `version` | Nonempty version string |
| `entry` | Path to an existing `.html` file inside the package |
| `hostApi` | Integer `1` or `2`; use `2` for fullscreen |
| `icon` | Optional path to an image inside the package for the plugin card |
| `description` | Optional short plugin description displayed on the card |
| `developer` | Optional developer name displayed on the card |
| `official` | Optional boolean; `true` displays the official badge |

The host uses a default icon and placeholder text when optional card metadata is absent. The official badge is based solely on the package declaration and is not independently verified.

The host identifies updates by `id`, so keep it stable across versions and use a new ID for a separate plugin. The host accepts up to 200 ZIP entries and 10,000,000 bytes of total uncompressed file content. The manifest is limited to 2,000,000 bytes. Package paths must stay inside the ZIP's root directory.

A minimal `index.html`:

```html
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Hello Magic Box</title>
</head>
<body>
  <h1>Hello Magic Box</h1>
  <p>This plugin runs offline.</p>
</body>
</html>
```

ZIP the contents of the plugin directory, not the directory itself, so that `manifest.json` is at the ZIP root. Copy the ZIP to the phone, open Magic Box, and select **Plugins > Import > Import plugin from local** to choose it. Use **Plugins** or **Search** to open the plugin. Use the settings button on its card to enable, disable, or uninstall it. Import a package with the same ID to update an installed plugin.

## Runtime and Host Interface

The host reserves the top and bottom 10% of screen height as background-only areas, including in fullscreen. Design plugin pages so that text, controls, icons, and other content remain in the middle 80%.

Bundle all resources locally and use relative paths. The host serves the plugin under an origin derived from its ID, `https://<id>.magicbox.local/`; this is a local WebView origin, not a network website. External navigation and resource requests are blocked. The host currently assigns HTML, JavaScript, and CSS MIME types based on `.html`, `.js`, and `.css` extensions; other extensions use `application/octet-stream`.

JavaScript can call these methods on `MagicBox`:

- `MagicBox.screenDpi()` returns the Android display metrics' vertical DPI (`ydpi`), useful as an initial measurement value; physical measurements may require user calibration.
- `MagicBox.density()` returns the Android display density scale.
- `MagicBox.fullscreen(true)` enters fullscreen and `MagicBox.fullscreen(false)` exits it. Declare `hostApi: 2` when using fullscreen.

The host dispatches `magicboxpause` on `window` when the activity pauses. While fullscreen, pressing Back dispatches `magicboxback` and exits fullscreen. A plugin may listen for these events to pause work or update its interface. On reopening, initialize the page again; the host destroys the WebView when returning to its home screen.

WebView DOM storage is enabled. A plugin can use `localStorage` for persistent settings; each plugin ID has its own origin. Preserve the ID to retain access to settings across plugin updates. Test persistence on the target device. The host stores plugin files and WebView data in its private application data; uninstalling Magic Box or clearing its data can remove them.

## Verify a Package

1. Import the ZIP on the target phone and confirm the plugin appears and opens offline.
2. Exercise the plugin's functions and any host interfaces it uses, including fullscreen and Back behavior where applicable. Verify that the top and bottom 10% of the screen contain only background.
3. Check that settings survive closing and reopening the plugin, if it stores settings.
4. Import an updated ZIP with the same ID and check that its state and settings remain available.
5. Check the card display and its enable, disable, and uninstall actions. Test physical measurement and calibration separately if applicable.
