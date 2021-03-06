package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Controller implements Initializable {





    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private HBox authPanel;
    @FXML
    private HBox msgPanel;
    @FXML
    private ListView<String> clientList;

    private Socket socket;
    private final String IP_ADDRESS = "localhost";
    private final int PORT = 8193;

    private DataInputStream in;
    private DataOutputStream out;

    private boolean authenticated;
    private String nickname;
    private Stage stage;
    private Stage regStage;
    private RegController regController;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        msgPanel.setManaged(authenticated);
        msgPanel.setVisible(authenticated);
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        clientList.setManaged(authenticated);
        clientList.setVisible(authenticated);
        if (!authenticated) {
            nickname = "";
        }
        setTitle(nickname);
        textArea.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createRegWindow();
        Platform.runLater(() -> {
            stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                System.out.println("bye");
                if (socket != null && !socket.isClosed()) {
                    try {
                        out.writeUTF("/end");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
        setAuthenticated(false);
    }

    private void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //???????? ????????????????????????????
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals("/regok")) {
                                regController.addMessage("?????????????????????? ???????????? ??????????????");
                            }
                            if (str.equals("/regno")) {
                                regController.addMessage("?????????????????????? ???? ????????????????????\n" +
                                        "???????????????? ???????????????????????? ???????? ?????? ?????????????? ?????? ????????????");
                            }

                            if (str.startsWith("/authok ")) {
                                nickname = str.split("\\s")[1];
                                setAuthenticated(true);
                                break;
                            }

                        } else {
                            textArea.appendText(str + "\n");
                        }
                    }

                    //???????? ????????????
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.startsWith("/clientlist ")) {
                                String[] token = str.split("\\s");
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < token.length; i++) {
                                        clientList.getItems().add(token[i]);
                                    }
                                });
                            }
                            if (str.equals("/end")) {
                                break;
                            }
                        } else {
                            textArea.appendText(str + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    setAuthenticated(false);
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

    @FXML
    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void changeNick() {

        OutputStream a;


        // 2.*???????????????? ?? ?????????????? ?????? ?????????????????????? ?????????? ????????.

        String nickname2 = "NewNick";
        if (!textField.getText().isEmpty())
            nickname2 = textField.getText();

            try {
            // Connection ?????????? ?????????????????????? DriverManager.getConnection () - ?????????? ?? ????????
            Connection postgresConnection = DriverManager.getConnection("jdbc:postgresql:GB","postgres","123");

            // Statemanet - ?????? ???????????????????? ??????????????
            Statement statement= postgresConnection.createStatement();

           // int result= statement.executeUpdate("update authbd set nickname='"+nickname2+"' where login = 'a'");
           //     System.out.println("result" + result);
            // !!! ???? ???????????????????? ?????? ?????????????????? ?????????? ?????? ???????????? ????????
                PreparedStatement ps = postgresConnection.prepareStatement("update authbd set nickname= ? where login = 'a'");
                        ps.setString(1,nickname2);
                ps.execute();

        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        setTitle(nickname2);
        textField.clear();





    }

    @FXML
    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        String msg = String.format("/auth %s %s", loginField.getText().trim(), passwordField.getText().trim());
        try {
            out.writeUTF(msg);
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(String username) {
        String title = String.format("???????????????? [ %s ]", username);
        if (username.equals("")) {
            title = "????????????????";
        }
        String chatTitle = title;
        Platform.runLater(() -> {
            stage.setTitle(chatTitle);
        });
    }

    @FXML
    public void clickClientlist(MouseEvent mouseEvent) {
        String msg = String.format("/w %s ", clientList.getSelectionModel().getSelectedItem());
        textField.setText(msg);
    }

    private void createRegWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reg.fxml"));
            Parent root = fxmlLoader.load();
            regStage = new Stage();
            regStage.setTitle("???????????????? ??????????????????????");
            regStage.setScene(new Scene(root, 350, 300));
            regStage.initModality(Modality.APPLICATION_MODAL);

            regController = fxmlLoader.getController();
            regController.setController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void showRegWindow(ActionEvent actionEvent) {
        regStage.show();
    }

    public void tryToReg(String login, String password, String nickname) {
        String msg = String.format("/reg %s %s %s", login, password, nickname);

        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
