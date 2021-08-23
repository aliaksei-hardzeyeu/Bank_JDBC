package by.hardzeyeu.jdbc.bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountLogic {
    private final Connection connection;
    private final BufferedReader reader;

    {
        DBWorker dbWorker = new DBWorker();
        connection = dbWorker.getConnection();
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Даёт на выбор: создать новый или выбрать существующий счет (если они существуют).
     *
     * @param user пользователь, чьи счета просматриваем или создаём
     * @return выбранный или вновь созданный счет
     */
    public Account chooseOrCreateAccount(User user) {
        Account account = null;
        if (anyAccountExists(user)) {
            System.out.println("Хотите выбрать существующий счет или создать новый? 1 - существующий, 2 - новый, любая другая кнопка - выход");
            try {
                String answer = reader.readLine();
                if (answer.equals("1")) {
                    account = chooseAccount(user);
                } else if (answer.equals("2")) {
                    account = createNewAccount(user);
                } else {
                    throw new Exception();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Выход из программы");
            }
        } else {
            System.out.println("Существующих счетов нет, создавайте новый");
            account = createNewAccount(user);

        }
        return account;
    }

    /**
     * Проверяет, существует ли хоть один счет у данного пользователя
     *
     * @param user
     * @return true если существуют, false если не существуют
     */
    public boolean anyAccountExists(User user) {
        int accountCount = 0;
        boolean anyAccountExists = true;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from accounts where userid = ?;");
            preparedStatement.setInt(1, user.getId());
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                accountCount++;
            }
            if (accountCount == 0) {
                anyAccountExists = false;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return anyAccountExists;
    }

    /**
     * Выбираем существующий счет
     *
     * @param user
     * @return выбранный счет
     */
    public Account chooseAccount(User user) {
        Account account = null;
        String currency;
        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement("select currency from accounts where userid = ?;");
            preparedStatement1.setInt(1, user.getId());
            ResultSet rs1 = preparedStatement1.executeQuery();

            System.out.println("Список счетов по валютам:");
            while (rs1.next()) {
                System.out.println(rs1.getString(1));
            }

            System.out.println("Введите валюту нужного счета:");
                currency = reader.readLine();

            PreparedStatement preparedStatement = connection.prepareStatement("select * from accounts where currency = ?;");
            preparedStatement.setString(1, currency);
            ResultSet rs2 = preparedStatement.executeQuery();

            while (rs2.next()) {
                account = new Account();
                account.setAccountId(rs2.getInt(1));
                account.setUserId(rs2.getInt(2));
                account.setBalance(rs2.getDouble(3));
                account.setCurrency(rs2.getString(4));
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return account;
    }

    /**
     * Создаёт новый счет
     *
     * @param user
     * @return вновь созданный счет
     */
    public Account createNewAccount(User user) {
        Account account = null;
        boolean anotherTry = false;
        String currency;
        do {
            try {
            System.out.println("Введите валюту");

                currency = reader.readLine();
                if (ifSuchCurrencyExists(user, currency)) {
                    System.out.println("Счет с такой валютой уже есть, введите другую валюту");
                    anotherTry = true;
                } else {
                    account = new Account();
                    account.setCurrency(currency);
                    anotherTry = false;

                    PreparedStatement preparedStatement;
                    ResultSet rs = null;
                    preparedStatement = connection.prepareStatement("insert into accounts (userid, balance, currency) values(?, ?, ?);");
                    preparedStatement.setInt(1, user.getId());
                    preparedStatement.setInt(2, 0);
                    preparedStatement.setString(3, currency);
                    preparedStatement.execute();

                    preparedStatement = connection.prepareStatement("select * from accounts where currency = ?;");
                    preparedStatement.setString(1, currency);
                    rs = preparedStatement.executeQuery();

                    while (rs.next()) {
                        account.setAccountId(rs.getInt(1));
                        account.setUserId(rs.getInt(2));
                        account.setBalance(rs.getInt(3));
                        account.setCurrency(rs.getString(4));
                    }
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }

        } while (anotherTry);

        return account;
    }

        /**
         * Проверяет, существует ли счет с такой валютой
         *
         * @param user
         * @param currency
         * @return
         */
        public boolean ifSuchCurrencyExists (User user, String currency){
            boolean ifSuchCurrencyExists = false;
            List<String> currencyList = new ArrayList<>();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select currency from accounts where userid = ?;");
                preparedStatement.setInt(1, user.getId());
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    currencyList.add(rs.getString(1));
                }

                if (currencyList.contains(currency)) {
                    ifSuchCurrencyExists = true;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return ifSuchCurrencyExists;
        }
    }
