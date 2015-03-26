///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Title:            Version Control App
// Files:            VersionControlApp.java, SimpleQueue.java, SimpleStack.java,
//						Repo.java, User.java, ChangeSet.java
// Semester:         CS367 Spring 2015
//
// Author:           Jeremy Koritzinsky
// Email:            jeremy.koritzinsky@wisc.edu
// CS Login:         koritzinsky
// Lecturer's Name:  Jim Skrentny
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ////////////////////
//
//                   CHECK ASSIGNMENT PAGE TO see IF PAIR-PROGRAMMING IS ALLOWED
//                   If pair programming is allowed:
//                   1. Read PAIR-PROGRAMMING policy (in cs302 policy) 
//                   2. choose a partner wisely
//                   3. REGISTER THE TEAM BEFORE YOU WORK TOGETHER 
//                      a. one partner creates the team
//                      b. the other partner must join the team
//                   4. complete this section for each program file.
//
// Pair Partner:     Jeff Tucker
// Email:            jetucker@wisc.edu
// CS Login:         jtucker
// Lecturer's Name:  Jim Skrentny
//
//////////////////////////// 80 columns wide //////////////////////////////////

import java.util.Scanner;

/**
 * Version control application. Implements the command line utility
 * for Version control.
 * @author
 *
 */
public class VersionControlApp {

	/* Scanner object on input stream. */
	private static final Scanner scnr = new Scanner(System.in);

	/**
	 * An enumeration of all possible commands for Version control system.
	 */
	private enum Cmd {
		AU, DU,	LI, QU, AR, DR, OR, LR, LO, SU, CO, CI, RC, VH, RE, LD, AD,
		ED, DD, VD, HE, UN
	}

	/**
	 * Displays the main menu help. 
	 */
	private static void displayMainMenu() {
		System.out.println("\t Main Menu Help \n" 
				+ "====================================\n"
				+ "au <username> : Registers as a new user \n"
				+ "du <username> : De-registers a existing user \n"
				+ "li <username> : To login \n"
				+ "qu : To exit \n"
				+"====================================\n");
	}

	/**
	 * Displays the user menu help. 
	 */
	private static void displayUserMenu() {
		System.out.println("\t User Menu Help \n" 
				+ "====================================\n"
				+ "ar <reponame> : To add a new repo \n"
				+ "dr <reponame> : To delete a repo \n"
				+ "or <reponame> : To open repo \n"
				+ "lr : To list repo \n"
				+ "lo : To logout \n"
				+ "====================================\n");
	}

	/**
	 * Displays the repo menu help. 
	 */
	private static void displayRepoMenu() {
		System.out.println("\t Repo Menu Help \n" 
				+ "====================================\n"
				+ "su <username> : To subcribe users to repo \n"
				+ "ci: To check in changes \n"
				+ "co: To check out changes \n"
				+ "rc: To review change \n"
				+ "vh: To get revision history \n"
				+ "re: To revert to previous version \n"
				+ "ld : To list documents \n"
				+ "ed <docname>: To edit doc \n"
				+ "ad <docname>: To add doc \n"
				+ "dd <docname>: To delete doc \n"
				+ "vd <docname>: To view doc \n"
				+ "qu : To quit \n" 
				+ "====================================\n");
	}

	/**
	 * Displays the user prompt for command.  
	 * @param prompt The prompt to be displayed.
	 * @return The user entered command (Max: 2 words).
	 */
	private static String[] prompt(String prompt) {
		System.out.print(prompt);
		String line = scnr.nextLine();
		String []words = line.trim().split(" ", 2);
		return words;
	}

	/**
	 * Displays the prompt for file content.  
	 * @param prompt The prompt to be displayed.
	 * @return The user entered content.
	 */
	private static String promptFileContent(String prompt) {
		System.out.println(prompt);
		String line = null;
		String content = "";
		while (!(line = scnr.nextLine()).equals("q")) {
			content += line + "\n";
		}
		return content;
	}

	/**
	 * Validates if the input has exactly 2 elements. 
	 * @param input The user input.
	 * @return True, if the input is valid, false otherwise.
	 */
	private static boolean validateInput2(String[] input) {
		if (input.length != 2) {
			System.out.println(ErrorType.UNKNOWN_COMMAND);
			return false;
		}
		return true;
	}

	/**
	 * Validates if the input has exactly 1 element. 
	 * @param input The user input.
	 * @return True, if the input is valid, false otherwise.
	 */
	private static boolean validateInput1(String[] input) {
		if (input.length != 1) {
			System.out.println(ErrorType.UNKNOWN_COMMAND);
			return false;
		}
		return true;
	}

