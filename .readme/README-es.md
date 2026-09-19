<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="html-previewer-ic-launcher" border="0" width="128" />
  </p>

  <p>Previsualiza archivos HTML y MHTML</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Idiomas (Languages)

******

El README.md actual admite los siguientes idiomas:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-fr.md)
- Español [es] # actual
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ar.md)

******

### Introducción

******

Vista previa al instante: abra archivos web directamente desde el gestor de archivos mediante el menú `Vista previa de HTML`, sin navegador y sin red para páginas puramente locales.

Los scripts están desactivados por defecto. Para un HTML normal completo y de confianza, active el modo interactivo en ajustes para JavaScript, botones, WebGL y almacenamiento local. MHTML, HTML truncado, código fuente y exportación PDF siempre desactivan los scripts.

******

### Funciones destacadas

******

- Vista previa al instante: abra archivos web directamente desde el gestor de archivos mediante el menú `Vista previa de HTML`, sin navegador y sin red para páginas puramente locales.
- Los scripts están desactivados por defecto. Para un HTML normal completo y de confianza, active el modo interactivo en ajustes para JavaScript, botones, WebGL y almacenamiento local. MHTML, HTML truncado, código fuente y exportación PDF siempre desactivan los scripts.
- Vista de código segura: alterne entre la vista previa saneada y el HTML original de solo lectura, o el HTML raíz decodificado de MHTML; las etiquetas siguen siendo texto literal y este modo no realiza solicitudes de red.
- Exportación PDF del sistema: elija `Exportar PDF` para enviar una copia recién saneada y con tema claro al panel de impresión de Android; el sistema gestiona la configuración de páginas y `Guardar como PDF` sin permiso de almacenamiento.
- Maquetación fiel: admite los estilos y recursos permitidos incluidos en MHTML, los recursos locales junto al HTML normal (imágenes, fuentes, audio, vídeo) y las imágenes web HTTPS opcionales, que pueden desactivarse por completo.
- Lectura cómoda: incluye búsqueda en la página, texto del 75%-200%, zoom con dos dedos y temas de AutoJs6, claro u oscuro. Los menús y diálogos siguen el idioma y modo oscuro de AutoJs6, al igual que GitHub (Auto) y el tema HTML automático. Las barras usan un color representativo de los bordes de la página y texto e iconos negros o blancos con contraste. Para degradados, imágenes y animaciones, el color permanece fijo hasta recargar para evitar parpadeos.
- Pantalla completa: cambie en cualquier momento a pantalla completa inmersiva o active `Iniciar en modo de pantalla completa` en la configuración.
- Enlaces externos: al tocar un enlace http/https se delega en el navegador del sistema, mientras que los saltos a anclas internas siguen funcionando.
- Multilingüe: interfaz, instrucciones, README y changelog disponibles en 10 idiomas.

******

### Cómo se usa

******

1. Descargue el APK más reciente del complemento desde la página [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases) e instálelo en el dispositivo.
2. Abra el centro de complementos de AutoJs6 y active el complemento `Vista previa de HTML`.
3. En el gestor de archivos de AutoJs6, localice el archivo HTML o MHTML que desea ver y abra su menú adicional (más acciones).
4. Elija `Vista previa de HTML`; la página se abrirá en un visor dedicado.
5. Durante la lectura, use el menú superior derecho para buscar texto, alternar entre `Ver código fuente` y `Ver vista previa`, `Exportar PDF`, `Actualizar`, cambiar el `Modo de pantalla completa` o abrir `Configuración` para ajustar el tamaño, el tema, las imágenes de red y el inicio a pantalla completa; pulse Atrás para cerrar la búsqueda, salir de pantalla completa o cerrar el visor.

> Si el complemento no aparece en el centro de complementos, actualice primero AutoJs6 a una versión reciente (compilación interna 5269 o posterior). Explorer Action v2 admite el botón principal y el menú contextual para un archivo, con permisos temporales de lectura del documento y su carpeta. Se requiere AutoJs6 build 5269 o posterior.

******

### Formatos compatibles

******

El complemento reconoce las siguientes extensiones, además de archivos sin extensión marcados explícitamente como `text/html`, `application/xhtml+xml`, `multipart/related` o `application/x-mimearchive` por el anfitrión:

```text
html, htm, shtm, shtml, xht, xhtml, mht, mhtml
```

