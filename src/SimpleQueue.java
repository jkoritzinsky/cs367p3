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
 * An implementation of a queue with a circular array
 * <p>Bugs: None known
 * @author Jeremy
 *
 */
public class SimpleQueue<E> implements QueueADT<E> {

	private static final int INITSIZE = 10;
	private E items[];
	private int numItems;
	private int start;
	
	/**
	 * Defines the queue object
	 */
	public SimpleQueue() {
		items = (E[]) new Object[INITSIZE];
		numItems = 0;
		start = 0;
	}

	/**
	 * checks if there are any items in the queue
	 */
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * removes the item from the front of the queue
	 */
	@Override
	public E dequeue() throws EmptyQueueException {
		if(size() == 0)
			throw new EmptyQueueException();
		E item = items[start];
		items[start] = null;
		start = calculateIndex(1);
		--numItems;
		return item;
	}

	/**
	 * Puts an item at the back of the queue
	 */
	@Override
	public void enqueue(E item) {
		if(item == null)
			throw new IllegalArgumentException();
		if(items.length == numItems) {
			expand();
		}
		items[calculateIndex(numItems)] = item;
		++numItems;
	}

	/**
	 * returns the item at the front of the queue, but doesn't take it out of the queue
	 */
	@Override
	public E peek() throws EmptyQueueException {
		if(size() == 0)
			throw new EmptyQueueException();
		return items[start];
	}

	/**
	 * returns the number of items in the queue
	 */
	@Override
	public int size() {
		return numItems;
	}
	
	/**
	 * Finds the position based on a base index
	 */
	private int calculateIndex(int baseIndex) {
		return (start + baseIndex) % items.length;
	}
	
	/**
	 * Increases the size of the queue
	 */
	private void expand() {
		int newSize = items.length * 2;
		E[] newItems = (E[]) new Object[newSize];
		for(int i = 0; i < numItems; ++i) {
			newItems[i] = items[calculateIndex(i)];
		}
		items = newItems;
		start = 0;
	}
	
	/**
	 * Returns the information in the queue as a string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < numItems; ++i) {
			builder.append(items[calculateIndex(i)]);
			builder.append('\n');
		}
		return builder.toString();
	}

}
