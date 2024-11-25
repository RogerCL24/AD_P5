package server;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

@ServerEndpoint("/stream")
public class StreamingServer {
    private static SecretKey secretKey;

    static {
        try {
            secretKey = generateAESKey(); // Generar clau única per a aquesta sessió
        } catch (Exception e) {
            throw new RuntimeException("Error generant la clau", e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connexió oberta: " + session.getId());
    }

    @OnMessage
    public void onMessage(ByteBuffer message, Session session) throws Exception {
        // Encriptem el missatge rebut i el reenviem
        byte[] encryptedData = encryptData(message.array());
        session.getBasicRemote().sendBinary(ByteBuffer.wrap(encryptedData));
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Connexió tancada: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error a la sessió: " + session.getId());
        throwable.printStackTrace();
    }

    private static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256, new SecureRandom());
        return keyGenerator.generateKey();
    }

    private static byte[] encryptData(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
}
