///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Main Class File:  VersionControlApp.java
// Semester:         CS367 Spring 2015
//
// Author:           Jeremy Koritzinsky
// Email:            jeremy.koritzinsky@wisc.edu
// CS Login:         koritzinsky
// Lecturer's Name:  Jim Skrentny
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ////////////////////
//
// Pair Partner:     Jeff Tucker
// Email:            jetucker@wisc.edu
// CS Login:         jtucker
// Lecturer's Name:  Jim Skrentny
//
//////////////////////////// 80 columns wide //////////////////////////////////

/**
 * Represents a set of change(s) made to a repository.
 * <p>Bugs: None known
 * @authors Jeremy
 *
 */
public class ChangeSet {
	
	/* Queue of changes contained within the change set. */
	private final  QueueADT<Change> changes;
	
	/* The name of the repository to which the changes belongs. */
	private final String repoName;
	
	/**
	 * Constructs a change set object. 
	 * @param reponame The name of the repository.
	 * @throws IllegalArgumentException if any argument is null.
	 */
	public ChangeSet(String repoName) {
		this.repoName = repoName;
		this.changes = new SimpleQueue<Change>();
	}
	
	/**
	 * Adds (queues) a new change to the change set.
	 * @param doc The doc to which the change was done.
	 * @param type The type of the change.
	 * @throws IllegalArgumentException if any argument is null.
	 */
	public void addChange(Document doc, Change.Type type) {
		changes.enqueue(new Change(doc, type));
	}
	
	/**
	 * Returns the repository's name to which this change list belongs.
	 * @return The repository's name.
	 */
	public String getReponame() {
		return this.repoName;
	}
	
	/**
	 * Returns and removes the next change from the change set.
	 * @return The next change if present, null otherwise.
	 */
	public Change getNextChange() {
		try {
			return changes.dequeue();
		}
		catch(EmptyQueueException ex) {
			return null;
		}
	}
	
	/**
	* Returns the count of changes contained in the change set.
	* @return The count of changes.
	*/
	public int getChangeCount() {
		return changes.size();
	}
	
	@Override
	public String toString() {
		return this.changes.toString();	
	}
}
