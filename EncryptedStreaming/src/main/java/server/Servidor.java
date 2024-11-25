package server;

import org.glassfish.tyrus.server.Server;


public class Servidor {
    public static void main(String[] args) {
        Server server = new Server("localhost", 8080, "/", null, StreamingServer.class);
        try {
            server.start();
            System.out.println("server.Servidor WebSocket executant-se a ws://localhost:8080/stream");
            System.in.read(); // Espera que premis una tecla per parar-lo
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
