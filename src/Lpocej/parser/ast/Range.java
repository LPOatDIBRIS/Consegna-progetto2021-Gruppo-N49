package Lpocej.parser.ast;

import Lpocej.visitors.Visitor;

public class Range extends BinaryOp{											//NEW ADD
	public Range(Exp left, Exp right) {
		super(left, right);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitRange(left, right);
	}
}
