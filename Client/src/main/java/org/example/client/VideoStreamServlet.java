package org.example.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/videoStream/*")
public class VideoStreamServlet extends HttpServlet {

    private static final String API_URL = "http://localhost:5080/ServerRTMP-1.0-SNAPSHOT/api/encryptedStream/";
    private static final String SECRET_KEY = "1234567890123456";        // Clave de prueba

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filePath = req.getPathInfo();
        if (filePath == null || filePath.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "File path is missing");
            return;
        }
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }

        URL url = new URL(API_URL + filePath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            boolean isEncrypted = "true".equalsIgnoreCase(connection.getHeaderField("X-Encrypted"));

            resp.setContentType("video/mp2t");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setHeader("Pragma", "no-cache");
            // Pasar contenido al cliente
            resp.setContentType(connection.getContentType());
            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                // Obtener los datos completos
                byte[] allData = baos.toByteArray();

                if (isEncrypted) {
                    byte[] decryptedData = decrypt(allData, SECRET_KEY);

                    // Enviar los datos desencriptados al cliente
                    resp.getOutputStream().write(decryptedData);
                } else {
                    // Enviar los datos originales al cliente
                    resp.getOutputStream().write(allData);
                }
            }

        } else {
            resp.sendError(responseCode, "Failed to fetch resource from API");
        }
    }

    private static byte[] decrypt(byte[] encryptedData, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}