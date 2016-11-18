import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by jwnicholson on 11/18/2016.
 */
public class Main
{
    public static final int MYSQL_DUPLICATE_PK = 1062;
    public static final int MYSQL_TABLE_DOES_NOT_EXIST = 1146;
    public static final int MYSQL_COLUMN_DOES_NOT_EXIST = 1054;
    public static void main (String [] args)
    {
        Connection con = null;
        Statement stmt = null;
        int choice = 0;
        Scanner scan = new Scanner(System.in);
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/idealhome", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        while(choice != 1)
        {
            System.out.println("Please select an option from the list below. \n 1. Quit \n 2. Query the database \n 3. Insert data \n 4. Delete data \n 5. Check metadata");
            choice = Integer.parseInt(scan.nextLine());

            switch (choice) {
                case 1:
                    break;
                case 2:
                    System.out.println("Enter your SQL query:");
                    String query = scan.nextLine();
                    try {
                        stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        while (rs.next())
                        {

                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        stmt = con.createStatement();
                        System.out.println("Enter the table you wish to insert data into. (Case sensitive)");
                        String table = scan.nextLine();
                        System.out.println("Enter all applicable information separated by commas and make sure VARCHAR variables are surrounded by single quotation marks.");
                        String values = scan.nextLine();
                        query = "INSERT INTO " + table + " VALUES (" + values + ")";
                        stmt.executeUpdate(query);
                    } catch (SQLException e) {
                        if(e.getErrorCode() == MYSQL_DUPLICATE_PK )
                        {
                            System.out.println("Insertion fail because a tuple with the same primary key already exists.");
                        }
                        else if(e.getErrorCode() == MYSQL_TABLE_DOES_NOT_EXIST)
                            System.out.println("The table you entered does not exist.");
                        else if(e.getErrorCode() == MYSQL_COLUMN_DOES_NOT_EXIST)
                            System.out.println("The column you attempted to insert data into does not exist. You may have formatted your values improperly.");
                        System.out.println(e.getErrorCode());
                        e.printStackTrace();
                    }
                    break;
                case 4:
                        try {

                            stmt = con.createStatement();
                            System.out.println("DELETE FROM ");
                            String table = scan.nextLine();
                            System.out.println("WHERE ");
                            String column = scan.nextLine();
                            System.out.println(" = (remember to surround data with ' '");
                            String value = scan.nextLine();

                            query = "DELETE FROM " + table + " WHERE "+column+ "="+value;
                            stmt.executeUpdate(query);

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;


            }
        }
    }

}