	/**
	 * Returns the Cmd equivalent for a string command. 
	 * @param strCmd The string command.
	 * @return The Cmd equivalent.
	 */
	private static Cmd stringToCmd(String strCmd) {
		try {
			return Cmd.valueOf(strCmd.toUpperCase().trim());
		}
		catch (IllegalArgumentException e){
			return Cmd.UN;
		}
	}

	/**
	 * Handles add user. Checks if a user with name "username" already exists; 
	 * if exists the user is not registered. 
	 * @param username The user name.
	 * @return USER_ALREADY_EXISTS if the user already exists, SUCCESS otherwise.
	 */
	private static ErrorType handleAddUser(String username) {
		if (VersionControlDb.addUser(username) != null) {
			return ErrorType.SUCCESS;
		}
		else {
			return ErrorType.USERNAME_ALREADY_EXISTS;
		}
	}

	/**
	 * Handles delete user. Checks if a user with name "username" exists; if 
	 * does not exist nothing is done. 
	 * @param username The user name.
	 * @return USER_NOT_FOUND if the user does not exists, SUCCESS otherwise.
	 */
	private static ErrorType handleDelUser(String username) {
		User user = VersionControlDb.findUser(username); 
		if (user == null) {
			return ErrorType.USER_NOT_FOUND;
		}
		else {
			VersionControlDb.delUser(user);
			return ErrorType.SUCCESS;
		}
	}

	/**
	 * Handles a user login. Checks if a user with name "username" exists; 
	 * if does not exist nothing is done; else the user is taken to the 
	 * user menu. 
	 * @param username The user name.
	 * @return USER_NOT_FOUND if the user does not exists, SUCCESS otherwise.
	 */
	private static ErrorType handleLogin(String username) {
		User currUser = VersionControlDb.findUser(username);
		if (currUser != null) {
			System.out.println(ErrorType.SUCCESS);
			processUserMenu(currUser);
			return ErrorType.SUCCESS;
		}
		else {
			return ErrorType.USER_NOT_FOUND;
		}
	}

	/**
	 * Processes the main menu commands.
	 * 
	 */
	public static void processMainMenu() {

		String mainPrompt = "[anon@root]: ";
		boolean execute = true;

		while (execute) {
			String[] words = prompt(mainPrompt);
			Cmd cmd = stringToCmd(words[0]);

			switch (cmd) {
			case AU:
				if (validateInput2(words)) {
					System.out.println(handleAddUser(words[1].trim()));
				}
				break;
			case DU:
				if (validateInput2(words)) {
					System.out.println(handleDelUser(words[1].trim())); 
				}
				break;
			case LI:
				if (validateInput2(words)) {
					System.out.println(handleLogin(words[1].trim()));
				}
				break;
			case HE:
				if (validateInput1(words)) {
					displayMainMenu();
				}
				break;
			case QU:
				if (validateInput1(words)) {
					execute = false;
				}
				break;
			default:
				System.out.println(ErrorType.UNKNOWN_COMMAND);
			}

		}
	}

