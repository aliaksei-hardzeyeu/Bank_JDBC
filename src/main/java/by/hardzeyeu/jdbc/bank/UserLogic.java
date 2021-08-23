package by.hardzeyeu.jdbc.bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class UserLogic {
    private final Connection connection;
    private final BufferedReader reader;
    private int userCount;


    {
        DBWorker dbWorker = new DBWorker();
        connection = dbWorker.getConnection();
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Даёт на выбор: создать нового или выбрать существующего пользователя(если они существуют).
     *
     * @param anyUserExists
     * @return выбранный на данный момент user
     */
    public User chooseOrCreateUser(boolean anyUserExists) {
        if (!anyUserExists) {
            System.out.println("Ни один пользователь не существует. Создавайте нового.");
            return createNewUser();
        } else {
            System.out.println("Хотите создать нового пользователя или использовать существующего? 1 - новый, 2 - существующий, любая другая кнопка - выход");
            try {
                String answer = reader.readLine();
                if (answer.equals("1")) {
                    return createNewUser();
                } else if (answer.equals("2")) {
                    return chooseUser();
                } else {
                    throw new Exception();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Выход из программы");
            }
        }
        return null;
    }

    /**
     * Проверяет, есть ли в БД хоть один существующий пользователь.
     * Заносит в переменную userCount количество существующих пользователей.
     *
     * @return true если хоть один пользователь существует, false если не существует.
     */
    public boolean anyUserExists() {
        boolean anyUserExists = true;
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from users;");

            while (result.next()) {
                userCount++;
            }
            if (userCount == 0) {
                anyUserExists = false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return anyUserExists;
    }

    /**
     * Выбираем пользователя из существующих.
     *
     * @return выбранного пользователя
     */
    public User chooseUser() {
        User user = null;
        String userName;

        try {
            Statement statement = connection.createStatement();
            ResultSet rs1 = statement.executeQuery("select namen from users;");

            System.out.println("Список пользователей:");
            while (rs1.next()) {
                System.out.println(rs1.getString(1));
            }

            System.out.println("Введите имя нужного пользователя:");
            userName = reader.readLine();

            PreparedStatement preparedStatement = connection.prepareStatement("select * from users where namen = ?;");
            preparedStatement.setString(1, userName);
            ResultSet rs2 = preparedStatement.executeQuery();

            while (rs2.next()) {
                user = new User();
                user.setId(rs2.getInt(1));
                user.setName(rs2.getString(2));
                user.setAddress(rs2.getString(3));
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Создаёт нового пользователя
     *
     * @return user
     */
    public User createNewUser() {
        User user = new User();
        String userName;
        String userAddress;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Введите имя");
            userName = reader.readLine();
            System.out.println("Введите адрес");
            userAddress = reader.readLine();

            PreparedStatement preparedStatement;
            ResultSet rs;

            preparedStatement = connection.prepareStatement("insert into users (namen, address) values(?, ?);");
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, userAddress);
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("select * from users where namen = ?;");
            preparedStatement.setString(1, userName);
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                user.setId(rs.getInt(1));
                user.setName(rs.getString(2));
                user.setAddress(rs.getString(3));
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return user;
    }
}
