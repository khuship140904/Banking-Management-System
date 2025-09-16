package src;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class User {
    /*these variables are intialized in all the classes using constructor*/
    private Connection connection;
    private Scanner scanner;
    static String name ;
    static String email;
    static String password;

    public User(Connection connection,Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }

    /* this method is for registration of the user */
    public void register(){

        scanner.nextLine();
        System.out.print("\nfullname: ");
        String full_name= scanner.nextLine();

    while(full_name.isEmpty()){
        System.out.print("\nPlease enter your fullname: ");
        full_name= scanner.nextLine();
    }
        
    
        System.out.print("\nemail: ");
        String email= scanner.nextLine();
        while(email.isEmpty()){
        System.out.print("\nPlease enter your email: ");
        email= scanner.nextLine();
    }
        
        System.out.print("\npassword: ");
        String password= scanner.nextLine();
        while(password.isEmpty()){
        System.out.print("\nPlease enter your password: ");
        password= scanner.nextLine();
    }

        /*check whether the user already exists */
        if(user_exists(email)){
            System.out.println("user already exists for this email id!!!");
            return;
        }


        /* query for the insertion of the user data in the db */
        String registerQuery="insert into user(full_name, email, password) values(?,?,?)";

        try {
            PreparedStatement preparedStatement=connection.prepareStatement(registerQuery);
            preparedStatement.setString(1,full_name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,password);

            int affectedRows=preparedStatement.executeUpdate();

            /*checks if all the entries are filled by the user */

            if(affectedRows>0){
                System.out.println("registration completed!!");
            }
            else{
                System.out.println("registration failed");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /* this is the method for the login of the user */

    public String login(){

        String log="login";
        scanner.nextLine();

        System.out.print("\nemail: ");
        String email=scanner.nextLine();

        while(email.isEmpty()){
        
            System.out.print("\nPlease Enter your email :");
            email=scanner.nextLine();

        }

        System.out.print("\npassword: ");
        String password=scanner.nextLine();

        while(password.isEmpty()){
        
            System.out.print("\n Please Enter your password :");
            password=scanner.nextLine();

        }

        String login_query = "select * from user where email=? and password=?";
        try{
        PreparedStatement preparedStatement=connection.prepareStatement(login_query);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                try
                {
                    DateFormat dt = DateFormat.getDateInstance(DateFormat.DEFAULT);
                    Date d = new Date();
                    String datee = dt.format(d);

                    SimpleDateFormat x = new SimpleDateFormat("h:mm a");
                    String time = x.format(d);

                    PreparedStatement ps = connection.prepareStatement("insert into logs (account_number,email,activity,amount,date,time) value (?,?,?,?,?,?)");
                    ps.setInt(1, 0);
                    ps.setString(2, email);
                    ps.setString(3, log);
                    ps.setInt(4, 0);
                    ps.setString(5, datee);
                    ps.setString(6, time);

                    int res = ps.executeUpdate();

                } catch(SQLException e)
                {e.printStackTrace();}

                return email;
            }
            else{
                return null;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    return null;
    }

    /* this method tells us whether the user already exists or not */

    public boolean user_exists(String email){
        String query = "select * from user where email=?";
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
     public  void get_history(String email)
    {   
        try{
        String query="select account_number from accounts where email=?";

        PreparedStatement ps1 = connection.prepareStatement(query);
        ps1.setString(1,email);
        
        ResultSet rs= ps1.executeQuery();

        Long account_number=null;

        if(rs.next()){account_number=rs.getLong("account_number");}



        query="select account_number,email,activity,amount,date,time from logs where account_number=? and activity != 'login'";
        PreparedStatement ps =  connection.prepareStatement(query);
        ps.setLong(1, account_number);
        
        ResultSet rs1=ps.executeQuery();

        while(rs1.next()){
            System.out.println(" "+rs1.getLong("account_number")+" |  "+rs1.getString("email")+" |  "+rs1.getString("activity")+" | RS. "+rs1.getInt("amount")+" |  "+rs1.getString("date")+" |  "+rs1.getString("time")  );
        }
        
    
        }catch(SQLException e){e.printStackTrace();}
    }
}