Los archivos de hasta 8 MB se cargan completos. El HTML normal más grande puede mostrar solo los primeros 8 MB tras confirmación y con el corte marcado; MHTML debe permanecer completo y se rechaza si supera el límite. El BOM tiene prioridad al decodificar HTML; después se usa el juego de caracteres MIME o una declaración `meta charset` o `http-equiv` inicial y, si falta, UTF-8. MHTML admite anidamiento MIME acotado y las codificaciones de transferencia identidad, quoted-printable y Base64.

******

### Preguntas frecuentes

******

#### ¿Por qué los botones y los efectos dinámicos de la página no responden?

Los scripts están desactivados por defecto. Para un HTML normal completo y de confianza, active el modo interactivo en ajustes para JavaScript, botones, WebGL y almacenamiento local. MHTML, HTML truncado, código fuente y exportación PDF siempre desactivan los scripts.

#### ¿Por qué faltan algunas imágenes o estilos?

Los recursos locales proceden de la carpeta autorizada o del archivo MHTML. El modo seguro permite imágenes HTTPS públicas; el interactivo permite también scripts, estilos y solicitudes HTTPS públicos al activar los recursos de red. Desactivar la red bloquea las solicitudes salientes de WebView. Los scripts interactivos pueden enviar datos a servicios remotos; use este modo solo con páginas de confianza.

#### La vista previa no es idéntica a la del navegador. ¿Es normal?

En el modo seguro predeterminado: Sí. Además de eliminar los scripts, el visor inyecta un estilo de lectura base y desactiva algunas funciones avanzadas. El objetivo es una lectura segura y legible, no una reproducción píxel a píxel. Compruebe el aspecto final en un navegador.

#### ¿Este complemento sube mis archivos a algún sitio?

Los recursos locales proceden de la carpeta autorizada o del archivo MHTML. El modo seguro permite imágenes HTTPS públicas; el interactivo permite también scripts, estilos y solicitudes HTTPS públicos al activar los recursos de red. Desactivar la red bloquea las solicitudes salientes de WebView. Los scripts interactivos pueden enviar datos a servicios remotos; use este modo solo con páginas de confianza.

#### ¿Dónde se guarda un PDF exportado?

`Exportar PDF` abre el panel de impresión del sistema Android. Elija `Guardar como PDF` y seleccione el nombre y el destino en el selector de archivos del sistema. El complemento nunca escribe directamente en el almacenamiento ni solicita ese permiso. La exportación siempre vuelve a leer el HTML o el MHTML completo y sanea la página raíz, incluso si se inicia desde la vista de código, y no altera el estado actual del visor.

#### ¿Puedo editar archivos HTML o previsualizar un sitio web completo?

La versión actual se limita a la vista previa de solo lectura de un único archivo: no hay edición ni exploración de directorios. Los recursos junto al archivo se leen bajo demanda, y el complemento solo posee el permiso temporal de lectura concedido por el anfitrión. Consulte [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md) para conocer lo previsto.

******

### Permisos y seguridad

******

El modo interactivo comienza desactivado en cada vista y requiere activación explícita para el documento actual de confianza. CSP bloquea fetch, WebSocket, workers y marcos. JavaScript interactivo no es un aislamiento de red general: WebRTC puede comunicarse al margen del control de recursos. Mantenga los documentos desconocidos en el modo estático predeterminado.

- En el modo seguro predeterminado: Saneamiento previo: se eliminan scripts, atributos de controladores de eventos, marcos incrustados, objetos incrustados y destinos de formularios, y se filtran las direcciones de enlaces o recursos no seguras.
- Cookies, acceso directo a archivos/content, puentes JavaScript nativos y ventanas nuevas permanecen desactivados. Solo el modo interactivo permite almacenamiento local para preferencias y puntuaciones. El código fuente y PDF usan contenido saneado de nuevo.
- El modo interactivo comienza desactivado en cada vista y requiere activación explícita para el documento actual de confianza. CSP bloquea fetch, WebSocket, workers y marcos. JavaScript interactivo no es un aislamiento de red general: WebRTC puede comunicarse al margen del control de recursos. Mantenga los documentos desconocidos en el modo estático predeterminado.
- Privilegio mínimo: el complemento solo recibe el permiso temporal de lectura por URI de contenido concedido por el anfitrión, nunca ve rutas del sistema de archivos y envía la salida PDF mediante el servicio de impresión de Android en vez de escribirla él mismo.
- Entrada acotada: la lectura se limita a 8 MB; MHTML limita además partes, anidamiento, cabeceras y cuerpos decodificados; los nombres de archivo y rutas de recursos se validan estrictamente para impedir accesos fuera de ámbito.
- Active solo para el documento actual de confianza. WebRTC y otras API pueden conectarse fuera del control de recursos. Cada vista nueva desactiva los scripts.
- La vista estática y el cliente HTTPS controlado validan y fijan las direcciones DNS públicas y revisan cada redirección. El control de recursos gobierna este cliente, que solo admite GET y HEAD.

