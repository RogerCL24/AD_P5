package org.example.serverrtmp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Path("/encryptedStream")
public class EncryptedStreamResource {

    private static final String HLS_PATH = "C:/nginx-rtmp-win32-dev/temp/hls";
    private static final String SECRET_KEY = "1234567890123456";    // Clave de prueba


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
    @Path("/{file}")
    @Produces("application/octet-stream")
    public Response getEncryptedSegment(@PathParam("file") String fileName) throws Exception {

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

            byte[] encryptedBytes = encrypt(fileBytes, SECRET_KEY);

            return Response.ok(encryptedBytes, "application/octet-stream")
                    .header("X-Encrypted", "true") // Indicar que el segmento está cifrado
                    .header("Access-Control-Allow-Origin", "*")
                    .build();


        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error reading file").build();
        }
    }

    private static byte[] encrypt(byte[] data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

}