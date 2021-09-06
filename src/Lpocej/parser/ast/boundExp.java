package Lpocej.parser.ast;

import Lpocej.visitors.Visitor;

public class boundExp extends UnaryOp{
	
	public boundExp (Exp exp) {
		super(exp);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitBound(exp);
	}
}