En el modo seguro predeterminado: El manifiesto fuente solo solicita el permiso de red (usado cuando se activan imágenes HTTPS) y el permiso de complemento de AutoJs6. AndroidX también añade un permiso de firma limitado al paquete para proteger receptores dinámicos no exportados; no concede acceso a datos del dispositivo. Al desactivar las imágenes de red, WebView no realiza solicitudes salientes. El acceso al destino PDF pertenece a las interfaces de impresión y selección de archivos de Android, por lo que no solicita almacenamiento, contenido multimedia, cámara, ubicación ni otros permisos sensibles.

******

### Interfaz del complemento

******

La siguiente información está dirigida a desarrolladores; el anfitrión descubre y ejecuta el complemento con estas identidades:

```text
application id: io.github.supermonster003.autojs6.plugin.htmlpreviewer
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: html-previewer
engine: explorer-action
variant: default
protocol version: 2
minimum host build: 5269
audited host build: 5279
audited host protocol: 22
```

Explorer Action v2 admite el botón principal y el menú contextual para un archivo, con permisos temporales de lectura del documento y su carpeta. Se requiere AutoJs6 build 5269 o posterior.

- [Ver la matriz de compatibilidad de Explorer Action](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/docs/explorer-action-compatibility.md)

******

### Hoja de ruta

******

Las capacidades previstas y su estado se registran como lista marcable en ROADMAP.md, organizada por hitos con criterios de aceptación, y abarca la búsqueda en la página, el ajuste del tamaño del texto, la compatibilidad con más codificaciones, un interruptor para imágenes web, entre otros. Los elementos sin marcar describen planes, no capacidades ya publicadas. Los comentarios mediante Issues son bienvenidos.

- [Ver ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md)

******

### Historial de versiones

******

#### v1.1.3

_2026/09/19_

- `Corrección` Los colores de las barras de estado y navegación se adaptan al fondo de la página en cuanto se muestra, sin esperar a que termine de cargar JavaScript
- `Corrección` Advertencias de lectura de SDK XML v4 con AGP 9.1 y comprobaciones de alineación nativa de APK activadas por error al ensamblar pruebas unitarias JVM, mediante los plugins de compilación compartidos 1.8.3

#### v1.1.2

_2026/09/16_

- `Mejora` Tras compileSdk, targetSdk sube a 37 (Android 17); el comportamiento del plugin no depende del nuevo objetivo

#### v1.1.1

_2026/09/15_

- `Mejora` compileSdk sube a 37 (Android 17); targetSdk se mantiene en 36 hasta verificar el comportamiento que depende del objetivo

##### Para consultar más historial de versiones

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/assets/doc/CHANGELOG-es.md)

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

Los parámetros de compilación provienen de `version.properties`. El SDK mínimo actual es 24 y el SDK de destino es 37.

******

### Localización y generación de documentos

******

```text
.readme/common.json
.readme/lang_*.json
.readme/template_readme.md
.changelog/lang_*.json
.changelog/template_changelog.md
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localiza los metadatos del complemento y la interfaz del visor, mientras que `plugin_instruction.md` proporciona las instrucciones visibles en el anfitrión. Para el README y el changelog, edite siempre las fuentes JSON bajo `.readme/` y `.changelog/` y ejecute `py .python/generate_markdown.py` para regenerarlo todo; los archivos generados nunca se editan a mano. Ejecute `py .python/generate_markdown.py --check` para comprobar que fuentes y archivos generados están sincronizados.

******

### Enlaces

******

- Documentación de AutoJs6: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/docs/16kb.md)
