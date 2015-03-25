import java.util.ArrayList;
import java.util.List;

/**
 * Represents a repository which stores and tracks changes to a collection of 
 * documents.
 * @author
 *
 */
public class Repo {
	
	/* The current version of the repo. */
	private int version;
	
	/* The name of the repo. It's a unique identifier for a repository. */
	private final String repoName;
	
	/* The user who is the administrator of the repo. */
	private final User admin;
	
	/* The collection(list) of documents in the repo. */
	private final List<Document> docs;
	
	/* The check-ins queued by different users for admin approval. */
	private final QueueADT<ChangeSet> checkIns;
	
	/* The stack of copies of the repo at points when any check-in was applied. */
	private final StackADT<RepoCopy> versionRecords; 

	/**
	 * Constructs a repo object.
	 * @param admin The administrator for the repo.
	 * @param reponame The name of the repo.
	 * @throws IllegalArgumentException if any argument is null. 
	 */
	public Repo(User admin, String repoName) {
		this.admin = admin;
		this.repoName =  repoName;
		this.docs =  new ArrayList<Document>();
		this.checkIns =  new SimpleQueue<ChangeSet>();
		this.versionRecords =  new SimpleStack<RepoCopy>();
		versionRecords.push(new RepoCopy(repoName, 0, new ArrayList<Document>()));
	}
	
	/**
	 * Return the name of the repo.
	 * @return The name of the repository.
	 */
	public String getName() {
		return this.repoName;
	}
	
	/**
	 * Returns the user who is administrator for this repository.
	 * @return The admin user.
	 */
	public User getAdmin() {
		return this.admin;
	}
	
	/**
	 * Returns a copy of list of all documents in the repository.
	 * @return A list of documents.
	 */
	public List<Document> getDocuments() {
		return new ArrayList<Document>(this.docs);
	}
	
	/**
	 * Returns a document with a particular name within the repository.
	 * @param searchName The name of document to be searched.
	 * @return The document if found, null otherwise.
	 * @throws IllegalArgumentException if any argument is null.
	 */
	public Document getDocument(String searchName) {
    	if (searchName == null) {
			throw new IllegalArgumentException();
		}
    	
		for (Document d : this.docs) {
			if (d.getName().equals(searchName)) {
				return d;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the current version of the repository.
	 * @return The version of the repository.
	 */
	public int getVersion() {
		return this.version;
	}
	
	/**
	 * Returns the number of versions (or changes made) for this repository.
	 * @return The version count.
	 */
	public int getVersionCount() {
		// Implemented as per Piazza answer at
		// https://piazza.com/class/i574bznhxhp2ms?cid=635
		return versionRecords.size();
	}
	
	/**
	 * Returns the history of changes made to the repository. 
	 * @return The string containing the history of changes.
	 */
	public String getVersionHistory() {
		return versionRecords.toString();
	}
	
	/**
	 * Returns the number of pending check-ins queued for approval.
	 * @return The count of changes.
	 */
	public int getCheckInCount() {
		return checkIns.size();
	}
	
	
	/**
	 * Queue a new check-in for admin approval.
	 * @param checkIn The check-in to be queued.
	 * @throws IllegalArgumentException if any argument is null. 
	 */
	public void queueCheckIn(ChangeSet checkIn) {
		if(checkIn == null) throw new IllegalArgumentException("checkIn");
		checkIns.enqueue(checkIn);
	}
	
	/**
	 * Returns and removes the next check-in in the queue 
	 * if the requesting user is the administrator.
	 * @param requestingUser The user requesting for the change set.
	 * @return The checkin if the requestingUser is the admin and a checkin
	 * exists, null otherwise.
	 * @throws IllegalArgumentException if any argument is null. 
	 */
	public ChangeSet getNextCheckIn(User requestingUser) {
		if(requestingUser == null) throw new IllegalArgumentException("requestingUser");
		if(requestingUser == admin && !checkIns.isEmpty()) {
			try {
			return checkIns.dequeue();
			}
			catch(EmptyQueueException ex) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Applies the changes contained in a particular checkIn and adds
	 * it to the repository if the requesting user is the administrator.
 	 * Also saves a copy of changed repository in the versionRecords.
	 * @param requestingUser The user requesting the approval.
	 * @param checkIn The checkIn to approve.
	 * @return ACCESS_DENIED if requestingUser is not the admin, SUCCESS 
	 * otherwise.
	 * @throws IllegalArgumentException if any argument is null. 
	 */
	public ErrorType approveCheckIn(User requestingUser, ChangeSet checkIn) {
		if(requestingUser == null) throw new IllegalArgumentException("requestingUser");
		if(checkIn == null) throw new IllegalArgumentException("checkIn");
		if(requestingUser != admin) return ErrorType.ACCESS_DENIED;
		// Explanation of how at: https://piazza.com/class/i574bznhxhp2ms?cid=616
		Change currentChange;
		while((currentChange = checkIn.getNextChange()) != null) {
			switch(currentChange.getType()) {
			case ADD:
				docs.add(currentChange.getDoc());
				break;
			case DEL:
				docs.remove(currentChange.getDoc());
				break;
			case EDIT:
				for(Document doc : docs) {
					if(doc.equals(currentChange.getDoc())) {
						doc.setContent(currentChange.getDoc().getContent());
						break;
					}
				}
				break;
			default:
				throw new IllegalArgumentException();
			}
		}
		RepoCopy copy = new RepoCopy(repoName, ++version, getDocuments());
		versionRecords.push(copy);
		return ErrorType.SUCCESS;
	}
	
	/**
	 * Reverts the repository to the previous version if present version is
	 * not the oldest version and the requesting user is the administrator.
	 * @param requestingUser The user requesting the revert.
	 * @return ACCESS_DENIED if requestingUser is not the admin, 
	 * NO_OLDER_VERSION if the present version is the oldest version, SUCCESS 
	 * otherwise.
	 * @throws IllegalArgumentException if any argument is null. 
	 */
	public ErrorType revert(User requestingUser) {
		if(requestingUser == null) throw new IllegalArgumentException();
		if(requestingUser != admin) return ErrorType.ACCESS_DENIED;
		if(getVersion() == 0) return ErrorType.NO_OLDER_VERSION;
		RepoCopy prevVersion = null;
		try {
			versionRecords.pop(); // Remove most recent version
			prevVersion = versionRecords.peek(); // Load previous version
		}
		catch (EmptyStackException e) {
		}
		docs.clear(); // Remove all current docs
		for(Document doc : prevVersion.getDocuments()) { // Restore all prev docs
			docs.add(new Document(doc.getName(), doc.getContent(), doc.getRepoName()));
		}
		--version;
		return ErrorType.SUCCESS;
	}
}
