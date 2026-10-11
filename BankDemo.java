import java.util.Scanner;

//withdrawal amount as a parameter
class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(double amount, double balance) {
        super("Insufficient balance. You tried to withdraw Rs." + amount + ", but only Rs." + balance + " is available.");
    }
}



//account class
class Account {
       private final int accountNumber;
       private double balance;

       public Account(int accountNumber, double initialBalance) {
              this.accountNumber = accountNumber;
              this.balance = initialBalance;
       } 

       public int getAccountNumber() {
              return accountNumber;
       }
       public void displayBalance() {
              System.out.println("Balance: Rs." + balance);
       }

       //deposit
       public void deposit(double amount) {
              balance += amount;
       }

       //withdraw
       public void withdraw(double amount) throws InsufficientBalanceException {
              if(amount > balance ) {
                throw new InsufficientBalanceException(amount, balance);
              }
              balance -= amount;
       }

}


public class BankDemo {
    public static void main(String[] args) {
        Account account = new Account(12345, 1000);
        Scanner in = new Scanner(System.in);

        System.out.println("Account Number: " + account.getAccountNumber());
        System.out.print("Initial ");
        account.displayBalance();
        System.out.print("Enter deposit amount: Rs.");
        account.deposit(in.nextDouble());
        account.displayBalance();

        System.out.print("Enter withdrawal amount: Rs.");

        try {
            account.withdraw(in.nextDouble());
            account.displayBalance();
        } catch (InsufficientBalanceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
