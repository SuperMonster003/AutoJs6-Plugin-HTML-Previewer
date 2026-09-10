******

### Release History

******

# v1.0.1

###### 2026/08/08

* `Feature` Scripts are disabled by default. For a trusted, complete ordinary HTML file, enable `Interactive mode` in `Settings` to run JavaScript, button events, WebGL, and local storage. MHTML, truncated HTML, source view, and PDF export always keep scripts disabled.
* `Fix` An issue where enabling the plugin in the plugin center could fail with an error
* `Fix` Fixed primary previewer protocol rejection and settings crashes; synchronized the host appearance, page chrome, and monochrome dialog controls
* `Improvement` Leaner plugin name and description, easier-to-read user documentation

# v1.0.0

###### 2026/08/06

* `Feature` First release: an `HTML Previewer` menu action for HTML files in the AutoJs6 file manager (plugin ID `html-previewer`)
* `Feature` Secure viewer: scripts, frames, embedded objects, and form submissions are removed before display, fully read-only with JavaScript never executed
* `Feature` Resource loading: supports page styles, local resources next to the file, and HTTPS web images under guarded rules, while all other requests are blocked
* `Feature` Reading experience: automatic light/dark theme, pinch-to-zoom, refresh, fullscreen mode, and a `Start in fullscreen mode` setting
* `Feature` Safety bounds: only the temporary read permission granted by the host is accepted, single files are capped at 8 MB, and file names as well as resource paths are strictly validated
* `Feature` Multilingual: interface, instructions, README, and changelog in 10 languages
