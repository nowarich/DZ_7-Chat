package ru.geekbains.DZ;

import ru.geekbains.DZ.application.AuthenticationService;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ServerChat implements Chat {
    private ServerSocket serverSocket;
    private Set<ClientHandler> clients;
    private AuthenticationService authenticationService;

    public ServerChat() {
        start();
    }

    @Override
    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    private void start() {
        try {
            serverSocket = new ServerSocket(8888);
            clients = new HashSet<>();
            authenticationService = new AuthenticationService();

            while (true) {
                System.out.println("Server is waiting for a connection");
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                System.out.println(String.format("> %s > Client [%s] successfully logged in", new Date(), clientHandler.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void broadcastMessage(String message) {
        String[] mayBePMessage = message.split("\\s");
        if (mayBePMessage[1].equals("-pm")) {
//            int pmStartPos = message.indexOf(" ", 4);
//            String pMessage = message.substring(pmStartPos);
//            System.out.println(pmStartPos + pMessage);
            String pMessage = mayBePMessage[0] + " PM:";
            for (int i = 2; i < mayBePMessage.length; i++) {
//                if (i == 2) {
//                    mayBePMessage[i] = ":";
//                }
                pMessage = pMessage + " " + mayBePMessage[i]; //pMessage.concat(mayBePMessage[i]);
            }
            for (ClientHandler client: clients) {
                if (client.getName().equals(mayBePMessage[2])) {
                    client.sendMessage(pMessage);
                }
            }
        } else {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    @Override
    public boolean isNicknameOccupied(String nickname) {
        for (ClientHandler client : clients) {
            if (client.getName().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

//    @Override
//    public void receiveMessage() {
//
//    }

    @Override
    public void subscribe(ClientHandler client) {
        clients.add(client);
    }

    @Override
    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }
}
