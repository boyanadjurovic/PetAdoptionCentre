import java.util.Scanner;
import java.sql.*;
public class Application {

    private static Connection aConnection = null;
    private static Statement aStatement = null;
    private static String aQuery;
    private static ResultSet aOutput;
    private static Scanner aScanner = new Scanner(System.in);

    private static int aEID;

    /**
     * Welcome menu prompt
     */
    private static void promptWelcomeMenu() {
        System.out.println(" *** WELCOME TO THE PET ADOPTION CENTRE DATABASE *** \n\n");
        System.out.println("If you wish to exit the system, please press 0");
        System.out.println("Are you an existing employee? (y/n) \n");

        String answer = aScanner.next();
        answer = answer.toLowerCase();

        if (answer.equals("0")) // todo someone please check if you can check for 0 like this when getting a String for answer
            System.exit(0);

        while (!answer.equals("y") || !answer.equals("n") || !answer.equals("yes") || !answer.equals("no")){
            System.out.println("Please enter a valid response (y/n");
            answer = aScanner.next();
        }

        if (answer.equals("y") || answer.equals("yes"))
            promptLogin();

        else
            promptCreateAccount();

    }

    /**
     * Prompts login to user if they are an existing employee
     */
    private static void promptLogin() {
        System.out.print("\n\n Please enter your employeeid (eid): ");
        int eid = aScanner.nextInt();

        System.out.print("\nPlease enter your password: ");
        String password = aScanner.next();

        try {
            authLogin(eid, password);
        } catch (SQLException e) {
            System.err.println("Login: SQL Exception" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Authorize login
     * @param pEID
     * @param pPassword
     * @throws SQLException
     */
    private static void authLogin(int pEID, String pPassword) throws SQLException {
        aQuery = "SELECT eid FROM employee WHERE eid = " + pEID + " AND password = " + pPassword + ";";
        aOutput = aStatement.executeQuery(aQuery);

        if (aOutput.next()) {
            aEID = aOutput.getInt(1);

            aQuery = "SELECT name FROM employee WHERE eid = " + aEID + ";";
            aOutput = aStatement.executeQuery(aQuery);
            String name = aOutput.getString(1);
            System.out.println("\nWelcome " + name + "!");
            promptMainMenu();
        }
        else {
            System.err.println("\nInvalid login information. Press 1 to try again or 0 to exit the database application");
            int decision = aScanner.nextInt();

            while (decision != 1 || decision != 0) {
                System.out.println("Please enter a valid response. Press 1 to try again or 0 to exit the database application");
                decision = aScanner.nextInt();
            }

            if (decision == 1) {
                promptLogin();
            }
            else {
                System.exit(0);
            }
        }
    }

    /**
     * Prompts user to create account if they are a new employee
     */
    private static void promptCreateAccount() {
        System.out.println("You may enter 0 to exit the system at any time");
        System.out.println("Please enter your 4 digit employeeid");

        int eid = aScanner.nextInt();
        int length = String.valueOf(eid).length();
        while (length != 4 || eid != 0) {
            System.out.println("Please enter a valid 4 digit employeeid");
            eid = aScanner.nextInt();
            length = String.valueOf(eid).length();
        }
        if (eid == 0)
            System.exit(0);

        System.out.println("Please enter your password");
        String password = aScanner.next();
        if (password.equals("0")) {
            System.exit(0);
        }

        System.out.println("Please enter your first and last name");
        String name = aScanner.next();
        while (!name.contains(" ") || name.length() > 30 || !name.equals("0")) {
            System.out.println("Please enter BOTH a first and last name less than 30 characters total");
            name = aScanner.next();
        }
        if (name.equals("0"))
            System.exit(0);

        System.out.println("Please enter your address");
        String address = aScanner.next();
        if (address.equals("0")) {
            System.exit(0);
        }

        System.out.println("Please enter your starting wage");
        int wage = aScanner.nextInt();
        length = String.valueOf(wage).length();
        while (length < 2 || wage != 0) {
            System.out.println("Invalid wage. All employees are paid at least $10/hour. Please re-enter");
            wage = aScanner.nextInt();
            length = String.valueOf(wage).length();
        }
        if (wage == 0) {
            System.exit(0);
        }

        try {
            authCreateAccount(eid, name, address, wage, password);
        } catch (SQLException e) {
            System.err.println("Create account: SQL Exception" + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Creates the account of the new employee
     * @param pEID
     * @param pName
     * @param pAddress
     * @param pWage
     * @throws SQLException
     */
    private static void authCreateAccount(int pEID, String pName, String pAddress, int pWage, String pPassword) throws SQLException {
        aQuery = "INSERT INTO employee VALUES (" + pEID + ", '" + pName + "', '" + pAddress + "', " + pWage + ", '" + pPassword + "');";
        aOutput = aStatement.executeQuery(aQuery);

        if (aOutput.next()) {
            System.out.println("Successfully created account.");
        }
        else {
            System.err.println("\nCould not create account. Press 1 to try again or 0 to exit the database application");
            int decision = aScanner.nextInt();

            while (decision != 1 || decision != 0) {
                System.out.println("Please enter a valid response. Press 1 to try again or 0 to exit the database application");
                decision = aScanner.nextInt();
            }

            if (decision == 1) {
                promptCreateAccount();
            }
            else {
                System.exit(0);
            }
        }
    }

    /**
     * The database's main menu
     */
    private static void promptMainMenu(){

    }

    public static void main(String[] args) {

        try	{
            aConnection = DriverManager.getConnection("jdbc:postgresql://localhost/CS421", "cs421g31", "1234Group31");
            aStatement = aConnection.createStatement();
        }
        catch(Exception e){
            System.err.println("Could not connect to database");
            System.exit(1); // 1 for error
        }
        promptWelcomeMenu();
    }
}
