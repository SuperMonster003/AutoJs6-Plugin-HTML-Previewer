<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-html-preview-ic-launcher" border="0" width="128" />
  </p>

  <p>Complemento seguro de vista previa de HTML para el explorador AutoJs6</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-HTML-Preview?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-HTML-Preview?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-HTML-Preview?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Idiomas (Languages)

******

El README.md actual admite los siguientes idiomas:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-fr.md)
- Español [es] # actual
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ar.md)

******

### Introducción

******

El complemento AutoJs6 HTML Preview añade una acción de vista previa de HTML de solo lectura para un archivo al explorador AutoJs6. Sanea y muestra el contenido web en un visor dedicado sin integrar la implementación en la aplicación anfitriona.

******

### Funciones

******

- Registra una acción de explorador de solo lectura para un archivo mediante el protocolo compartido `org.autojs.plugin.EXPLORER_ACTION`.
- Recibe acceso temporal de lectura mediante URI de contenido al archivo HTML seleccionado y a los recursos de su directorio principal, sin rutas directas del sistema de archivos.
- Elimina scripts, atributos de controladores de eventos, marcos, objetos incrustados y direcciones de recursos no seguras antes de mostrar el contenido.
- Carga recursos relativos, recursos data e imágenes HTTPS permitidas mediante reglas de solicitud controladas.
- Ofrece un estilo legible fijo, actualización, modo de pantalla completa, una preferencia de inicio en pantalla completa y un tamaño de entrada HTML limitado.

******

### Formatos compatibles

******

La primera versión reconoce las siguientes extensiones de archivo:

```text
html, htm, shtm, shtml, xht, xhtml
```

******

### Interfaz del complemento

******

AutoJs6 descubre y ejecuta el complemento con las siguientes identidades:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: html-preview
engine: explorer-action
variant: default
```

La versión 1 se limita a una acción secundaria de solo lectura para un archivo en el explorador AutoJs6 principal.

******

### Seguridad

******

El visor sanea el documento, desactiva JavaScript, el almacenamiento WebView, las cookies y el acceso directo a archivos, limita los recursos y la navegación mediante CSP y reglas URI, y solo acepta permisos temporales otorgados por el anfitrión.

******

### Historial de versiones

******

# v1.0.0

###### 2026/08/06

* `Función` Complemento HTML Preview con ID `html-preview`, motor `explorer-action` y variante `default`
* `Función` Acción secundaria de solo lectura para un archivo en el explorador AutoJs6 principal mediante `org.autojs.plugin.EXPLORER_ACTION`
* `Función` Ejecución mediante `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` con acceso temporal de lectura a los URI de contenido del archivo y del directorio principal
* `Función` Saneamiento HTML que elimina scripts, atributos de controladores de eventos, marcos, objetos incrustados y direcciones de recursos no seguras antes de mostrar
* `Función` Carga controlada de recursos relativos, recursos data e imágenes HTTPS permitidas con estilo fijo, actualización y pantalla completa
* `Función` Política WebView reforzada con CSP, navegación URI controlada, JavaScript y almacenamiento desactivados y entrada limitada
* `Función` Metadatos, interfaz, instrucciones, README y changelog localizados en español, francés, ruso, árabe, japonés, coreano, inglés, chino simplificado, chino tradicional de Hong Kong y chino tradicional de Taiwán

##### Para consultar más historial de versiones

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/app/src/main/assets/doc/CHANGELOG-es.md)

******

### Compilación

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Compilación Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

Los parámetros de compilación provienen de `version.properties`. El SDK mínimo actual es 24 y el SDK de destino es 36.

******

### Estructura de recursos

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localiza los metadatos del complemento y la interfaz, mientras que `plugin_instruction.md` proporciona instrucciones visibles para el anfitrión. Los archivos README y changelog se generan desde fuentes JSON mediante `.python/generate_markdown.py`.

******

### Enlaces

******

- Documentación de AutoJs6: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org
