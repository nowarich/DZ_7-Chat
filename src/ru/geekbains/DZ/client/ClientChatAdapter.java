package ru.geekbains.DZ.client;

import ru.geekbains.DZ.gui.ChatFrame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClientChatAdapter {
    private ChatFrame chatFrame;
    private Client client;
    private static File file;

    public ClientChatAdapter(String host, int port) {
        file = new File("C:\\Java\\DZ_6-Networks\\src\\ru\\geekbains\\DZ\\client\\history.txt");
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
                chatFrame.append(readNLastLinesViaRAF(5));  //
                while (true) {
                    chatFrame.append(
                            client.receiveMessage()
                    );
                    doBufferedWriter(client.receiveMessage());  //
                }
            } catch (ClientConnectionException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (client != null) {
                    client.close();
                }
            }
        }).start();
    }

//    public static void readNLastLines(int n) throws Exception {
//            File file = new File("C:\\Java\\DZ_6-Networks\\src\\ru\\geekbains\\DZ\\client\\history.txt");
//            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//                List<String> lines = new ArrayList<>();
//                String line;
//
//                while ((line = br.readLine()) != null) {
//                    lines.add(line);
//                }
//
//                for (int i = lines.size() - 1; i >= lines.size() - n; i--) {
//                    System.out.println(lines.get(i));
//                }
//            }
//    }

    private static void doBufferedWriter(String string) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.newLine();
            bw.append(string);
            bw.flush();
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }

    }


    static String readNLastLinesViaRAF(int n) throws Exception {
        RandomAccessFile raf = null;
//        try {
            raf = new RandomAccessFile(file, "r");

            long length = file.length() - 1;
            int readLines = 0;

            StringBuilder sb = new StringBuilder();

            for (long i = length; i >= 0; i--) {
                raf.seek(i);

                char c = (char) raf.read();

                if (c == '\n') {
                    readLines++;
                    if (readLines == n) {
                        break;
                    }
                }
                sb.append(c);
            }
            sb.reverse();
            return sb.toString();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            return null;
//        }


    }
}
