package by.hardzeyeu.jdbc.bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Account {
    private int accountId;
    private int userId;
    private double balance;
    private String currency;
    private final BufferedReader reader;
    boolean anotherTry;
    Connection connection;

    {
        reader = new BufferedReader(new InputStreamReader(System.in));
        DBWorker dbWorker = new DBWorker();
        connection = dbWorker.getConnection();
    }

    /**
     * Метод выполняет операции (транзакции, просмотр баланса) по счету
     *
     * @return выполненную транзакцию
     */
    public Transaction operationsWithAccount() {
        Transaction transaction = null;

        try {
            do {
                System.out.println("Введите тип операции: 1 - пополнить баланс, 2 - снять деньги, 3 - посмотреть баланс, любая другая кнопка - выход");
                String answer = reader.readLine();
                if (answer.equals("1")) {
                    System.out.println("Введите сумму");
                    String sum = reader.readLine();
                    transaction = topUpBalance(sum);
                    anotherTry = true;
                } else if (answer.equals("2")) {
                    System.out.println("Введите сумму");
                    String sum = reader.readLine();
                    transaction = withdrawFromAccount(sum);
                    anotherTry = true;
                } else if (answer.equals("3")) {
                    System.out.println("Баланс равен " + getBalance());
                    anotherTry = true;
                } else {
                    System.out.println("Выход из программы");
                    anotherTry = false;
                }
            } while (anotherTry);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return transaction;
    }

    /**
     * Пополняет баланс, создавая транзакцию ограничение: сумма должна быть не более 2ккк.
     * Если ограничение проходит, то делает запись в БД.
     *
     * @param sum сумма операции
     * @return транзакцию, которая записалась в БД
     */
    public Transaction topUpBalance(String sum) {
        Transaction transaction = new Transaction(getAccountId());
        double sumDouble = Double.parseDouble(sum);
        transaction.setAmount(sumDouble);
        if ((getBalance() + transaction.getAmount()) >= 2_000_000_000) {
            System.out.println("Баланс не может быть больше 2ккк. Попробуйте еще раз.");
            anotherTry = true;
        } else {
            anotherTry = false;
            setBalance(getBalance() + transaction.getAmount());
            writeTransactionToDB(transaction);
        }
        return transaction;
    }

    /**
     * Снимает деньги со счета, ограничение: баланс не должен быть отрицательным.
     * Если ограничение проходит, то делает запись в БД.
     *
     * @param sum сумма операции
     * @return транзакцию, которая записалась в БД
     */
    public Transaction withdrawFromAccount(String sum) {
        Transaction transaction = new Transaction(getAccountId());
        double sumDouble = Double.parseDouble(sum);
        transaction.setAmount(sumDouble);
        if ((getBalance() - transaction.getAmount()) < 0) {
            System.out.println("Не хватает средств на счёте. Попробуйте еще раз.");
            anotherTry = true;
        } else {
            anotherTry = false;
            setBalance(getBalance() - transaction.getAmount());
            writeTransactionToDB(transaction);
        }
        return transaction;
    }

    /**
     * Записывает транзакцию в БД
     *
     * @param transaction
     */
    public void writeTransactionToDB(Transaction transaction) {
        PreparedStatement preparedStatement1;
        PreparedStatement preparedStatement2;
        try {
            preparedStatement1 = connection.prepareStatement("update accounts set balance = ? where accountsid = ?;");
            preparedStatement1.setDouble(1, getBalance());
            preparedStatement1.setInt(2, getAccountId());
            preparedStatement1.executeUpdate();
            preparedStatement2 = connection.prepareStatement("insert into transactions (accountsid, amount) values (?, ?);");
            preparedStatement2.setInt(1, getAccountId());
            preparedStatement2.setDouble(2, transaction.getAmount());
            preparedStatement2.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "by.hardzeyeu.jdbc.bank.Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                '}';
    }
}
