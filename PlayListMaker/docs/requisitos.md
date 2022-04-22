# Requisitos funcionales
---

- 1. Relativos a las listas de reproducción.
    - 1.1. La aplicación debe mostrar los nombres de las listas de reproducción existentes.
    - 1.2. La aplicación debe mostrar el número de canciones de cada lista de reproducción.
    - 1.3. La aplicación debe poder crear nuevas listas de reproducción (crear nuevo fichero).
    - 1.4. La aplicación debe poder borrar listas de reproducción (entendidos como ficheros).
    - 1.5. La aplicación debe permitir editar las listas de reproducción (creando nuevo fichero o editando existente).
    - 1.6. La pantalla principal debe poder permitir "swipe down to refresh" para actualizar las listas de reproducción existentes en el dispositivo.
    - 1.7. Al intentar editar una lista de reproducción existente, o crear una nueva, el sistema debe actualizar el listado de canciones que existen en el dispositivo.

- 2. Relativos a las canciones.
    - 2.1. La aplicación debe mostrar las canciones disponibles en el dispositivo.
    - 2.2. La aplicación debe mostrar las canciones ya seleccionadas.
    - 2.3. La aplicación debe poder permitir "ocultar" canciones disponibles en el dispositivo del listado general.
    - 2.4. La aplicación debe poder permitir seleccionar canciones de entre aquellas disponibles en el dispositivo para añadirlas a la lista de reproducción.
    - 2.5. La aplicación debe poder permitir eliminar canciones ya seleccionadas de la lista de reproducción.
    - 2.6. La aplicación debe poder permitir reordenar canciones ya seleccionadas.

- 3. La aplicación debe preguntar por los permisos necesarios para su funcionamiento.

- 4. Relativos a confirmaciones.
    - 4.1. La aplicación deberá pedir confirmación al "ocultar canciones del listado general.
    - 4.2. La aplicación deberá pedir confirmación al eliminar canciones seleccionadas.
    - 4.3. La aplicación deberá pedir confirmación al añadir canciones a la lista de reproducción.
    - 4.4. La aplicación deberá pedir confirmación al intentar borrar una lista de reproducción (fichero).
    - 4.5. Al intentar guardar una lista de reproducción, deberá comprobarse si esta vacía y mostrar un mensaje de así serlo. Aborta la operación.
    - 4.6. Al intentar guardar una lista de reproducción, deberá mostrarse una dialogo emergente para preguntar por el título.
    - 4.7. Al intentar guardar una lista de reproducción, si el título introducido es vacío se mostrará un mensaje. Aborta la operación.
    - 4.8. Al intentar guardar una lista de reproducción, si se pulsa fuera del diálogo que pregunta por el título se cancela la operación.
    - 4.9. Al intentar abrir una aplicación reproductora externa, el usuario deberá confirmar la acción y seleccionar la que desee (si no establece una por defecto).

- 5. La aplicación funcionará sólo en modo vertical.

- 6. Se dispondrán de botones para abrir aplicaciones reproductoras de música externas, a elección del usuario.

# Requisitos no funcionales
---

- 6. Relativo a elementos de la interfaz
    - 6.1. Se mostrará un tick al seleccionar una lista de reproducción o canción.
    - 6.2. Si hay listas de reproducción seleccionadas, aparecerá el icono para poder borrarlas.
    - 6.3. Si hay canciones del listado general seleccionadas, deberá aparecer el icono para "ocultarlas" y el icono para añadiras a la playlist.
    - 6.4. Si hay canciones de la lista de reproducción seleccionadas, deberá aparecer un botón para borrarlas de la lista.
    - 6.5. Se tendrá un botón para crear una nueva lista de reproducción.
    - 6.6. Se tendrá un botón para guardar una lista de reproducción.
    - 6.7. Mientras se tengan listas de reproducción seleccionadas, no se permitirá acceder a editar ninguna de ellas, pero sí a crear una nueva.
    - 6.8. Al pulsar los iconos de acción, salvo el de guardar, deberán aparecer los correspondientes pop-ups de confirmación.
    - 6.9. Se podrán eliminar canciones individualmente, tanto en el listado general, como en el listado seleccionado, con el gesto "swipe to the left".
    - 6.10. Se podrán eliminar una playlist de forma individual con el gesto "swipe to left".
    - 6.11. Se tendrá un botón en la lista de canciones seleccionadas y otro en el listado de playlists, para poder abrir una aplicación reproductora de música externa.

- 7. La aplicación debe disponer de modo claro y modo oscuro, de acuerdo a los ajustes del sistema.

- 8. La aplicación debe disponer de traducciones al inglés y al español. Por defecto, y si en los ajustes del sistema no se indica lengua española, se utilizará la traducción al inglés.
 