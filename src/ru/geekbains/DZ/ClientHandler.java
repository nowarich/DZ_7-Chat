package ru.geekbains.DZ;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import static java.lang.Thread.sleep;

public class ClientHandler {
    private String name;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private Chat chat;


    public ClientHandler(Socket socket, Chat chat) {
        this.socket = socket;
        this.chat = chat;
//        name = String.valueOf(socket.getPort());
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }
//        if (chat.isNicknameOccupied(name)) {
//            sendMessage(String.format("Sorry user %s already registered.", name));
//            return;
//        }
        listen();
    }

    public String getName() {
        return name;
    }

    private void listen() { // Handler слушает клиента
        new Thread(() -> {
            doAuth();
            receiveMessage();
        }).start();
    }

    private void doAuth() {
        try {
            /**
             * -auth login password
             * sample: -auth l1 p1
             */
            Thread timeout = new Thread(() -> {
                try {
                    sleep(120000);
                    sendMessage("Login time out reached. Connection is terminated.");
                    try {
                        socket.close();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            timeout.start();
            while (true) {
                sendMessage("Please authorise. Sample: [-auth login password]");
                String mayBeCredetials = in.readUTF();
                if (mayBeCredetials.startsWith("-auth")) {
                    String[] credentials = mayBeCredetials.split("\\s");
                    String mayBeNickname = chat.getAuthenticationService()
                            .findNicknameByLoginAndPassword(credentials[1], credentials[2]);
                    if (mayBeNickname != null) {
                        if (!chat.isNicknameOccupied(mayBeNickname)) {
                            sendMessage("[INFO] Auth OK");
                            name = mayBeNickname;

                            chat.broadcastMessage(String.format("[%s] logged in", name));
                            chat.subscribe(this);
                            timeout.interrupt();
                            return;
                        } else {
                            sendMessage("[INFO] Current user is already logged in");
                        }

                    } else {
                        sendMessage("[INFO] Wrong login or password");
                    }
                }

            }

        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }
    }

    private void authTimeout() {
//        long startTime = System.currentTimeMillis();
        new Thread(() -> {
//        long timer = System.currentTimeMillis() - startTime;
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void sendMessage(String message){
        try {
            out.writeUTF(message);
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }


    }
    public void receiveMessage(){
        while (true){
            try {
                String message = in.readUTF();
                if (message.startsWith("-exit")) {
                    chat.unsubscribe(this);
                    chat.broadcastMessage(String.format("[%s] logged out", name));
                    break;
//                } else if (message.startsWith("-pm")) {
//                    System.out.println("PM!");
//                    break;
                }
                chat.broadcastMessage(String.format("[%s]: %s", name, message));
            } catch (Exception e) {
                throw new RuntimeException("SWW", e);
            }
        }

    }
}
