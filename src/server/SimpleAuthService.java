package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService {
    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private  List<UserData> users2;
    private List<UserData> users;

    // 1. Добавить в сетевой чат авторизацию через базу данных SQLite.
    // делаем подключение к БД:
   // static Connection postgresConnection;
    static {
        try {
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public SimpleAuthService() {
        users = new ArrayList<>();
        users2 = new ArrayList<>();
        // подготовка к добавлению БД
        // 1. Добавить в сетевой чат авторизацию через базу данных SQLite.
        // 2.*Добавить в сетевой чат возможность смены ника.
        users.add(new UserData("qwe", "qwe", "qwe"));
        users.add(new UserData("asd", "asd", "asd"));
        users.add(new UserData("zxc", "zxc", "zxc"));
        users.add(new UserData("aaa", "aaa", "aaa"));
        for (int i = 1; i <= 10; i++) {
            users.add(new UserData("login" + i, "pass" + i, "nick" + i));
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        // подготовка к добавлению БД
        // 1. Добавить в сетевой чат авторизацию через базу данных SQLite.
        // 2.*Добавить в сетевой чат возможность смены ника.

        String nickname = null;
        try {

            // Connection класс подключения DriverManager.getConnection () - подкл к базе
            Connection postgresConnection = DriverManager.getConnection("jdbc:postgresql:GB","postgres","123");

            // Statemanet - для выполнения запроса
            Statement statement= postgresConnection.createStatement();

            // выполнить запрос:


            ResultSet resultSet = statement.executeQuery("select DISTINCT nickname from authbd where login = '"+ login +"' and pass = '"+ password +"'");
            while(resultSet.next())
            {
                if (!resultSet.getString("nickname").isEmpty())
                    nickname=resultSet.getString("nickname");
                System.out.println("Такой пользователь существует, его ник: " + nickname);}
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return nickname;
    }


    @Override
    public boolean registration(String login, String password, String nickname) {
        for (UserData user : users) {
            if(user.login.equals(login) || user.nickname.equals(nickname)){
                return false;
            }
        }

        users.add(new UserData(login, password, nickname));
        return true;
    }
}
