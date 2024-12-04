package org.example.client;

import java.io.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/play")
public class VideoServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // En este caso, simplemente redirigimos al cliente a la URL del archivo .m3u8
        String playlistUrl = "http://localhost:8080/api/encryptedStream/stream1.m3u8";  // URL de tu archivo .m3u8
        response.sendRedirect(playlistUrl);
    }
}