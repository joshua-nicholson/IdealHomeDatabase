import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import java.sql.DatabaseMetaData;


/**
 * Created by jwnicholson on 11/18/2016.
 */
public class Main
{
    public static final int MYSQL_DUPLICATE_PK = 1062;
    public static final int MYSQL_TABLE_DOES_NOT_EXIST = 1146;
    public static final int MYSQL_COLUMN_DOES_NOT_EXIST = 1054;
    public static final int MYSQL_TRUNCATED_INCORRECT_DOUBLE_VALUE = 1292;
    public static void main (String [] args)
    {
        Connection con = null;
        Statement stmt = null;
        DatabaseMetaData metadata = null;
        int choice = 0;
        String query;
        String table;
        String conditions;
        Scanner scan = new Scanner(System.in);
        String[] inputValues;
        String values = "";


        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/idealhome", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            metadata = con.getMetaData();
        } catch (SQLException e) {
            System.err.println("There was an error getting the metadata: "
                    + e.getMessage());
        }



        while(choice != 1)
        {
            System.out.println("Please select an option from the list below. \n 1. Quit \n 2. Query the database \n 3. Insert data \n 4. Delete data \n 5. Check metadata");
            choice = Integer.parseInt(scan.nextLine());

            switch (choice) {
                case 1:
                    break;
                case 2:
                    System.out.println("What table do you wish to query?");
                    table = scan.nextLine();
                    System.out.println("How many values do you wish to query? If you wish to query all values, type 0.");
                    int numOfValues = Integer.parseInt(scan.nextLine());
                    if(numOfValues != 0)
                    {
                        inputValues = new String[numOfValues];
                        System.out.println("What values do you wish to query? Type one value at a time and then push enter. Continue until you have typed all values.");
                        for(int i = 0; i < inputValues.length; i++)
                            inputValues[i] = scan.nextLine();
                        for(int i = 0; i < inputValues.length; i++)
                        {
                          if(i < inputValues.length - 1)
                              values += inputValues[i] + " AND ";
                          else
                              values += inputValues[i];
                        }
                    }
                    else
                    {
                        values = "*";
                    }
                    System.out.println("Do you want to use a WHERE clause? (Y/N)");
                    String userResponse = scan.nextLine();
                    if(userResponse.equalsIgnoreCase("Y"))
                    {
                        System.out.println("Type the conditions for your WHERE clause, excluding the keyword WHERE.");
                        conditions = scan.nextLine();
                        query = "SELECT " + values + " FROM " + table + " WHERE " + conditions + ";";
                        System.out.println(query);
                    }
                    else
                        query = "SELECT " + values + " FROM " + table + ";";

                    try {
                        stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        while (rs.next())
                        {
                                int numCols = rs.getMetaData().getColumnCount();
                                for(int i = 1; i <= numCols; i++)
                                    System.out.println("Column " + i + " = " +  rs.getObject(i));
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    values = "";
                    break;
                case 3:
                    try {
                        stmt = con.createStatement();
                        System.out.println("Enter the table you wish to insert data into. (Case sensitive)");
                        table = scan.nextLine();
                        System.out.println("Enter all applicable information separated by commas and make sure VARCHAR variables are surrounded by single quotation marks.");
                        values = scan.nextLine();
                        query = "INSERT INTO " + table + " VALUES (" + values + ")";
                        stmt.executeUpdate(query);
                    } catch (SQLException e) {
                        if(e.getErrorCode() == MYSQL_DUPLICATE_PK )
                            System.out.println("Insertion fail because a tuple with the same primary key already exists.");
                        else if(e.getErrorCode() == MYSQL_TABLE_DOES_NOT_EXIST)
                            System.out.println("The table you entered does not exist.");
                        else if(e.getErrorCode() == MYSQL_COLUMN_DOES_NOT_EXIST)
                            System.out.println("The column you attempted to insert data into does not exist. You may have formatted your values improperly.");
                        else
                            e.printStackTrace();
                    }
                    break;
                case 4:
                        try {

                            stmt = con.createStatement();
                            System.out.println("DELETE FROM ");
                            table = scan.nextLine();
                            System.out.println("WHERE ");
                            String column = scan.nextLine();
                            System.out.println(" = ");
                            String value = scan.nextLine();

                            query = "DELETE FROM " + table + " WHERE "+column+ "="+ "'" +value+ "'" ; //added those single quotation marks on value so that the user doesn't have to.
                            stmt.executeUpdate(query);
                            System.out.println("Deletion Successful \n");

                        } //end try

                        catch (SQLException e) {
                            if(e.getErrorCode() == MYSQL_TABLE_DOES_NOT_EXIST)
                                System.out.println("The table you entered does not exist.");
                            else if(e.getErrorCode() == MYSQL_COLUMN_DOES_NOT_EXIST)
                                System.out.println("The column you attempted to delete does not exist. You may have formatted your values improperly.");
                            else if(e.getErrorCode() == MYSQL_TRUNCATED_INCORRECT_DOUBLE_VALUE)
                                System.out.println("The value you have entered may not have been formatted correctly");
                            System.out.println(e.getErrorCode());
                            e.printStackTrace();
                        } //end catch
                        break;
                case 5:
                        try{
                            System.out.println("Database Product Name: " + metadata.getDatabaseProductName());
                            System.out.println("Database Product Version: "+ metadata.getDatabaseProductVersion());
                            System.out.println("Logged User: " + metadata.getUserName());
                            System.out.println("JDBC Driver: " + metadata.getDriverName());
                            System.out.println("Driver Version: " + metadata.getDriverVersion());
                            System.out.println("\n");
                        }
                        catch(SQLException e)
                        {

                        }
                        break;


            }


            } //end switch
        } //end while loop
    }// end main


