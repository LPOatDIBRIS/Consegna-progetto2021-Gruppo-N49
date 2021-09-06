package Lpocej.parser.ast;

import Lpocej.visitors.Visitor;

public class Fst extends UnaryOp {

	public Fst(Exp exp) {
		super(exp);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitFst(exp);
	}
}
