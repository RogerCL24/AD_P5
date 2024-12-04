<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reproductor de Video en Vivo</title>

    <!-- Enlace al CSS de Video.js -->
    <link href="https://vjs.zencdn.net/7.13.3/video-js.css" rel="stylesheet">

    <!-- Enlace al JavaScript de Video.js -->
    <script src="https://vjs.zencdn.net/7.13.3/video.min.js"></script>
</head>
<body>
<h1>Reproducción de Video HLS en Vivo</h1>

<!-- Contenedor para el reproductor -->
<video id="video_player" class="video-js vjs-default-skin" controls width="800" height="450">
    <source src="http://localhost:8080/api/encryptedStream/stream1.m3u8" type="application/x-mpegURL">
    Tu navegador no soporta la reproducción de este video.
</video>

<script>
    // Inicializar el reproductor de Video.js
    var player = videojs('video_player', {
        controls: true,
        autoplay: true,
        preload: 'auto'
    });
</script>
</body>
</html>
