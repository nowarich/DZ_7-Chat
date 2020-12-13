package ru.geekbains.DZ;

import ru.geekbains.DZ.application.AuthenticationService;

public interface Chat {
    void broadcastMessage(String message);
    boolean isNicknameOccupied(String nickName);
    void subscribe(ClientHandler client);
    void unsubscribe(ClientHandler client);
    AuthenticationService getAuthenticationService();
}
