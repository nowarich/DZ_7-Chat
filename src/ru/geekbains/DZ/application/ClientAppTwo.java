package ru.geekbains.DZ.application;

import ru.geekbains.DZ.client.ClientChatAdapter;

public class ClientAppTwo {
    public static void main(String[] args) {
        new ClientChatAdapter("localhost", 8888);
    }
}

