package ru.gb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private List<ClientHandler> clients;

    public ChatServer() {
        clients = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket( 8189)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, this);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
