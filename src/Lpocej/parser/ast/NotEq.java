package Lpocej.parser.ast;

import Lpocej.visitors.Visitor;

public class NotEq extends BinaryOp{											//NEW ADD
	public NotEq(Exp left, Exp right) {
		super(left, right);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitNotEq(left, right);
	}
}
