package by.hardzeyeu.jdbc.bank;

public class BankApp {


    public static void main(String[] args) {
        AccountLogic accountLogic = new AccountLogic();
        UserLogic userLogic = new UserLogic();

        User user = userLogic.chooseOrCreateUser(userLogic.anyUserExists());

        Account account = accountLogic.chooseOrCreateAccount(user);

        account.operationsWithAccount();
    }
}


