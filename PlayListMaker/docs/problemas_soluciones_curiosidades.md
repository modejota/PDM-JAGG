# ¿Cómo obtener la imagen del álbum y la ruta de la canción?

En versiones anteriores del API teníamos la propiedad "MediaStore.Audio.Albums.ALBUM_ART", pero actualmente se encuentra "deprecated". La alternativa pasa por tomar la URI del directorio donde se almacenan dichas imágenes, obtener el ID del album con "MediaStore.Audio.Media.ALBUM_ID", concatenarlos y utilizar una librería de terceros, como Picasso, para cargar la imagen en la interfaz.

Hay otras alternativas, como utilizar librerías como "FFmpegMediaMetadataRetriever" para obtener la imagen. Pero aparentemente deberemos recurrir a Picasso o Glide para cargar la imagen de todos modos.

Además, mencionar que el campo para obtener la ruta de una canción me ha resultado un poco "engañoso". Para obtener la URI hay campos en "MediaStore.Audio.Media" llamados EXTERNAL_CONTENT_URI e INTERNAL_CONTENT_URI, que claramente hacen referencia a que se obtiene una URI. Sin embargo, el campo para acceder a la ruta es "MediaStore.Audio.Media.DATA", el cual no hace referencia a PATH; podría dar a entender que se obtienen otros metadatos de la canción si no se lee la documentación en detalle.

# Permisos para archivos.

Los permisos necesarios para acceder al sistema de archivos y operar sobre el mismo varían en función de la versión de la API. Por debajo de Android 10 (Q) necesitamos pedir tanto WRITE_EXTERNAL_STORAGE como READ_EXTERNAL_STORAGE, pero para versiones superiores solo se necesita el permiso de lectura.

El permiso de lectura debe declararse en el fichero AndroidManifest, independientemente de la versión. Para el permiso de escritura se tiene que especificar que solo se pida para versiones del SDK hasta la 28.

Con Android 11 se incluye un nuevo sistema de gestión de permisos a nivel de SO, y se incluye un nuevo sistema de peticiones, revocados y comprobaciones más robusto para los desarrolladores (requestPermissionLauncher). Estos cambios parecen estar especialmente enfocados para aquellos permisos con opción de usarse "solo esta vez" (localización, micrófono y cámara). 

Dado que no utilizo ninguno de estos permisos, y se necesita escribir mucho más código con la nueva API, prefiero no utilizarla y hacer uso del "método clásico".

# Cambiar la visibilidad de elementos en la interfaz

En las primeras pruebas que hice, siguiendo algunos tutoriales, era la clase ViewHolder de cada Adapter la encargada de cambiar la visibilidad de los elementos. Resulta obvio, pero los RecyclerView reciclan los elementos, por lo que los cambios en una dada posición se replicaban en la misma posición posteriormente, para otra canción distinta, tras el reciclado.

La solución pasa por tener una función para cambiar un valor booleano que indique la visibilidad de elemento (en mi caso un tick de seleccionado) dentro del adaptado y notifique que el elemento ha cambiado. Posteriormente, llamamos a dicha función desde la actividad o fragmento correspondiente cuando se necesite. 

Cuando se debe renderizar el elemento, el adaptador llama al método "onBindViewHolder", y este es quien asigna los valores correspondientes dado un objeto obtenido del listado. Es en este método donde
tenemos que realizar el cambio de visibilidad en función del booleano actualizado anteriormente.

# Gestión de clicks

Hay varias maneras de gestionar los clicks. Aparentemente, cada desarollador prefiere implementarlo de una manera.

Una manera es pasar al Adapter una declaración de función que reciba un objeto del listado y no retorne nada. Dentro del ViewHolder, en el bloque init, se declara un listener que invoque a dicho método. Así, podemos tener distintas implementaciones del método, será la actividad o fragmento encargado de "definir" el funcionamiento para cada RecyclerView correspondiente.

En mi caso, he optado por hacer que el Adapter reciba un listener de una interfaz propia creada dentro del propio Adapter. En el ViewHolder implementamos los Listener de la clase View, inicializamos la vista con el Listener correspondiente y redefinimos la función "onClick" para que llame a la de nuestro listener. La actividad o fragmento que correspondan, implementan dicha interfaz, sobreescribiendo su método "onItemClick" y definiendo el comportamiento que queramos.

Utilizo esta segunda opción, tras haber probado ambas, ya que me es más facil gestionar la posición pulsada de esta manera.

# Deslizar para realizar acciones.

Una de las sugerencias recibidas, que nunca me convenció demasiado dado el contexto de la aplicación, era que el borrado de canciones o su añadido a la selección se hiciera mediante gestos de deslizamiento sobre los objetos del listado.

