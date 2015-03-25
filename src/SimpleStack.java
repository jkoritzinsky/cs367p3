/**
 * @author Jeff
 *
 */
public class SimpleStack<E> implements StackADT<E> {
	private static final int INITSIZE = 10;
	private E items[];
	private int numItems;
	/**
	 * 
	 */
	public SimpleStack() {
		items = (E[]) new Object[INITSIZE];
		numItems = 0;
	}


	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return numItems;
	}


	private void expand() {
		int newSize = items.length * 2;
		E[] newItems = (E[]) new Object[newSize];
		for(int i = 0; i < numItems; i++) {
			newItems[i] = items[i];
		}
		items = newItems;
	}


	@Override
	public E pop() throws EmptyStackException {
		if(size() != 0) {
			E hold = items[numItems-1];
			items[numItems-1] = null;
			numItems--;
			return hold;
		}
		else {
			throw new EmptyStackException();
		}
	}


	@Override
	public void push(E item) {
		if(items.length == numItems) {
			expand();
		}
		items[numItems] = item;
		numItems++;
	}


	@Override
	public E peek() throws EmptyStackException {
		if(size() != 0) {
			return items[numItems-1];
		}
		else {
			throw new EmptyStackException();
		}
	}
	
	@Override 
	public String toString() {
		StringBuilder str = new StringBuilder();
		for(int i = numItems; i > 0; i--) {
			str.append(items[i-1]);
			str.append('\n');
		}
		return str.toString();
	}
}