package Lpocej.visitors.evaluation;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class RangeValue implements Value,  Iterable<Integer> {								//NEW ADD

	private final Integer first;
	private final Integer last;							

	public RangeValue(Integer fstVal, Integer sndVal) {
		this.first = requireNonNull(fstVal);
		this.last = requireNonNull(sndVal);
	}
	
	public Integer getFirst() {
		return first;
	}

	public Integer getLast() {
		return last;
	}

	@Override
	public RangeValue toRange() {
		return this;
	}
	
	@Override
	public String toString() {
		return "[" + first + " : " + last + "]";	
	}
	
	@Override
	public int hashCode() {
		return hash(first,last);
	}
	
	@Override							
	public final boolean equals(Object obj) {
		if (obj==null || !(obj instanceof RangeValue)) return false;
		if ((this.iterator().hasNext()!=((RangeValue) obj).iterator().hasNext())) return false;
		if (!this.iterator().hasNext()) return true;
		return first.equals(((RangeValue) obj).first) && last.equals(((RangeValue) obj).last);
	}
	
	@Override
	public RangeIterator iterator() {
		return new RangeIterator(first, last);
	}
}
