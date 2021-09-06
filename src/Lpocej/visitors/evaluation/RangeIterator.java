package Lpocej.visitors.evaluation;

import java.util.Iterator;
import java.util.NoSuchElementException;

class RangeIterator implements Iterator<Integer> {					//NEW ADD

	private int next;
	private final int end;

	 RangeIterator(int start, int end) {
		this.next = start;
		this.end = end;
	}
	 
	@Override
	public boolean hasNext() {
		return (next<end || next>end); 
	}

	@Override
	public Integer next() {
		if (!hasNext())
			throw new NoSuchElementException();
		if(next<end) return next++;
		return next--;
	}
}