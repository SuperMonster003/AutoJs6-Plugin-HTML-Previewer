HTML Previewer muestra una vista previa de archivos HTML y MHTML en AutoJs6 sin abrir un navegador. Las páginas se abren en modo seguro, sin ejecutar scripts; también puedes ver el código fuente, buscar en la página o exportarla a PDF. La vista previa solo lee el archivo y nunca modifica el original.

### Primeros pasos

1. Comprueba que este plugin esté activado en el "Centro de plugins" de AutoJs6.
2. Abre el gestor de archivos de AutoJs6 y busca el archivo HTML o MHTML.
3. Elige "Vista previa de HTML" en el menú del archivo (en AutoJs6 6.8.1 y posteriores está en el menú de primer nivel "HTML Previewer").
4. Usa el menú de la esquina superior derecha de la vista previa para buscar, ver el código fuente, exportar a PDF, abrir la "Configuración" y más.

### Funciones principales

- **Buscar en la página**: resalta las coincidencias del texto que escribes y permite saltar entre ellas.
- **Ver código fuente**: muestra el texto original en un diseño monoespaciado de solo lectura; elige "Ver vista previa" para volver a la página.
- **Exportar PDF**: abre la pantalla de impresión del sistema, donde eliges guardar como PDF y la ubicación; el plugin no necesita permiso de almacenamiento.
- **Modo interactivo**: actívalo en la "Configuración" para ejecutar scripts y que funcionen los botones, las animaciones y el almacenamiento local; se desactiva en cada nueva vista previa, así que actívalo solo para páginas de confianza.
- **Recursos de red**: activa "Cargar imágenes de red" para mostrar imágenes HTTPS; con el modo interactivo activado, la opción pasa a ser "Cargar recursos de red" y también permite scripts y estilos HTTPS.
- **Tamaño del texto y tema**: ajusta el "Tamaño del texto" en la "Configuración" y elige el tema "Seguir AutoJs6", "Claro" u "Oscuro".
- **Modo de pantalla completa**: oculta la barra de herramientas para leer sin distracciones; activa "Iniciar en modo de pantalla completa" para abrir cada vista previa a pantalla completa.
- **Archivos MHTML**: muestra la página de un archivo web con sus estilos, imágenes y fuentes incrustados, sin extraer nada al almacenamiento.

### Requisitos

- Android 7.0 o posterior.
- AutoJs6 6.8.0 o posterior.
- No necesita permiso de almacenamiento; el plugin solo lee el archivo seleccionado y su carpeta, y usa la red únicamente para cargar recursos HTTPS.

### Preguntas frecuentes

**Por qué los botones y las animaciones no responden**

Por defecto, las páginas no ejecutan scripts. Para un archivo HTML completo de confianza, activa el "Modo interactivo" en la "Configuración". Los archivos MHTML, los archivos grandes de los que solo se muestra el principio y la vista de código fuente nunca ejecutan scripts.

**Por qué faltan algunas imágenes o estilos**

Las imágenes y estilos locales deben estar en la carpeta del archivo HTML o en una de sus subcarpetas. Las imágenes de red requieren activar "Cargar imágenes de red", y solo se admiten direcciones HTTPS.

**Por qué solo se muestra el principio de un archivo grande**

Con un archivo HTML de más de 8 MB, el plugin pregunta primero y luego muestra solo los primeros 8 MB, con una marca donde se corta. Los archivos MHTML deben mostrarse completos, así que uno de más de 8 MB no se puede previsualizar.

**Por qué falta "Vista previa de HTML" en el menú del archivo**

El plugin solo procesa archivos con las extensiones html, htm, shtm, shtml, xht, xhtml, mht, mhtml. Comprueba la extensión del archivo y que el plugin esté activado en el "Centro de plugins".

Más detalles en la [página del proyecto](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer).
