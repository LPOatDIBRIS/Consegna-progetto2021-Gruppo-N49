package Lpocej.visitors;

import Lpocej.parser.ast.Block;
import Lpocej.parser.ast.Exp;
import Lpocej.parser.ast.Stmt;
import Lpocej.parser.ast.StmtSeq;
import Lpocej.parser.ast.VarIdent;

public interface Visitor<T> {
	T visitAdd(Exp left, Exp right);

	T visitAssignStmt(VarIdent ident, Exp exp);

	T visitIntLiteral(int value);

	T visitEq(Exp left, Exp right);

	T visitMoreStmt(Stmt first, StmtSeq rest);

	T visitMul(Exp left, Exp right);

	T visitPrintStmt(Exp exp);

	T visitProg(StmtSeq stmtSeq);

	T visitSign(Exp exp);

	T visitVarIdent(VarIdent id); // the only corner case ...

	T visitSingleStmt(Stmt stmt);

	T visitVarStmt(VarIdent ident, Exp exp);

	T visitNot(Exp exp);

	T visitAnd(Exp left, Exp right);

	T visitBoolLiteral(boolean value);

	T visitIfStmt(Exp exp, Block thenBlock, Block elseBlock);

	T visitBlock(StmtSeq stmtSeq);

	T visitPairLit(Exp left, Exp right);

	T visitFst(Exp exp);

	T visitSnd(Exp exp);
	
	T visitNotEq(Exp left, Exp right); 												// NEW ADD
	 
	T visitRange(Exp left, Exp right);												// NEW ADD
	
	T visitBound(Exp exp);        											        // NEW ADD
	
	T visitForStmt(VarIdent ident,  Exp exp,  Block thenBlock);  					// NEW ADD
	
	
}
