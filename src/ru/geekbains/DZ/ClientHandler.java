package ru.geekbains.DZ;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class ClientHandler {
    private String name;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private Chat chat;
    private User user;

    public ClientHandler(Socket socket, Chat chat) {
        this.socket = socket;
        this.chat = chat;
//        this.user = user;
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
            user = doAuth();
            sendMessage(name + " has joined a chat");
            receiveMessage();
        }).start();
    }

    private User doAuth() {
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
                String mayBeCredentials = in.readUTF();
                if (mayBeCredentials.startsWith("-auth"))
                {
                    String[] credentials = mayBeCredentials.split("\\s");

                    /** ===== new authentication using mySQL here ======
                     */
                    String email = credentials[1];
                    String password = credentials[2];
                    Objects.requireNonNull(credentials[1], "Email cannot be null");
                    Objects.requireNonNull(credentials[2], "Password cannot be null");
                    try {
                        Connection connection = DatabaseService.connect() ;
                        PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");

                        statement.setString(1, email);
                        statement.setString(2, password);

                        ResultSet rs = statement.executeQuery();
                        if (rs.next()) {
                            User user = new User(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("email"),
                                    rs.getString("password")
                            );
                            if (user.getName() != null) {
                                if (!chat.isNicknameOccupied(user.getName())) {
                                    sendMessage("[INFO] Auth OK");
                                    name = user.getName();

                                    chat.broadcastMessage(String.format("[%s] logged in", name));
                                    chat.subscribe(this);
                                    timeout.interrupt();
                                    return user;
                                } else {
                                    sendMessage("[INFO] Current user is already logged in");
                                }

                            } else {
                                sendMessage("[INFO] Wrong login or password");
                            }
                        }
                        return null;

                    } catch (Exception e) {
                        throw new RuntimeException("SWW", e);
                    }



//                    String mayBeNickname = chat.getAuthenticationService()
//                            .findNicknameByLoginAndPassword(credentials[1], credentials[2]);

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
                if (message.startsWith("-newnick")){
                    String oldName = name;
                    String[] mayBeNewNick = message.split("\\s");
                    name = mayBeNewNick[1];
                    user.setName(mayBeNewNick[1]);
                    UserRepository userRepo = new UserRepository();
                    userRepo.update(user);
                    chat.broadcastMessage(String.format("[%s] is now -> [%s]", oldName, name));
                }
                chat.broadcastMessage(String.format("[%s]: %s", name, message));
            } catch (Exception e) {
                throw new RuntimeException("SWW", e);
            }
        }

    }
}
