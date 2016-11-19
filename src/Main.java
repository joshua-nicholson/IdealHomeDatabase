import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import java.sql.DatabaseMetaData;


public class Main
{
    public static final int MYSQL_DUPLICATE_PK = 1062;
    public static final int MYSQL_TABLE_DOES_NOT_EXIST = 1146;
    public static final int MYSQL_COLUMN_DOES_NOT_EXIST = 1054;
    public static final int MYSQL_TRUNCATED_INCORRECT_DOUBLE_VALUE = 1292;
    public static final int MYSQL_DATABASE_ALREADY_EXISTS = 1007;
    public static final int MySQL_Integrity_Constraint_ViolationException = 1451; // Foreign Key Issue
    public static void main (String [] args)
    {
        //Global variables created
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


        //initial connection is setup to the MySQL server
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            metadata = con.getMetaData();
        } catch (SQLException e) {
            System.err.println("There was an error getting the metadata: "
                    + e.getMessage());
        }
        //Creates a database using user input
        System.out.println("What is the name of the database you wish to create?");
        String database = scan.nextLine();
        String sql = "CREATE DATABASE " + database;

        //Checks to see if the database already exists
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
           if(e.getErrorCode() == MYSQL_DATABASE_ALREADY_EXISTS)
               System.out.println("A database with this name already exists.");
        }

        //modifies the connection so it points to the newly created database
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database, "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //The SQL statement to create the Staff table with proper relations is prepared
        String createStaff = "CREATE TABLE Staff("
            + "staffNo VARCHAR(4),"
            + "fName VARCHAR(20),"
            + "lName VARCHAR(20),"
            + "position VARCHAR(20),"
            + "salary INT,"
            + "PRIMARY KEY(staffNo))";

        //The above SQL statement is executed
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(createStaff);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //The SQL statement to create the Client table with proper relations is prepared
        String createClient = "CREATE TABLE Client("
            + "clientNo VARCHAR(4),"
            + "fName VARCHAR(20),"
            + "lName VARCHAR(20),"
            + "telNo VARCHAR(13),"
            + "maxRent INT,"
            + "PRIMARY KEY(clientNo))";

        //The above SQL statement is executed
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(createClient);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //The SQL statement to create the PrivateOwner table with proper relations is prepared
        String createPrivateOwner = "CREATE TABLE PrivateOwner("
            + "ownerNo VARCHAR(4),"
            + "fName VARCHAR(20),"
            + "lName VARCHAR(20),"
            + "telNo VARCHAR(13),"
            + "PRIMARY KEY(ownerNo))";

        //The above SQL statement is executed
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(createPrivateOwner);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //The SQL statement to create the PropertyForRent table with proper relations is prepared
        String createPropertyForRent = "CREATE TABLE PropertyForRent("
            + "propertyNo VARCHAR(4),"
            + "street VARCHAR(20),"
            + "city VARCHAR(20),"
            + "postcode VARCHAR(8),"
            + "rent INT,"
            + "ownerNo VARCHAR(4),"
            + "staffNo VARCHAR(4),"
            + "PRIMARY KEY(propertyNo),"
            + "FOREIGN KEY(ownerNo) REFERENCES PrivateOwner(ownerNo),"
            + "FOREIGN KEY(staffNo) REFERENCES Staff(staffNo))";

        //The above SQL statement is executed
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(createPropertyForRent);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //The SQL statement to create the Viewing table with proper relations is prepared
        String createViewing = "CREATE TABLE Viewing("
            + "clientNo VARCHAR(4),"
            + "propertyNo VARCHAR(4),"
            + "viewDate VARCHAR(9),"
            + "comment VARCHAR(20),"
            + "PRIMARY KEY(clientNo, propertyNo),"
            + "FOREIGN KEY(clientNo) REFERENCES Client(clientNo))";

        //The above SQL statement is executed
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(createViewing);
        } catch (SQLException e) {
            if(e.getErrorCode() == MYSQL_DATABASE_ALREADY_EXISTS)
                System.out.println("A database with this name already exists.");
        }

        //Presents the user with a menu
        while(choice != 1)
        {
            System.out.println("Please select an option from the list below. \n 1. Quit \n 2. Query the database \n 3. Insert data \n 4. Delete data \n 5. Check metadata");
            choice = Integer.parseInt(scan.nextLine());

            switch (choice) {
                //Quits the program
                case 1:
                    break;

                //Asks the user which table they wish to query and what values they want to extract from it. Also asks for a WHERE clause
                case 2:
                    System.out.println("What table do you wish to query?");
                    table = scan.nextLine();
                    System.out.println("How many values do you wish to query? If you wish to query all values, type 0.");
                    int numOfValues = Integer.parseInt(scan.nextLine());
                    if(numOfValues != 0)
                    {
                        //Creates an array that stores all variables the user wishes to extract
                        inputValues = new String[numOfValues];
                        System.out.println("What values do you wish to query? Type one value at a time and then push enter. Continue until you have typed all values.");
                        for(int i = 0; i < inputValues.length; i++)
                            inputValues[i] = scan.nextLine();
                        //Creates a string that is formatted the proper way for SQL
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
                        //The SQL statement is prepared using the input from above
                        query = "SELECT " + values + " FROM " + table + " WHERE " + conditions + ";";
                        System.out.println(query);
                    }
                    else
                        query = "SELECT " + values + " FROM " + table + ";";

                    //The SQL statement is executed. A loop is used to get all of the data that was requested
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
                    //Asks the user for which table they want to delete as well as which fields.
                    try {
                        stmt = con.createStatement();
                        System.out.println("Enter the table you wish to insert data into.");
                        table = scan.nextLine();
                        System.out.println("Enter all applicable fields separated by commas and make sure VARCHAR variables are surrounded by single quotation marks.");
                        values = scan.nextLine();
                        query = "INSERT INTO " + table + " VALUES (" + values + ")";
                        stmt.executeUpdate(query);
                    } catch (SQLException e) {
                        //Error codes are used to determine the cause of the failure and the user is notified
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

                            query = "DELETE FROM " + table + " WHERE "+column+ "="+ "'" +value+ "'" ;    //added those single quotation marks on value so that the user doesn't have to.
                            stmt.executeUpdate(query); //Sends statement query to the db
                            System.out.println("Deletion Successful \n"); //confirms successful deletion

                        } //end try

                        catch (SQLException e) {
                           if (e.getErrorCode() == MySQL_Integrity_Constraint_ViolationException) {  //Catches if user is trying to delete a foreign key

                               System.out.println("That is a foreign key, if you would like to delete it type y. Then re-enter your data.");
                               String UserResponse = scan.nextLine();
                               if (UserResponse.equalsIgnoreCase("Y"))
                               {
                                   try {
                                       stmt.executeUpdate("SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;");
                                       System.out.println("DELETE FROM ");
                                       table = scan.nextLine();
                                       System.out.println("WHERE ");
                                       String column = scan.nextLine();
                                       System.out.println(" = ");
                                       String value = scan.nextLine();

                                       query = "DELETE FROM " + table + " WHERE "+column+ "="+ "'" +value+ "'" ; //added those single quotation marks on value so that the user doesn't have to.
                                       stmt.executeUpdate(query);
                                       System.out.println("Deletion Successful \n");
                                       stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;");


                                   } catch (SQLException e1) {
                                       e1.printStackTrace();
                                   }
                               }
                               else break;
                           }
                            else if(e.getErrorCode() == MYSQL_TABLE_DOES_NOT_EXIST)
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
                            System.out.println("Database Product Name: " + metadata.getDatabaseProductName()); //Gets db prod name
                            System.out.println("Database Product Version: "+ metadata.getDatabaseProductVersion()); //Gets db prod version
                            System.out.println("Logged User: " + metadata.getUserName()); //gets user name
                            System.out.println("JDBC Driver: " + metadata.getDriverName()); //gets driver name
                            System.out.println("Driver Version: " + metadata.getDriverVersion()); //gets driver version
                            System.out.println("\n");
                        }
                        catch(SQLException e)
                        {
                            e.printStackTrace();
                        }
                        break;


            }


            } //end switch
        } //end while loop
    }// end main


