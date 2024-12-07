package org.example.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/videoStream/*")
public class VideoStreamServlet extends HttpServlet {

    private static final String API_URL = "http://localhost:5080/ServerRTMP-1.0-SNAPSHOT/api/encryptedStream/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filePath = req.getPathInfo(); // Obtiene la parte después de /videoStream/
        if (filePath == null || filePath.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "File path is missing");
            return;
        }
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1); // Elimina el "/"
        }

        // Construye la URL completa para llamar a la API REST
        URL url = new URL(API_URL + filePath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            resp.setContentType("video/mp2t");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setHeader("Pragma", "no-cache");
            // Pasar contenido al cliente
            resp.setContentType(connection.getContentType());
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    resp.getOutputStream().write(buffer, 0, bytesRead);
                }
            }
        } else {
            resp.sendError(responseCode, "Failed to fetch resource from API");
        }
    }
}
