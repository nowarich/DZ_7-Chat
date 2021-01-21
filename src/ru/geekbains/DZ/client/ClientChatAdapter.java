package ru.geekbains.DZ.client;

import ru.geekbains.DZ.gui.ChatFrame;

import java.util.function.Consumer;

public class ClientChatAdapter {
    private ChatFrame chatFrame;
    private Client client;

    public ClientChatAdapter(String host, int port) {
        client = new Client(host, port);
        chatFrame = new ChatFrame(new Consumer<String>() {
            @Override
            public void accept(String messageFromFormSubmitListener) {
                client.sendMessage(messageFromFormSubmitListener);
            }
        });
        read();
    }

    private void read() {

        new Thread(() -> {
            try {
                while (true) {
                    chatFrame.append(
                            client.receiveMessage()
                    );
                }
            } catch (ClientConnectionException e) {
                throw e;
            } finally {
                if (client != null) {
                    client.close();
                }
            }
        }).start();
    }

}
