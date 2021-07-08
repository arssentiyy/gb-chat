package ru.gb;

import javax.lang.model.element.Name;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Socket socket;
    private final ChatServer server;
    private final DataInputStream in;
    private final DataOutputStream out;

    private String name;


    public ClientHandler(Socket socket, ChatServer server) {
        try {
            this.name = "";
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    authenticate();
                    readMessages();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Не могу создать обработчик для клиента ", e);
        }
    }

    private void authenticate() {
        while (true) {
            try {
                final String str = in.readUTF();
                if (str.startsWith("/auth")) { // пользователь передает логин и пароль
                    final String[] split =  str.split("\\s"); // java заменяет все пробелы
                    final String login = split[1];
                    final String pass = split[2];
                    final String nickname = server.getAuthService().getNicknameByLoginAndPassword(login, pass);
                    if(nickname != null) {
                        if (!server.isNicknameBusy(nickname)) {
                            sendMessage("/auth " + nickname);
                        } else {
                            sendMessage("Уже произведен вход в учетную запись");
                            this.name = nickname;
                            server.broadcast("Пользователь " + nickname + " зашел в чат");
                            server.subscribe(this);
                        }
                    } else {
                        sendMessage("Неверные логин / пароль");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
    }

    private void closeConnection() {
        try {
            server.unsubscribe(this);
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void readMessages() {
        while (true) {
            try {
                final String strFromClient = in.readUTF();
                if("/end".equals(strFromClient)) {
                    return;
                }
                System.out.println("Получено сообщение от " + name + ": " + strFromClient);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public String  getName() {
        return name;
    }
}
