package ru.geekbains.DZ;

public interface Chat {
    void broadcastMessage(String message);
    boolean isNicknameOccupied(String nickName);
    void subscribe(ClientHandler client);
    void unsubscribe(ClientHandler client);
    AuthenticationService getAuthenticationService();
}
