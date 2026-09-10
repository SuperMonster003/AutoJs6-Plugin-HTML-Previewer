******

### Historial de versiones

******

# v1.0.1

###### 2026/08/08

* `Corrección` Enlace de servicio nulo que impedía la activación en el centro de complementos
* `Mejora` Nombre, descripción y documentación de usuario más claros

# v1.0.0

###### 2026/08/06

* `Función` Complemento HTML Previewer con ID `html-previewer`, motor `explorer-action` y variante `default`
* `Función` Acción secundaria de solo lectura para un archivo en el gestor de archivos mediante `org.autojs.plugin.EXPLORER_ACTION`
* `Función` Ejecución mediante `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` con acceso temporal de lectura a los URI de contenido del archivo y del directorio principal
* `Función` Saneamiento HTML que elimina scripts, atributos de controladores de eventos, marcos, objetos incrustados y direcciones de recursos no seguras antes de mostrar
* `Función` Carga controlada de recursos relativos, recursos data e imágenes HTTPS permitidas con estilo fijo, actualización y pantalla completa
* `Función` Política WebView reforzada con CSP, navegación URI controlada, JavaScript y almacenamiento desactivados y entrada limitada
* `Función` Metadatos, interfaz, instrucciones, README y changelog localizados en español, francés, ruso, árabe, japonés, coreano, inglés, chino simplificado, chino tradicional de Hong Kong y chino tradicional de Taiwán
