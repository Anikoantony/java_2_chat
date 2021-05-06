package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerTest {
    // ServerSocket это класс Сервера
    private ServerSocket server2;
    private Socket socket2;

    public ServerTest()
    {
        clients = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();

        try {
            server2 = new ServerSocket(PORT2);
            System.out.println("Server 2 started");

            while (true) {
                socket2=server2.accept();
                System.out.println("client connect" + socket.getRemoteSocketAddress());
                new ClientHandlerTest(this,socket2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Server Closed");
                server2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }


    private ServerSocket server;

    private Socket socket;
    private final int PORT = 8189;
    private final int PORT2 = 8192;

    private List<ClientHandlerTest> clients;
    private AuthService authService;



    public void broadcastMsg(ClientHandlerTest sender, String msg) {
        String message = String.format("%s : %s", sender.getNickname(), msg);

        for (ClientHandlerTest c : clients) {
            c.sendMsg(message);
        }
    }

    public void privateMsg(ClientHandlerTest sender, String receiver, String msg) {
        String message = String.format("[ %s ] private [ %s ] : %s", sender.getNickname(), receiver, msg);

        for (ClientHandlerTest c : clients) {
            if (c.getNickname().equals(receiver)) {
                c.sendMsg(message);
                if (!c.equals(sender)) {
                    sender.sendMsg(message);
                }
                return;
            }
        }
        sender.sendMsg("Not found user: " + receiver);
    }

    public void subscribe(ClientHandlerTest clientHandlerTest) {
        clients.add(clientHandlerTest);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandlerTest clientHandlerTest) {
        clients.remove(clientHandlerTest);
        broadcastClientList();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isloginAuthenticated(String login){
        for (ClientHandlerTest c : clients) {
            if(c.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public void broadcastClientList(){
        StringBuilder sb = new StringBuilder("/clientlist ");
        for (ClientHandlerTest c : clients) {
            sb.append(c.getNickname()).append(" ");
        }

        String msg = sb.toString();
        for (ClientHandlerTest c : clients) {
            c.sendMsg(msg);
        }
    }
}
