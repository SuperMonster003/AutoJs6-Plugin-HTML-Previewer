******

### Historial de versiones

******

# v1.0.1

###### 2026/08/08

* `Función` Los scripts están desactivados por defecto. Para un HTML normal completo y de confianza, active el modo interactivo en ajustes para JavaScript, botones, WebGL y almacenamiento local. MHTML, HTML truncado, código fuente y exportación PDF siempre desactivan los scripts.
* `Corrección` Fallo ocasional al activar el complemento en el centro de complementos
* `Corrección` Corregidos el rechazo de protocolo del botón principal y los cierres de ajustes; sincronizados la apariencia de AutoJs6, las barras y los controles monocromos
* `Mejora` Nombre y descripción del complemento más concisos, documentación de usuario más legible

# v1.0.0

###### 2026/08/06

* `Función` Primera versión: una acción de menú `Vista previa de HTML` para archivos HTML en el gestor de archivos de AutoJs6 (ID de complemento `html-previewer`)
* `Función` Visor seguro: los scripts, marcos, objetos incrustados y envíos de formularios se eliminan antes de mostrar, todo en modo de solo lectura y sin ejecutar nunca JavaScript
* `Función` Carga de recursos: admite los estilos de la página, los recursos locales junto al archivo y las imágenes web HTTPS bajo reglas controladas, con el resto de peticiones bloqueadas
* `Función` Experiencia de lectura: tema claro/oscuro automático, zoom con dos dedos, actualización, modo de pantalla completa y ajuste `Iniciar en modo de pantalla completa`
* `Función` Límites de seguridad: solo se acepta el permiso temporal de lectura concedido por el anfitrión, cada archivo se limita a 8 MB, y los nombres de archivo y las rutas de recursos se validan estrictamente
* `Función` Multilingüe: interfaz, instrucciones, README y changelog en 10 idiomas
