package src;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Date;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;

    public AccountManager(Connection connection,Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }
      /*credit money method credits the money from the user's account */
     public void credit_money(long account_number)throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();

        while (security_pin.isEmpty()) {

            System.out.print("\nEnter Security Pin: :");
            security_pin = scanner.nextLine();

        }

        try {
            connection.setAutoCommit(false);
            if(account_number != 0) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ? ");
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, account_number);
                    int rowsAffected = preparedStatement1.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Rs."+amount+" credited Successfully");
                        getBalance(account_number);

try {
                            String email = "";
                            String query = "select email from accounts where account_number=?";

                            PreparedStatement ps = connection.prepareStatement(query);
                            ps.setLong(1, account_number);
                            ResultSet rs = ps.executeQuery();

                            if (rs.next()) {
                                email = rs.getString("email");
                            }

                            DateFormat dt = DateFormat.getDateInstance(DateFormat.DEFAULT);
                            Date d = new Date();
                            String datee = dt.format(d);

                            SimpleDateFormat x = new SimpleDateFormat("h:mm a");
                            String time = x.format(d);

                            PreparedStatement ps1 = connection.prepareStatement(
                                    "insert into logs (account_number,email,activity,amount,date,time) value (?,?,?,?,?,?)");
                            ps1.setLong(1, account_number);
                            ps1.setString(2, email);
                            ps1.setString(3, "credit");
                            ps1.setDouble(4, amount);
                            ps1.setString(5, datee);
                            ps1.setString(6, time);
                            int res = ps1.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    } else {
                        System.out.println("Transaction Failed!");
                        credit_money(account_number);
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                }else{
                    System.out.println("Invalid Security Pin!");
                    credit_money(account_number);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    /*debit money method debits the money from the user's account */
    public void debit_money(long account_number) throws SQLException{
     scanner.nextLine();
        System.out.print("enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("enter security pin: ");
        String security_pin= scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            if(account_number!=0){
                PreparedStatement preparedStatement = connection.prepareStatement("select * from Accounts where account_number=? and security_pin = ?");
                preparedStatement.setLong(1,account_number);
                preparedStatement.setString(2,security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                double current_balance = resultSet.getDouble("balance");
                if (amount<=current_balance){
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, account_number);
                        int rowsAffected = preparedStatement1.executeUpdate();
                        if (rowsAffected > 0) {

                        
                            try {
                                String email = "";
                                String query = "select email from accounts where account_number=?";

                                PreparedStatement ps = connection.prepareStatement(query);
                                ps.setLong(1, account_number);
                                ResultSet rs = ps.executeQuery();

                                if (rs.next()) {
                                    email = rs.getString("email");
                                }

                                DateFormat dt = DateFormat.getDateInstance(DateFormat.DEFAULT);
                                Date d = new Date();
                                String datee = dt.format(d);

                                SimpleDateFormat x = new SimpleDateFormat("h:mm a");
                                String time = x.format(d);

                                PreparedStatement ps1 = connection.prepareStatement(
                                        "insert into logs (account_number,email,activity,amount,date,time) value (?,?,?,?,?,?)");
                                ps1.setLong(1, account_number);
                                ps1.setString(2, email);
                                ps1.setString(3, "debit ");
                                ps1.setDouble(4, amount);
                                ps1.setString(5, datee);
                                ps1.setString(6, time);

                                int res = ps1.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            System.out.println("Rs."+amount+" debited Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else{
                        System.out.println("Insufficient Balance!");
                    }
                }else{
                    System.out.println("Invalid Pin!");
                    debit_money(account_number);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    /* transfer of money from one account to other*/
     public void transfer_money(long sender_account_number) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Receiver Account Number: ");
        long receiver_account_number = scanner.nextLong();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try{
            connection.setAutoCommit(false);
            if(sender_account_number!=0 && receiver_account_number!=0){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ? ");
                preparedStatement.setLong(1, sender_account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount<=current_balance){

                        // Write debit and credit queries
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                        String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";

                        // Debit and Credit prepared Statements
                        PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);

                        // Set Values for debit and credit prepared statements
                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, receiver_account_number);
                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(2, sender_account_number);
                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();
                        if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                            System.out.println("Transaction Successful!");
                            System.out.println("Rs."+amount+" Transferred Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else{
                        System.out.println("Insufficient Balance!");
                    }
                }else{
                    System.out.println("Invalid Security Pin!");
                    transfer_money(sender_account_number);
                }
            }else{
                System.out.println("Invalid account number");
                transfer_money(sender_account_number);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    /* find the balance of the account */
    public void getBalance(long account_number){
        
        try{
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1, account_number);
        
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                double balance = resultSet.getDouble("balance");
                String bal = String.format("%.2f",balance);
                System.out.println("Balance: "+bal);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
