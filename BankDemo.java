import java.util.Scanner;
import java.util.Random;

//withdrawal amount as a parameter
class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(double amount, double balance) {
        super("Insufficient balance. You tried to withdraw Rs." + amount + ", but only Rs." + balance + " is available.");
    }
}



//account class with synchronization for thread safety
class Account {
       private final int accountNumber;
       private double balance;

       public Account(int accountNumber, double initialBalance) {
              this.accountNumber = accountNumber;
              this.balance = initialBalance;
       }

       public synchronized double getBalance() {
              return balance;
       }

       public synchronized void displayBalance() {
              System.out.printf("Balance: Rs.%.2f%n", balance);
       }

       public synchronized void deposit(double amount) {
              balance += amount;
       }

       public synchronized void withdraw(double amount) throws InsufficientBalanceException {
              if (amount > balance) {
                 throw new InsufficientBalanceException(amount, balance);
              }
              balance -= amount;
       }

}

//joint account class
class JointAccount {
    private double balance;

    public JointAccount(double initialBalance) {
        this.balance = initialBalance;
    }

    //deposit
    public synchronized void deposit(double amount) {
        balance += amount;
    }

    //withdraw
    public synchronized void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > balance) {
            throw new InsufficientBalanceException(amount, balance);
        }
        balance -= amount;
    }

    //balance
    public synchronized double getBalance() {
        return balance;
    }
}


//runnable
class UserTask implements Runnable {
    private final JointAccount jointAccount;
    private final Random random = new Random();

    public UserTask(JointAccount jointAccount) {
        this.jointAccount = jointAccount;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        for (int i = 0; i < 5; i++) {
            double amount = Math.round(random.nextDouble() * 100 * 100) / 100.0; // Rs.0 to Rs.100
            if (random.nextBoolean()) {
                jointAccount.deposit(amount);
                System.out.printf("%s deposited Rs.%.2f%n", name, amount);
            } else {
                try {
                    jointAccount.withdraw(amount);
                    System.out.printf("%s withdrew Rs.%.2f%n", name, amount);
                } catch (InsufficientBalanceException e) {
                    System.out.println(name + "ERROR! " + e.getMessage());
                }
            }
            try {
                Thread.sleep(random.nextInt(50)); // let threads interleave
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}

//joint account demo class
class JointAccountDemo {
    public static void main(String[] args) throws InterruptedException {
        JointAccount account = new JointAccount(1000);

        Thread t1 = new Thread(new UserTask(account), "User_1");
        Thread t2 = new Thread(new UserTask(account), "User_2");
        Thread t3 = new Thread(new UserTask(account), "User_3");

        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        System.out.printf("Final balance: Rs.%.2f%n", account.getBalance());
    }
}


public class BankDemo {
    private static final Scanner in = new Scanner(System.in);

    //withdrawal attempt
    private static boolean attemptWithdrawal(Account account, double amount){
            try {
                account.withdraw(amount);
                System.out.println("Withdrawal Successful~");
                return true;
            } catch (InsufficientBalanceException e){
                System.out.println("ERROR!! " + e.getMessage());
                return false;
            } finally {
                account.displayBalance();
            }
    }

    //asking permission
    private static boolean askToContinue() {
            System.out.print("Do you wish to continue? (yes/no): ");
            return in.next().equalsIgnoreCase("yes");  //ignoring if yes
    }


    //main
    public static void main(String[] args) {
        Account account = new Account(12345, 1000);
        Thread[] threads = new Thread[3];
        Scanner in = new Scanner(System.in);

        //picking user
        for(int i = 0; i < threads.length; i++){
           threads[i] = new Thread(new BankTask(account), "User_" + (i + 1));
           threads[i].start();
        }

        for( Thread t : threads) {
           try{
              t.join();
           } catch (InterruptedException e){
                   System.out.println("Thread was interrupted! " + e.getMessage());
           }
        }

        boolean keepGoing = true;
        while (keepGoing) {
              System.out.print("Enter withdrawal amount : Rs.");
              double amount = in.nextDouble();
              boolean success = false;
              try {
                  success = attemptWithdrawal(account, amount);
              } finally {
                  keepGoing = askToContinue();
              }
              if(keepGoing && success) {
                account.deposit(amount);
                System.out.println("Money was deposited back into the account...");
                account.displayBalance();
              }
        }

        System.out.println("Thank you for banking with us~");
    }
}
