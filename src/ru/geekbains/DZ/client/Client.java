package ru.geekbains.DZ.client;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Client(String host, int port) {
        try {
            socket = new Socket("127.0.0.1", 8888);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            throw new ClientConnectionException("SWW", e);
        }
    }

    public String receiveMessage() throws ClientConnectionException {
        try {
            return in.readUTF();
        } catch (Exception e) {
            throw new ClientConnectionException("SWW", e);
        }
    }


    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (Exception e) {
            throw new ClientConnectionException("SWW", e);
        }
    }

    public void close() {
        close(in);
        close(out);
    }

    private void close(Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
        stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

