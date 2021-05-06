package server;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandlerTest {

    private ServerTest serverTest;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    /*private DataInputStream in;
    private DataOutputStream out;
*/
    private String nickname;
    private String login;

    // все подключения клиентов
    public ClientHandlerTest(ServerTest server, Socket socket) {
        try {
            this.serverTest = server;
            this.socket = socket;

             in=new DataInputStream(socket.getInputStream());
             out=new DataOutputStream(socket.getOutputStream());

          /*  in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());*/

            new Thread(() -> {
                try {
                    socket.setSoTimeout(5000);
                    //цикл аутентификации
                    while (true) {
                        String str = in.readUTF();
                        String str2 = in.readUTF();

                        if (str2.startsWith("/"))
                            if (str2.startsWith("/reg"))
                            // что за разделитель \\s ?
                            {
                                String[] token2=str2.split("\\s",4);
                            boolean a=server.getAuthService().registration(token2[1],token2[2],token2[3]);
                            if (a){
                                sendMsg("Норм");
                            } else {sendMsg("Не норм");}
                            };

                            if (str2.startsWith("/auth"))
                        {
                            String[] token2=str2.split("\\s",3);
                            String newNick = server.getAuthService().getNicknameByLoginAndPassword(token2[1],token2[2]);
                            if (newNick!=null)
                            {
                                login=token2[1];
                                if (!server.isloginAuthenticated(login))
                                {nickname=newNick;
                                out.writeUTF(("Нормик" + nickname));
                                server.subscribe(this);
                                }

                            }

                        }


                        if (str.startsWith("/auth ")) {
                            String[] token = str.split("\\s", 3);
                            String newNick = server.getAuthService().getNicknameByLoginAndPassword(token[1], token[2]);
                            if (newNick != null) {
                                login = token[1];
                                if (!server.isloginAuthenticated(login)) {
                                    nickname = newNick;
                                    out.writeUTF("/authok " + nickname);
                                    server.subscribe(this);
                                    break;
                                } else {
                                    out.writeUTF("Учетная запись уже используется");
                                }
                            } else {
                                out.writeUTF("Неверный логин / пароль");
                            }
                        }

                        /*if (str.startsWith("/")) {

                            *//*if (str.startsWith("/reg ")) {
                                String[] token = str.split("\\s", 4);
                                boolean b = server.getAuthService()
                                        .registration(token[1], token[2], token[3]);
                                if (b) {
                                    sendMsg("/regok");
                                } else {
                                    sendMsg("/regno");
                                }
                            }*//*


                        }*/
                    }

                    //Цикл работы
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.startsWith("/w")) {
                                String[] token = str.split("\\s+", 3);
                                if (token.length < 3) {
                                    continue;
                                }
                                server.privateMsg(this, token[1], token[2]);
                            }

                            if (str.equals("/end")) {
                                out.writeUTF("/end");
                                break;
                            }
                        } else {
                            server.broadcastMsg(this, str);
                        }
                    }
                    //catch SocketTimeoutException
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Client disconnected!");
                    server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }
}
