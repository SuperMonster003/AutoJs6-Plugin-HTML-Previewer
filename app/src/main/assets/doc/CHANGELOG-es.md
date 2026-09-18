******

### Historial de versiones

******

# v1.1.3

###### 2026/09/18

* `Corrección` Los colores de las barras de estado y navegación se adaptan al fondo de la página en cuanto se muestra, sin esperar a que termine de cargar JavaScript

# v1.1.2

###### 2026/09/16

* `Mejora` Tras compileSdk, targetSdk sube a 37 (Android 17); el comportamiento del plugin no depende del nuevo objetivo

# v1.1.1

###### 2026/09/15

* `Mejora` compileSdk sube a 37 (Android 17); targetSdk se mantiene en 36 hasta verificar el comportamiento que depende del objetivo

# v1.1.0

###### 2026/09/13

* `Función` Historial de versiones local desde la interfaz con traducciones y alternativa en inglés
* `Corrección` La vista estática y el cliente HTTPS controlado validan y fijan las direcciones DNS públicas y revisan cada redirección. El control de recursos gobierna este cliente, que solo admite GET y HEAD.
* `Corrección` Active solo para el documento actual de confianza. WebRTC y otras API pueden conectarse fuera del control de recursos. Cada vista nueva desactiva los scripts.
* `Corrección` Conservar HTTP 206 y otros estados correctos al cargar recursos remotos
* `Mejora` Comprobación de la firma completa, los APK esperados y la documentación reproducible de cada versión
* `Dependencia` Añadir OkHttp 4.12.0 para la carga controlada de recursos HTTPS

# v1.0.1

###### 2026/09/11

* `Función` Los scripts están desactivados por defecto. Para un HTML normal completo y de confianza, active el modo interactivo en ajustes para JavaScript, botones, WebGL y almacenamiento local. MHTML, HTML truncado, código fuente y exportación PDF siempre desactivan los scripts.
* `Corrección` Fallo ocasional al activar el complemento en el centro de complementos
* `Corrección` Corregidos el rechazo de protocolo del botón principal y los cierres de ajustes; sincronizados la apariencia de AutoJs6, las barras y los controles monocromos
* `Mejora` Nombre y descripción del complemento más concisos, documentación de usuario más legible
* `Mejora` La verificación de compilación rechaza dependencias nativas accidentales y genera un informe JSON

# v1.0.0

###### 2026/08/06

* `Función` Primera versión: una acción de menú `Vista previa de HTML` para archivos HTML en el gestor de archivos de AutoJs6 (ID de complemento `html-previewer`)
* `Función` Visor seguro: los scripts, marcos, objetos incrustados y envíos de formularios se eliminan antes de mostrar, todo en modo de solo lectura y sin ejecutar nunca JavaScript
* `Función` Carga de recursos: admite los estilos de la página, los recursos locales junto al archivo y las imágenes web HTTPS bajo reglas controladas, con el resto de peticiones bloqueadas
* `Función` Experiencia de lectura: tema claro/oscuro automático, zoom con dos dedos, actualización, modo de pantalla completa y ajuste `Iniciar en modo de pantalla completa`
* `Función` Límites de seguridad: solo se acepta el permiso temporal de lectura concedido por el anfitrión, cada archivo se limita a 8 MB, y los nombres de archivo y las rutas de recursos se validan estrictamente
* `Función` Multilingüe: interfaz, instrucciones, README y changelog en 10 idiomas