El framework proporciona una clase llamada "ItemTouchHelper" para realizar dichas acciones. El "problema" de dicha clase es que espera que los gestos de deslizamiento, ya sea hacia los laterales o hacia arriba, desemboquen en un reposicionamiento de los elementos del listado sí o sí. Esto tiene sentido si deslizamos para eliminar un archivo o si deslizamos para archivarlo o reposicionarlo, los cuales son los usos típicos de esta clase. 

Sin embargo, no tiene sentido en nuestra aplicación; al deslizar para añadir la canción a la lista no movemos ningun elemento del listado de sitio. Al no mover elementos, provoca errores en el visualizado del elemento deslizado, dejandolo en blanco y sin hacer que ningún otro ocupe su lugar hasta que no deslizamos y forzamos el re-renderizado del elemento, que procede a desaparecer.

Finalmente, he optado por implementar selección/deselección mediante toques en los elementos para las canciones, y toques largos para las listas de reproducción. Si hay elementos seleccionados, apareceran en la barra superior botones que permitan realizar las acciones deseadas. Me parece un enfoque más cómodo para el usuario que realizar muchos deslizamientos.

He de mencionar que sí utilizo la clase "ItemTouchHelper" para gestionar la reordenación de las canciones seleccionadas. Esta clase facilita enormemente esta casuística, permitiendo camnbiar la transparencia del objeto mientras se desplaza, por ejemplo.

# Compartir datos en la aplicación.

Por cómo he planteado la interfaz, es necesario compartir datos entre los fragmentos en los que se muestran todas las canciones del dispositivo y las seleccionadas. La documentación no recomienda pasar datos entre fragmentos directamente, sino hacerlo por la actividad padre mediante una serie de métodos.

Los métodos proporcionados por el framework están pasados para pasar datos de tipos primitivos, no para objetos completos, por lo que se me complicaba la tarea.

Consultado un poco StackOverflow, la gente recomendaba tener los datos en la actividad padre, como propiedades de dicha actividad, y consultarlos haciendo uso de funciones como "getActivity". Dado que las respuestas eran algo antiguas, decidí buscar más para ver si había algun método "más moderno". 

En respuestas más modernas, se recomendaba almacenar los datos no sensibles que se necesiten compartir en un objeto "Singleton" compartido por la aplicación. La manera de acceder y modificar los datos es más sencilla, y parece incurrir en menos sobrecarga. Esta fue la alternativa por la que me decanté.

También he visto que existen clases como ViewModel o LiveData en el framework que permiten compartir datos y manejar su gestión durante los ciclos de vida. Los probé, pero también parecen estar pensados para datos de tipos primitivos, y me dieron muchos problemas al intentar compartir listas de elementos.


# El estándar M3U y los reproductores.

El formato estándar libre para listas de reproducción, tanto de vídeo como de audio, es M3U. Existe una variante, M3U8, cuya unica diferencia es el uso de codificación UTF-8 por defecto.

El estándar especifica que el fichero debe empezar por #EXTM3U, y para cada archivo debe haber dos líneas. La primera linea debe seguir el siguiente formato "#EXTINF:(duracion en segundos),(nombre)", y la segunda debe contener la ruta al fichero.

La ruta del fichero debe ser una ruta absoluta, una ruta relativa a la ubicación del fichero, o una URI. Es precisamente este último punto el genera disputa.

En función del reproductor de música, este puede contemplar o no la presencia de atributos opcionales no estándar, como #EXTALB o #EXTART, o esperar una tipo de ruta concreta a la hora de importar rutas de reproducción.

Por ejemplo, Blackplayer se salta el propio estándar y obvia los tag, salva los resultados en la carpeta "Music" del sistema sin ellos. Permite leer ficheros de cualquier directorio del dispositivo, pero en estos casos si que lee ficheros estándar con los tag sin problema. Este comportamiento me resulta cuanto menos curioso, y bastante chapuzero.

RetroMusic Player, el que utilizo habitualmente, guarda los ficheros en la raiz del sistema, pero su comportamiento a la hora de leerlos no es el que esperaba. Lee sin problema los ficheros ubicados en la raíz del sistema y en la carpeta "Downloads", pero a veces lee los presentes en la carpeta "Music" y a veces no, la mayoría de las veces no. Además, sólo permite importar todas las playlist del sistema, por lo que no se puede importar un único fichero.

Además, si clickas en el archivo M3U y eliges abrirlo con una aplicación concreta en el menú contextual, ni Blackplayer ni RetroMusic los importan correctamente. Sólo permiten importarlos desde interfaz. Por último, cabe destacar que ambas aplicaciones esperan rutas absolutas.

Por último, la navaja suiza de los reproductores, VLC, espera URIs, por lo que al intentar importar canciones especificadas como rutas absolutas falla. (En versiones de pruebas lo exportaba como URIs y funcionaba correctamente.)

Después de haber estado cacharreando varios reproductores y haber consultado el código de algunos libres, como RetroMusic, me da la impresión de que la gestión de listas de reproducción procedentes de fuera de la aplicación está muy descuidada, habiendo aplicaciones que ni lo permiten.