	/**
	 * Processes the user menu commands for a logged in user.
	 * @param logInUser The logged in user.
	 * @throws IllegalArgumentException in case any argument is null.
	 */
	public static void processUserMenu(User logInUser) {
		// Prevents the user from logging in without a username
		if (logInUser == null) {
			throw new IllegalArgumentException();
		}
		
		String userPrompt = "[" + logInUser.getName() + "@root" + "]: ";
		boolean execute = true;
		//A loop over the user menu
		while (execute) {
			//Asks the user for commands
			String[] words = prompt(userPrompt);
			//gets the first two characters from the input to turn into a command
			Cmd cmd = stringToCmd(words[0]);

			switch (cmd) {
			//This case adds a repository if there is no existing repository with the same name
			case AR:
				if (validateInput2(words)) { 
					if(VersionControlDb.findRepo(words[1]) != null) { //If the repo exists
						System.out.println(ErrorType.REPONAME_ALREADY_EXISTS);
					}
					else { 
						VersionControlDb.addRepo(words[1], logInUser); //adds the repository
						logInUser.subscribeRepo(words[1]);  //automatically subscribes the user
						System.out.println(ErrorType.SUCCESS);
					}
				} 
				break;
				//This case deletes a repository if the repository exists
			case DR:
				if (validateInput2(words)) {
					if(VersionControlDb.findRepo(words[1]) != null) { //If the repository exists
						if(VersionControlDb.findRepo(words[1]).getAdmin().getName() != 
								logInUser.getName()) { //If the current user is not the admin of the repo
							System.out.println(ErrorType.ACCESS_DENIED);
						}
						else {
							//Deletes the repo
							VersionControlDb.delRepo(VersionControlDb.findRepo(words[1]));
							System.out.println(ErrorType.SUCCESS);
						}
					}
					else {
						System.out.println(ErrorType.REPO_NOT_FOUND);
					}
				}
				break;
				//This case lists the repositories of the logged in user
			case LR:
				if (validateInput1(words)) {
					System.out.println(logInUser);
				}
				break;
				//This case opens a repository specified by the user
			case OR:
				if (validateInput2(words)) {
					if(VersionControlDb.findRepo(words[1]) == null) { //If the repo exists
						System.out.println(ErrorType.REPO_NOT_FOUND);
						break;
					}
					if(!logInUser.getAllSubRepos().contains(words[1])) { //If the user is subscribed to the repo
						System.out.println(ErrorType.REPO_NOT_SUBSCRIBED);
						break;
					}
					if(logInUser.getWorkingCopy(words[1]) == null) { //gets a copy of the repo
						logInUser.checkOut(words[1]);
					}
					System.out.println(ErrorType.SUCCESS);
					processRepoMenu(logInUser, words[1]); //Enters the repo menu
					System.out.println(ErrorType.SUCCESS);
				}
				break;
				//This case logs out the user
			case LO:
				if (validateInput1(words)) {
					execute = false;
				}
				break;
				//This case displays the help menu
			case HE:
				if (validateInput1(words)) {
					displayUserMenu();
				}
				break;
				//This will trigger if the user types in an invalid command
			default:
				System.out.println(ErrorType.UNKNOWN_COMMAND);
			}

		}
	}

