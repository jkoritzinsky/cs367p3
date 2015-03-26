import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user. Maintains the list of subscribed repositories, working
 * copy of the subscribed repositories and their changelist. 
 * @author
 *
 */
public class User {
	
	/* The name of the user. It's a unique identifier for a user. */
	private final String userName;
	
	/*  The list of names of the repositories to which the user is subscribed. */
	private final List<String> subRepos;
	
	/* The list of all pending check-ins not yet made by the user. */
	private final List<ChangeSet> pendingCheckIns;
	
	/* The list of all local working copies of the user. */
	private final List<RepoCopy> workingCopies;
	
	/**
	 * Constructor for User. 
	 * @param username The user name.
	 * @throws IllegalArgumentException if any argument is null. 
	 */
	public User(String userName) {
		this.userName = userName;
		this.subRepos = new ArrayList<String>();
		this.pendingCheckIns = new ArrayList<ChangeSet>();
		this.workingCopies = new ArrayList<RepoCopy>();
	}
	
	/**
	 * Returns the name of the user.
	 * @return the user name.
	 */
	public String getName() {
		return this.userName;
	}
		
	/**
	 * Returns a copy of list of subscribed repositories.
	 * @return The subscribed repo list.
	 */
	public List<String> getAllSubRepos() {
		return new ArrayList<String>(this.subRepos);
	}
	
    /**
     * Returns the working copy for a repository.
     * @param repoName The name of the repository.
     * @return The working copy if exists, null otherwise.
     * @throws IllegalArgumentException if any argument is null. 
     */
    public RepoCopy getWorkingCopy(String repoName) {
    	if(repoName == null) throw new IllegalArgumentException("repoName");
    	for(RepoCopy workingCopy : workingCopies) { //A loop to check if the repo exists
			if(workingCopy.getReponame().equals(repoName)) {
				return workingCopy;
			}
		}
    	return null;
	}
    
	/**
	 * Subscribes the user to a repository. Adds a new repository to the 
	 * subscribed list. 
	 * @param repoName The name of the repository to subscribe.
	 * @throws IllegalArgumentException if any argument is null. 
	 */
	public void subscribeRepo(String repoName) {
		
		if (repoName == null) {
			throw new IllegalArgumentException();
		}
		
		if (!this.subRepos.contains(repoName)) {
			this.subRepos.add(repoName); 
		}
	}
	
	/**
	 * Un-subscribes the user from a repository. Deletes a repository from 
	 * the subscribed list.
	 * @param repoName The name of the repository to unsubscribe. 
	 * @throws IllegalArgumentException if any argument is null. 
	 */
	public void unsubscribeRepo(String repoName) {
		if (repoName == null) {
			throw new IllegalArgumentException();
		}
		this.subRepos.remove(repoName);
	}
	
	/**
	 * Checks if the user is subscribed to a particular repository.
	 * @param repoName The name of the repository to subscribe.
	 * @return True if the repository is subscribed, false otherwise.
	 * @throws IllegalArgumentException if any argument is null. 
	 */
	public boolean isSubRepo(String repoName) {
		if (repoName == null) {
			throw new IllegalArgumentException();
		}
		return subRepos.contains(repoName);
	}
	
	/**
	 * Adds a new change (add, edit or delete) to the pending checkIn for the 
	 * repository. If a checkIn does not exits, a new checkIn is
	 * created.
	 * @param doc The document added, deleted or edited.
	 * @param type The type of change.
	 * @param repoName The name of the repository on which the change is done.
	 * @throws IllegalArgumentException if any argument is null. 
	 */
	public void addToPendingCheckIn(Document doc, Change.Type type, String repoName) {
		//A few checks to make sure the input all exists
		if(doc == null) throw new IllegalArgumentException("doc");
		if(type == null) throw new IllegalArgumentException("type");
		if(repoName == null) throw new IllegalArgumentException("repoName");
		ChangeSet pendingCheckIn = getPendingCheckIn(repoName);
		if(pendingCheckIn == null) { //Checks if the checkin exists
			pendingCheckIn = new ChangeSet(repoName);
			pendingCheckIns.add(pendingCheckIn); //adds the checkin
		}
		pendingCheckIn.addChange(doc, type); //Adds the change
	}
	
	/**
	 * Returns the pending check-in for a repository.
	 * @param repoName The name of the repository.
	 * @return The pending check-in for the repository if exists, 
	 * null otherwise.
	 * @throws IllegalArgumentException if any argument is null. 
	 */
    public ChangeSet getPendingCheckIn(String repoName) {
    	if(repoName == null) throw new IllegalArgumentException("repoName");
		for(ChangeSet set : pendingCheckIns) {
			if(set.getReponame().equals(repoName)) return set; //checks for the repo with the given name
		}
    	return null;
	}
    
    /**
     * Checks in or queues a pending checkIn into a repository and removes it
     * from the local pending CheckIns list.   
     * @param repoName The name of repository.
     * @return NO_LOCAL_CHANGES, if there are no pending changes for the
     * repository, SUCCESS otherwise.
     * @throws IllegalArgumentException if any argument is null. 
     */
	public ErrorType checkIn(String repoName) {
		if(repoName == null) throw new IllegalArgumentException("repoName");
		ChangeSet checkIn = null;
		for(int i = 0; i < pendingCheckIns.size(); ++i) { //A loop over all of the pending checkins
			ChangeSet set = pendingCheckIns.get(i); 
			if(set.getReponame().equals(repoName)) { //A check for the specified repo
				checkIn = set;
				pendingCheckIns.remove(i); 
				break;
			}
		}
		if(checkIn == null) return ErrorType.NO_LOCAL_CHANGES; //If there are no changes to make
		Repo repo = VersionControlDb.findRepo(repoName);
		repo.queueCheckIn(checkIn); 
		return ErrorType.SUCCESS;
	}
	
	/**
	 * Gets a latest version of the documents from the repository and puts
	 * them onto a working copy, if the user is currently subscribed to the
	 * repository. When the latest version is checked out, a new working copy 
	 * is created and existing one is deleted.
	 * @param repoName The name of the repository to check out.
	 * @return REPO_NOT_SUBSCRIBED if the repository is not subscribed, 
	 * SUCCESS otherwise. 
	 * @throws IllegalArgumentException if any argument is null. 
	 */
	public ErrorType checkOut (String repoName) {
		if(repoName == null) throw new IllegalArgumentException("repoName");
		if(!subRepos.contains(repoName)) return ErrorType.REPO_NOT_SUBSCRIBED;
		// Remove previous working copy if it exists
		for(int i = 0; i < workingCopies.size(); ++i) {
			if(workingCopies.get(i).getReponame().equals(repoName)) {
				workingCopies.remove(i);
				break;
			}
		}
		// Delete pending check-in if it exists
		for(int i = 0; i < pendingCheckIns.size(); ++i) {
			if(pendingCheckIns.get(i).getReponame().equals(repoName)) {
				pendingCheckIns.remove(i);
				break;
			}
		}
		// Create and add new working copy
		Repo repo = VersionControlDb.findRepo(repoName);
		RepoCopy workingCopy = new RepoCopy(repoName, repo.getVersion(), repo.getDocuments());
		workingCopies.add(workingCopy);
		return ErrorType.SUCCESS;
	}
		
	@Override
	public String toString() {
		String str = "=================================== \n";
		str += "Username: " + this.userName + "\n"
				+ "-----------Repos------------------ \n";
		int count = 0;
		for (String r : this.subRepos) {
			str += ++count + ". " + r + "\n";
		}
		str += this.subRepos.size() + " repos(s) subscribed.\n"
				+ "===================================";
		return str;
	}
}
