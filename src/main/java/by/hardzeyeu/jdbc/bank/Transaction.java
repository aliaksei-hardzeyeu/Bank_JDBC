package by.hardzeyeu.jdbc.bank;

public class Transaction {
    private int transactionId;
    private int accountId;
    private double amount;

    public Transaction(int accountId) {
        this.accountId = accountId;
        this.amount = 0;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


}