	/**
	 * Process the repo menu commands for a logged in user and current
	 * working repository.
	 * @param logInUser The logged in user. 
	 * @param currRepo The current working repo.
	 * @throws IllegalArgumentException in case any argument is null.
	 */
	public static void processRepoMenu(User logInUser, String currRepo) {
		//Prevents the menu from being accessed if the user or repo are invalid
		if (logInUser  == null || currRepo == null) {
			throw new IllegalArgumentException();
		}
		
		String repoPrompt = "["+ logInUser.getName() + "@" + currRepo + "]: ";
		boolean execute = true;
		//A loop over the repo menu
		while (execute) {
			// prompts the user
			String[] words = prompt(repoPrompt);
			// Shortens the input to command size
			Cmd cmd = stringToCmd(words[0]);

			switch (cmd) {
			//This case will subscribed the user to the specified repo
			case SU:
				if (validateInput2(words)) {
					User user1 = VersionControlDb.findUser(words[1]); //A dummy variable to shorten code
					if(user1 != null) {
						if(logInUser == VersionControlDb.findRepo(currRepo).getAdmin()) {//If the current user is the admin of the repo
							user1.subscribeRepo(currRepo); //Subscribes the user
							System.out.println(ErrorType.SUCCESS);
						}
						else { //Occurs if the user is not the admin
							System.out.println(ErrorType.ACCESS_DENIED);
						}
					}
					else {
						System.out.println(ErrorType.USER_NOT_FOUND);
					}
				}
				break;
				//This case lists the documents of a repository
			case LD:
				if (validateInput1(words)) {
					RepoCopy workingCopy = logInUser.getWorkingCopy(currRepo);
					System.out.println(workingCopy);
				}
				break;
				//This case Edits a document in the repo
			case ED:
				if (validateInput2(words)) {
					if(logInUser.getWorkingCopy(currRepo) != null) {
						Document doc = logInUser.getWorkingCopy(currRepo).getDoc(words[1]); //A dummy variable
						if(doc != null) {			
							//Prompts the user for new file content
							doc.setContent(promptFileContent("Enter the file content and press q to quit: "));
							logInUser.addToPendingCheckIn(doc, Change.Type.EDIT, currRepo); //Adds the changes
							System.out.println(ErrorType.SUCCESS);
						}	
						else { //Occurs if the document doesn't exist
							System.out.println(ErrorType.DOC_NOT_FOUND);
						}
					}
					else {
						System.out.println(ErrorType.DOC_NOT_FOUND);
					}
				}					
				break;
				//This case adds a new document if the document name isn't taken
			case AD:
				if (validateInput2(words)) {
					if(logInUser.getWorkingCopy(currRepo) != null) {
						if(logInUser.getWorkingCopy(currRepo).getDoc(words[1]) == null) { //if the doc doesn't exist	
							//Prompts the user for content for the file
							Document doc = new Document(words[1], promptFileContent("Enter the file content and press q to quit: "), currRepo);
							logInUser.getWorkingCopy(currRepo).addDoc(doc); //adds the doc
							logInUser.addToPendingCheckIn(doc, Change.Type.ADD, currRepo); //queues the changes
							System.out.println(ErrorType.SUCCESS);
						}	
						else { //Occurs if the doc exists
							System.out.println(ErrorType.DOCNAME_ALREADY_EXISTS);
						}
					}
					else {
						System.out.println(ErrorType.DOC_NOT_FOUND);
					}
				}
				break;
				//This case deletes a document
			case DD:
				if (validateInput2(words)) {
					if(logInUser.getWorkingCopy(currRepo) != null) {
						if(logInUser.getWorkingCopy(currRepo).getDoc(words[1]) != null) { //If the document exists		
							Document doc = logInUser.getWorkingCopy(currRepo).getDoc(words[1]);
							//deletes the document
							logInUser.getWorkingCopy(currRepo).delDoc(doc);
							//queues the change
							logInUser.addToPendingCheckIn(doc, Change.Type.DEL, currRepo);
							System.out.println(ErrorType.SUCCESS);
						}	
						else { //Occurs if the document doesn't exist
							System.out.println(ErrorType.DOC_NOT_FOUND);
						}
					}
					else {
						System.out.println(ErrorType.DOC_NOT_FOUND);
					}
				}
				break;
				//This case shows the user a document
			case VD:
				if (validateInput2(words)) {
					if (validateInput2(words)) {
						if(logInUser.getWorkingCopy(currRepo) != null) {
							if(logInUser.getWorkingCopy(currRepo).getDoc(words[1]) != null) {//If the document exists
								//Prints the document
								System.out.println(logInUser.getWorkingCopy(currRepo).getDoc(words[1]).toString());
							}	
							else { //Occurs if the document doesn't exist
								System.out.println(ErrorType.DOC_NOT_FOUND);
							}
						}
						else {
							System.out.println(ErrorType.DOC_NOT_FOUND);
						}
					}
				}
				break;
				//This case checks in the repo
			case CI:
				if (validateInput1(words)) {
					System.out.println(logInUser.checkIn(currRepo));
				}
				break; 
				//This case checks out the repo
			case CO:
				if (validateInput1(words)) {
					System.out.println(logInUser.checkOut(currRepo));
				}
				break;
				//This case allows the user to review and accept/deny checkins
			case RC:
				if (validateInput1(words)) {
					Repo repo = VersionControlDb.findRepo(currRepo);
					ChangeSet checkIn = repo.getNextCheckIn(logInUser);
					if(checkIn == null) {
						if(repo.getCheckInCount() == 0) { //If the queue is empty
							System.out.println(ErrorType.NO_PENDING_CHECKINS);
						}
						else if(logInUser != repo.getAdmin()) { //If the user isn't the repo admin
							System.out.println(ErrorType.ACCESS_DENIED);
						}
						break;
					}
					do {
						System.out.println(checkIn);
						System.out.print("Approve changes? Press y to accept: "); //prompts the user for input
						String confirmation = scnr.nextLine(); //gets the input
						if(confirmation.equals("y")) {
							repo.approveCheckIn(logInUser, checkIn); //checks in if the user agrees
						}
					} while((checkIn = repo.getNextCheckIn(logInUser)) != null); //quits after each checkin has been approved or not
					System.out.println(ErrorType.SUCCESS);
				}
				break;
				//This case displays the version history
			case VH:
				if (validateInput1(words)) {
					System.out.println(VersionControlDb.findRepo(currRepo).getVersionHistory());
				}
				break;
				//This case reverts the repository
			case RE:	
				if (validateInput1(words)) {
					System.out.println(VersionControlDb.findRepo(currRepo).revert(logInUser));
				}
				break;
				//This case displays the repo help menu
			case HE:
				if (validateInput1(words)) {
					displayRepoMenu();
				}
				break; 
				//This case quits the repo menu
			case QU:
				if (validateInput1(words)) {
					execute = false;
				}
				break;
			default:
				System.out.println(ErrorType.UNKNOWN_COMMAND);
			}

		}
	}

	/**
	 * The main method. Simulation starts here.
	 * @param args Unused
	 */
	public static void main(String []args) {
		try {
			processMainMenu(); 
		}
		// Any exception thrown by the simulation is caught here.
		catch (Exception e) {
			System.out.println(ErrorType.INTERNAL_ERROR);
			// Uncomment this to print the stack trace for debugging purpose.
			//e.printStackTrace();
		}
		// Any clean up code goes here.
		finally {
			System.out.println("Quitting the simulation.");
		}
	}
}
