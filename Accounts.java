import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Accounts {
    private Connection connection;
    private Scanner scanner;

    public Accounts(Connection connection,Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }
/*used for opening account in the bank */
    public long openAccount(String email){
        if (!account_exists(email)) {
        String open_account_query = "insert into Accounts(account_number,full_name,email,balance,security_pin) values (?,?,?,?,?)";
        scanner.nextLine();
        System.out.print("enter fullname: ");
        String full_name= scanner.nextLine();
        System.out.print("enter Initial Amount: ");
        String balance = scanner.nextLine();
        System.out.print("enter security pin: ");
        String security_pin= scanner.nextLine();

        try {
            long account_number = generateAccountNumber();
            PreparedStatement preparedStatement=connection.prepareStatement(open_account_query);
            preparedStatement.setLong(1,account_number);
            preparedStatement.setString(2,full_name);
            preparedStatement.setString(3,email);
            preparedStatement.setString(4,balance);
            preparedStatement.setString(5,security_pin);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows>0) {
                return account_number;
            } else{
                throw new RuntimeException("account creation failed");
            }
        } catch (SQLException e) {
           e.printStackTrace();
        }
    }
    throw new RuntimeException("account already exists");
  }
    /*if we want the account number of any user we will use this method */
    public long getAccount_number(String email){
        String query ="select account_number from Accounts where email = ?";
        try {
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,email);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
            return resultSet.getLong("account_number");                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Account Number Doesn't Exist!");
    }
    
    
    
    /*if we want to generate new account number of any user we will use this method */
    public long generateAccountNumber(){
        try {
            Statement statement= connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select account_number from Accounts order by account_number desc LIMIT 1");
            if(resultSet.next()){
                long last_account_number = resultSet.getLong("account_number");
                return last_account_number+1;
            }
            else{
                return 10000100;
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 10000100;
    }




/*check whether the account exists in the database or not */
    public boolean account_exists(String email){
        String query ="select account_number from accounts where email = ?";
        try {
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,email);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
            else{
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
}
