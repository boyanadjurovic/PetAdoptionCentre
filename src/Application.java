import java.util.Calendar;
import java.util.Scanner;
import java.sql.*;

public class Application {

    private static Connection aConnection = null;
    private static Statement aStatement = null;
    private static String aQuery;
    private static ResultSet aOutput;
    private static Scanner aScanner = new Scanner(System.in);
    private static PreparedStatement preparePetInfo;

    private static int aEID;

    /**
     * Welcome menu prompt
     */
    private static void promptWelcomeMenu() {
        System.out.println(" *** WELCOME TO THE PET ADOPTION CENTRE DATABASE ***");
        System.out.println("If you wish to exit the system, please press 0");
        System.out.println("Are you an existing employee? (y/n)");

        String answer = aScanner.next();
        answer = answer.toLowerCase();

        if (answer.equals("0"))  {
        	closeDatabase();
        	return;
        }

        /*while (!answer.equals("y") || !answer.equals("n") || !answer.equals("yes") || !answer.equals("no")){
            System.out.println("Please enter a valid response (y/n)");
            answer = aScanner.next();
        }*/

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
            System.err.println("Login: SQL Exception " + e.getMessage());
            closeDatabase();
            return;
        }
    }

    /**
     * Authorize login
     * @param pEID
     * @param pPassword
     * @throws SQLException
     */
    private static void authLogin(int pEID, String pPassword) throws SQLException {
        aStatement = aConnection.createStatement();
        
    	aQuery = "SELECT eid, name FROM employee WHERE eid = " + pEID + " AND password = '" + pPassword +"'" ;
        aOutput = aStatement.executeQuery(aQuery);
        if (aOutput.next()) {
            
            System.out.println("\nWelcome " + aOutput.getString("name") + "!");
            promptMainMenu();
        }
        else {
            System.err.println("\nInvalid login information. Press 1 to try again or 0 to exit the database application");
            int decision = aScanner.nextInt();

            while (decision != 1 && decision != 0) {
                System.out.println("Please enter a valid response. Press 1 to try again or 0 to exit the database application");
                decision = aScanner.nextInt();
            }

            if (decision == 1) {
                promptLogin();
            }
            else if (decision ==0){
            	System.out.println("exit successful");
            	
                closeDatabase();
                return;
            }
        }
    }

    /**
     * Prompts user to create account if they are a new employee
     */
    private static void promptCreateAccount() {
        System.out.println("You may enter 0 to exit the system at any time");
        System.out.println("Please enter your new 4 digit employeeid");

        int eid = aScanner.nextInt();
        int length = String.valueOf(eid).length();
        /*while (length != 4 || eid != 0) {
            System.out.println("Please enter a valid 4 digit employeeid");
            eid = aScanner.nextInt();
            length = String.valueOf(eid).length();
        }*/
        if (eid == 0) {
        	closeDatabase();
            return;
        }
        //promptMainMenu();

        System.out.println("Please enter your password");
        String password = aScanner.next();
        if (password.equals("0")) {
        	closeDatabase();
            return;
        }

        System.out.println("Please enter your first and last name");
        String name = aScanner.nextLine();
        /*while (!name.contains(" ") || name.length() > 30 || !name.equals("0")) {
            System.out.println("Please enter BOTH a first and last name less than 30 characters total");
            name = aScanner.next();
        }*/
        if (name.equals("0")) {
        	closeDatabase();
            return;
        }

        System.out.println("Please enter your address");
        String address = aScanner.nextLine();
        if (address.equals("0")) {
        	closeDatabase();
            return;
        }

        System.out.println("Please enter your starting wage");
        int wage = aScanner.nextInt();
        length = String.valueOf(wage).length();
        /*while (length < 2 || wage != 0) {
            System.out.println("Invalid wage. All employees are paid at least $10/hour. Please re-enter");
            wage = aScanner.nextInt();
            length = String.valueOf(wage).length();
        }*/
        if (wage == 0) {
        	closeDatabase();
            return;
        }

        try {
            authCreateAccount(eid, name, address, wage, password);
        } catch (SQLException e) {
            System.err.println("Create account: SQL Exception " + e.getMessage());
            closeDatabase();
            return;
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
        
    	aQuery = "insert into employee (eid,name,address,wage,password) values ("+  pEID +",'"+ pName +"','" + pAddress+ "',"+pWage + ",'" + pPassword +"')";

        
        aStatement.executeUpdate(aQuery, Statement.RETURN_GENERATED_KEYS);
        aOutput = aStatement.getGeneratedKeys();


            
            if (aOutput.next()) {
                System.out.println("Successfully created account.");
                promptMainMenu();
            }
            else {
                System.err.println("\nCould not create account. Press 1 to try again or 0 to exit the database application");
                int decision = aScanner.nextInt();

                while (decision != 1 && decision != 0) {
                    System.out.println("Please enter a valid response. Press 1 to try again or 0 to exit the database application");
                    decision = aScanner.nextInt();
                }

                if (decision == 1) {
                    promptCreateAccount();
                }
                else {
                	System.out.println("Exit successful");
                	closeDatabase();
                	return;
                }
            }
    }

    /**
     * The database's main menu
     * @throws SQLException 
     */
    private static void promptMainMenu() throws SQLException{
    	
    	System.out.println("welcome to our main menu.");
    	System.out.println("You may: \n"
    			+ "1) Look up for a pet's information\n"
    			+ "2) Get pet's medical history\n"
    			+ "3) Add a rescued pet in centre's database\n"
    			+ "4) \n"
    			+ "5) \n"
    			+ "6) Exit\n");
    	System.out.println("Choose from options 1, 2, 3, 4, 5, or 6 ");
    	int option = aScanner.nextInt();
    	
    	if(option == 1) lookUpPetInfo();
    	else if(option == 2) petMedicalInfo();
    	else if(option== 3) addRescuedPet();
    	else if(option == 4);
    	else if(option == 5);
    	
    	else if(option == 6) {
    		closeDatabase();
    		return;
        }
    	
    	else {
    		System.out.println("Invalid input. Please enter 1, 2, 3, 4, 5, or 6.");
    		promptMainMenu();
    	}


    }
    
    public static void lookUpPetInfo() throws SQLException {
    	
    	System.out.println("Please enter pet's pid: ");
    	int pid = aScanner.nextInt();
    	System.out.println("Please enter the attribute you need: (choose from : date, weight, breed, species, kno, mid, birthdate, name)");
    	String str = aScanner.next().toLowerCase();
    	
    	
    	try {
			preparePetInfo = aConnection.prepareStatement("SELECT "+str+" FROM pet WHERE pid = ?");

			
			preparePetInfo.setInt(1, pid);
				
				aOutput = preparePetInfo.executeQuery();
				
				
				if(aOutput.next()) {
					
					if(str.equals("date") || str.equals("birthdate")) {
						System.out.println(str + " of pet " + pid +" is " + aOutput.getDate("date"));
					}
					if(str.equals("weight")) {
						System.out.println(str + " of pet " + pid + " is " + aOutput.getString(1));
					}
					if(str.equals("breed") || str.equals("species") || str.equals("kno") || str.equals("mid") || str.equals("name")) {
						
						System.out.println(str+ " of pet " + pid + " is " + aOutput.getString(1));
						
					}

				
				}
				//preparePetInfo.close();
			//}

		} catch (SQLException e) {
			
			closeDatabase();
			System.out.println("database closed");
			e.printStackTrace();
		}
    	System.out.println("\n\n\n");
    	promptMainMenu();
    }
    

    
    
    public static void petMedicalInfo() throws SQLException {
    	System.out.println("Please enter pet's pid: ");
    	int pid = aScanner.nextInt();
    	System.out.println("The medical history of pet (pid:"+pid+")");
    	
    	 aQuery="SELECT ccondition, pcondition, medication FROM medicalhistory WHERE pid="+pid;
    	 aOutput = aStatement.executeQuery(aQuery);
    	 
    	 try {
    		 if(aOutput.next()) {
		     System.out.println("Current condition: "+aOutput.getString("ccondition"));
	    	 System.out.println("Past history: "+aOutput.getString("pcondition"));
	    	 System.out.println("Medication: "+aOutput.getString("medication"));
    		 }
    		 
    	 }
    	 catch(SQLException e) {
    		 closeDatabase();
 			System.out.println("database closed");
 			e.printStackTrace();
    	 }
    	System.out.println("\n\n\n");
    	promptMainMenu();
    	
    }
    
    
    
    public static void addRescuedPet() throws SQLException{
    	
    	
    	try {
    		
    		System.out.println("Enter the eid of the employee who rescued the pet: ");
    		int rescuerEid = aScanner.nextInt();
    		
    		System.out.println("Enter the weight of the pet:");
    		Double petWeight = aScanner.nextDouble();
    		
    		System.out.println("Enter the breed of the pet: ");
    		String petBreed = aScanner.nextLine();
    		
    		System.out.println("Enter the species of the pet: ");
    		String petSpecies = aScanner.nextLine();
    		
    		System.out.println("Enter the name of the pet: ");
    		String petName = aScanner.nextLine();
    		
    		Date date = (Date) Calendar.getInstance().getTime();
    		System.out.println("Date: "+date);
    		
    		aOutput = aStatement.executeQuery("select pid, kno, mid from pet");
    		
    		if(aOutput.next()) {
    			int uniquePid = (int)(Math.random()*99999+10000);
    			int uniqueKno = (int)(Math.random()*99+1);
    			String uniqueMid = Integer.toString((int)(Math.random()*99999+10000));
    			
    				try {
    					aQuery="insert into pet (pid,date,weight,breed,species,kno,mid,birthdate,name)"
    							+ "values ("+uniquePid+","+date+","+petWeight+","+petBreed+","+petSpecies+","+uniqueKno+","+uniqueMid+","+date+","+petName+")";
    					
    					aStatement.executeUpdate(aQuery, Statement.RETURN_GENERATED_KEYS);
    			        aOutput = aStatement.getGeneratedKeys();
    			        if(aOutput.next()) {
    			        	System.out.println("Pet added to database");
    			
    			        }
    			        aQuery ="insert into rescue (eid,pid) values ("+rescuerEid+","+uniquePid+")";
    			        aStatement.executeUpdate(aQuery, Statement.RETURN_GENERATED_KEYS);
    			        aOutput = aStatement.getGeneratedKeys();
    			        if(aOutput.next()) {
    			        	System.out.println("Rescue table updated");
    			        }
    			        
    			        
    				}
    				catch(SQLException e) {
    					System.out.println("error occured here");
    					addRescuedPet();
    					closeDatabase();
    					e.printStackTrace();
    				}

    			
    		}
    		
    	}
    	catch (SQLException e) {
    		 closeDatabase();
  			System.out.println("database closed");
  			e.printStackTrace();
    	}
    	
    }
    
    
    public static void closeDatabase() {
    	try {
    		aConnection.close();
    		aStatement.close();
    		aOutput.close();
    		preparePetInfo.close();
    		System.out.println("database closed");
    	}
    	catch (SQLException e) {
    		System.err.println("Could not close connections to database");
    	}
    	
    }

    public static void main(String[] args) {

    	try {
            DriverManager.registerDriver ( new org.postgresql.Driver() ) ;
        } catch (Exception cnfe){
            System.out.println("Class not found");
        }
        try	{
            aConnection = DriverManager.getConnection("jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421", "cs421g31", "1234Group31");
            aStatement = aConnection.createStatement();
            System.out.println("connection successful");
            promptWelcomeMenu();
            closeDatabase();
        }
        catch(Exception e) {
        	closeDatabase();
            System.err.println("Could not connect to database");
            System.exit(1); // 1 for error
        }

    }
}
