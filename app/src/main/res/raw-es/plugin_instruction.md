Use HTML Previewer desde el gestor de archivos:

1. Instale y active el complemento `HTML Previewer`.
2. Abra el menú secundario de un archivo HTML o MHTML compatible.
3. Seleccione `Vista previa de HTML`.

El complemento recibe acceso temporal de lectura al archivo seleccionado y a su directorio principal mediante URI de contenido. No recibe una ruta directa del sistema de archivos.

Extensiones compatibles: `html`, `htm`, `shtm`, `shtml`, `xht`, `xhtml`, `mht`, `mhtml`.

Para MHTML, el visor selecciona la raíz HTML del archivo y asigna los estilos, imágenes, fuentes, audio y vídeo incrustados permitidos a un origen virtual aislado. Las ubicaciones y los ID de contenido son solo etiquetas internas; las etiquetas ambiguas se ignoran y nada se extrae al almacenamiento.

Los scripts están desactivados por defecto. Para un HTML normal completo y de confianza, active el modo interactivo en ajustes para JavaScript, botones, WebGL y almacenamiento local. MHTML, HTML truncado, código fuente y exportación PDF siempre desactivan los scripts. Cookies, acceso directo a archivos/content, puentes JavaScript nativos y ventanas nuevas permanecen desactivados. Solo el modo interactivo permite almacenamiento local para preferencias y puntuaciones. El código fuente y PDF usan contenido saneado de nuevo.

El visor también incluye búsqueda en la página, tamaño de texto ajustable y selección de tema. Reconoce el BOM, el juego de caracteres MIME y las declaraciones de codificación HTML iniciales. El HTML normal de más de 8 MB requiere confirmación antes de mostrar solo los primeros 8 MB con el corte marcado; MHTML debe permanecer completo y se rechaza por encima de 8 MB.

Use `Ver código fuente` en el menú del visor para inspeccionar el texto original con un diseño monoespaciado y de solo lectura; seleccione `Ver vista previa` para volver. Las etiquetas siguen siendo texto literal en este modo y se bloquean todas las solicitudes salientes.

Los recursos locales proceden de la carpeta autorizada o del archivo MHTML. El modo seguro permite imágenes HTTPS públicas; el interactivo permite también scripts, estilos y solicitudes HTTPS públicos al activar los recursos de red. Desactivar la red bloquea las solicitudes salientes de WebView. Los scripts interactivos pueden enviar datos a servicios remotos; use este modo solo con páginas de confianza.

Seleccione `Exportar PDF` para abrir el panel de impresión de Android con una copia recién leída, saneada y de tema claro. Esto también se aplica al exportar desde la vista de código y no cambia el estado actual del visor. Elija `Guardar como PDF` y el destino en la interfaz del sistema; el complemento no solicita permiso de almacenamiento.

Explorer Action v2 admite el botón principal y el menú contextual para un archivo, con permisos temporales de lectura del documento y su carpeta. Se requiere AutoJs6 build 5269 o posterior.

Los menús y diálogos siguen el idioma y modo oscuro de AutoJs6, al igual que GitHub (Auto) y el tema HTML automático. Las barras usan un color representativo de los bordes de la página y texto e iconos negros o blancos con contraste. Para degradados, imágenes y animaciones, el color permanece fijo hasta recargar para evitar parpadeos.
