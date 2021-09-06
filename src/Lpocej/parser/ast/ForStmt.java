package Lpocej.parser.ast;

import static java.util.Objects.requireNonNull;

import Lpocej.visitors.Visitor;


public class ForStmt implements Stmt {

	private final Exp exp;
	private final Block forBlock;
	private final VarIdent ident;
	
	public ForStmt(VarIdent ident, Exp exp, Block forBlock) {
		this.exp = requireNonNull(exp);
		this.forBlock = requireNonNull(forBlock);
		this.ident = requireNonNull(ident);
	}
	@Override
    public String toString() {
        return getClass().getSimpleName() + "(" + ident + "," + exp + "," + forBlock + ")";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitForStmt(ident, exp, forBlock);
    }
	
}
