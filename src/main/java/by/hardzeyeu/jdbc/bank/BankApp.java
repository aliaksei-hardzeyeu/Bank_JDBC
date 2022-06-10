package by.hardzeyeu.jdbc.bank;

public class BankApp {


    public static void main(String[] args) {
        AccountLogic accountLogic = new AccountLogic();
        UserLogic userLogic = new UserLogic();

        User user = userLogic.chooseOrCreateUser(userLogic.anyUserExists());

        Account account = accountLogic.chooseOrCreateAccount(user);

        account.operationsWithAccount();

        System.out.println("main commit 1 before branching feature");

        System.out.println("main commit 2 after branching feature");
        System.out.println("main commit 3 after branching feature");
    }
}


