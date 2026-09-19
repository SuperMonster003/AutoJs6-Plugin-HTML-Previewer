******

### Release History

******

# v1.1.3

###### 2026/09/19

* `Fix` Status and navigation bar colors follow the rendered page background without waiting for JavaScript to finish loading
* `Fix` SDK XML v4 parsing warnings with AGP 9.1 and APK native alignment checks incorrectly triggered by JVM unit-test assembly tasks, using shared build plugins 1.8.3

# v1.1.2

###### 2026/09/16

* `Improvement` Raise targetSdk to 37 (Android 17) after compileSdk; the plugin's behavior does not depend on the new target

# v1.1.1

###### 2026/09/15

* `Improvement` Raise compileSdk to 37 (Android 17); targetSdk stays at 36 until the behavior that depends on the target is verified

# v1.1.0

###### 2026/09/13

* `Feature` Local release history is available from the interface, with localized text and an English fallback
* `Fix` Static preview and the controlled HTTPS resource client validate and bind public DNS addresses and recheck every redirect. The resource switch controls this client; requests are limited to GET and HEAD.
* `Fix` Enable only for this trusted document. Scripts can use network APIs outside the resource switch, including WebRTC. Every new preview starts with scripts off.
* `Fix` Preserve HTTP 206 and other successful status codes when loading remote resources
* `Improvement` Release packages are checked for a complete signing configuration, exact APK contents and reproducible documentation
* `Dependency` Add OkHttp 4.12.0 for controlled HTTPS resource loading

# v1.0.1

###### 2026/09/11

* `Feature` Scripts are disabled by default. For a trusted, complete ordinary HTML file, enable `Interactive mode` in `Settings` to run JavaScript, button events, WebGL, and local storage. MHTML, truncated HTML, source view, and PDF export always keep scripts disabled.
* `Fix` An issue where enabling the plugin in the plugin center could fail with an error
* `Fix` Fixed primary previewer protocol rejection and settings crashes; synchronized the host appearance, page chrome, and monochrome dialog controls
* `Improvement` Leaner plugin name and description, easier-to-read user documentation
* `Improvement` Build verification rejects accidental native dependencies and produces a JSON report

# v1.0.0

###### 2026/08/06

* `Feature` First release: an `HTML Previewer` menu action for HTML files in the AutoJs6 file manager (plugin ID `html-previewer`)
* `Feature` Secure viewer: scripts, frames, embedded objects, and form submissions are removed before display, fully read-only with JavaScript never executed
* `Feature` Resource loading: supports page styles, local resources next to the file, and HTTPS web images under guarded rules, while all other requests are blocked
* `Feature` Reading experience: automatic light/dark theme, pinch-to-zoom, refresh, fullscreen mode, and a `Start in fullscreen mode` setting
* `Feature` Safety bounds: only the temporary read permission granted by the host is accepted, single files are capped at 8 MB, and file names as well as resource paths are strictly validated
* `Feature` Multilingual: interface, instructions, README, and changelog in 10 languages
