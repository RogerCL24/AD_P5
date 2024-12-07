package org.example.serverrtmp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Path("/encryptedStream")
public class EncryptedStreamResource {

    private static final String HLS_PATH = "C:/nginx-rtmp-win32-dev/temp/hls"; // Ruta donde NGINX guarda los segmentos
    private static final String SECRET_KEY = "1234567890123456"; // Clave de 16 bytes (AES)


    @GET
    @Path("/playlist.m3u8")
    @Produces("application/vnd.apple.mpegurl")
    public Response getPlaylist() {
        File playlistFile = new File(HLS_PATH, "stream1.m3u8"); 
        if (!playlistFile.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try (FileInputStream fis = new FileInputStream(playlistFile)) {
            byte[] playlistBytes = fis.readAllBytes();
            return Response.ok(playlistBytes)
                    .header("Content-Disposition", "inline; filename=stream1.m3u8")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error reading playlist").build();
        }
    }


    @GET
    @Path("/listSegments")
    @Produces("application/json")
    public Response listSegments() {
        File folder = new File(HLS_PATH);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".ts")); // Filtra solo los .ts

        if (listOfFiles != null && listOfFiles.length > 0) {
            String[] segmentNames = new String[listOfFiles.length];
            for (int i = 0; i < listOfFiles.length; i++) {
                segmentNames[i] = listOfFiles[i].getName();
            }

            return Response.ok(segmentNames).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No segments available").build();
        }
    }


   @GET
    @Path("/{file}")
    @Produces("application/octet-stream")
    public Response getEncryptedSegment(@PathParam("file") String fileName) {

        // Validar nombre del archivo
        if (fileName.contains("..") || fileName.contains("/")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid file name").build();
        }

        File file = new File(HLS_PATH, fileName);
        System.out.println("Intentando leer el archivo: " + file.getAbsolutePath());
        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = fis.readAllBytes();
            return Response.ok(fileBytes, "video/mp2t")           //quitar esto
                    .header("Content-Disposition", "attachment; filename=" + fileName)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
            // Cifrar el segmento
            /*System.out.println("Iniciando encriptación para: " + fileName);
            byte[] encryptedBytes = encrypt(fileBytes, SECRET_KEY);
            System.out.println("Acabando encriptación para: " + fileName);

            return Response.ok(encryptedBytes)
                    .header("Content-Disposition", "attachment; filename=" + fileName)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();*/
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error reading file").build();
        }
    }

    private byte[] encrypt(byte[] data, String key) throws IOException {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new IOException("Error during encryption", e);
        }
    }
}