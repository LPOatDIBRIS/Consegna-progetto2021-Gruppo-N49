package Lpocej.parser;

import static java.util.Objects.requireNonNull;
import static Lpocej.parser.TokenType.*;
//import static java.lang.System.err;

//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.io.IOException;
//import java.io.InputStreamReader;

import Lpocej.parser.ast.*;

public class BufferedParser implements Parser {

	private final BufferedTokenizer buf_tokenizer; // the buffered tokenizer used by the parser

	/*
	 * reads the next token through the buffered tokenizer associated with the
	 * parser; TokenizerExceptions are chained into corresponding ParserExceptions
	 */
	private void nextToken() throws ParserException {
		try {
			buf_tokenizer.next();
		} catch (TokenizerException e) {
			throw new ParserException(e);
		}
	}

	// decorates error message with the corresponding line number
	private String line_err_msg(String msg) {
		return "on line " + buf_tokenizer.getLineNumber() + ": " + msg;
	}

	/*
	 * checks whether the token type of the currently recognized token matches
	 * 'expected'; if not, it throws a corresponding ParserException
	 */
	private void match(TokenType expected) throws ParserException {
		final var found = buf_tokenizer.tokenType();
		if (found != expected)
			throw new ParserException(line_err_msg(
					"Expecting " + expected + ", found " + found + "('" + buf_tokenizer.tokenString() + "')"));
	}

	/*
	 * checks whether the token type of the currently recognized token matches
	 * 'expected'; if so, it reads the next token, otherwise it throws a
	 * corresponding ParserException
	 */
	private void consume(TokenType expected) throws ParserException {
		match(expected);
		nextToken();
	}

	// throws a ParserException because the current token was not expected
	private void unexpectedTokenError() throws ParserException {
		throw new ParserException(line_err_msg(
				"Unexpected token " + buf_tokenizer.tokenType() + "('" + buf_tokenizer.tokenString() + "')"));
	}

	// associates the parser with a corresponding non-null buffered tokenizer
	public BufferedParser(BufferedTokenizer tokenizer) {
		this.buf_tokenizer = requireNonNull(tokenizer);
	}

	/* parses a program
	 * Prog ::= StmtSeq EOF
	 */
	@Override
	public Prog parseProg() throws ParserException { // 
		nextToken(); // one look-ahead symbol
		var prog = new ProgAST(parseStmtSeq());
		match(EOF); // last token must have type EOF
		return prog;
	}

	@Override
	public void close() throws IOException {
		if (buf_tokenizer != null)
			buf_tokenizer.close();
	}

	/* parses a non empty sequence of statements, MoreStmt binary operator is right associative
	 * StmtSeq ::= Stmt (';' StmtSeq)? 
	 */
	private StmtSeq parseStmtSeq() throws ParserException {
		var stmt = parseStmt();
		if (buf_tokenizer.tokenType() == STMT_SEP) {
			nextToken();
			return new MoreStmt(stmt, parseStmtSeq());
		}
		return new SingleStmt(stmt);
	}

	/* parses a statement
	 * Stmt ::= 'var'? IDENT '=' Exp | 'print' Exp |  'if' '(' Exp ')' Block ('else' Block)? |'for' IDENT 'in' Exp Block 
		(ho un or nascosto in ident )
	 */
	
	private Stmt parseStmt() throws ParserException {												// NEW ADD
		switch (buf_tokenizer.tokenType()) {
		default:
			unexpectedTokenError();
		case PRINT:
			return parsePrintStmt();
		case VAR:
			return parseVarStmt();
		case IDENT:
			return parseAssignStmt();
		case IF:
			return parseIfStmt();
		case FOR:
			return parseForStmt();
			
		}
	}
	
	/* parses the 'print' statement
	 * Stmt ::= 'print' Exp
	 */
	private PrintStmt parsePrintStmt() throws ParserException {
		consume(PRINT); // or nextToken() since PRINT has already been recognized
		return new PrintStmt(parseAnd());
	}

	/* parses the 'var' statement
	 * Stmt ::= 'var' IDENT '=' Exp 
	 */
	private VarStmt parseVarStmt() throws ParserException {
		consume(VAR); // or nextToken() since VAR has already been recognized
		var ident = parseVarIdent();
		consume(ASSIGN);
		return new VarStmt(ident, parseAnd());
	}

	/* parses the assignment statement
	 * Stmt ::= IDENT '=' Exp 
	 */
	private AssignStmt parseAssignStmt() throws ParserException {
		var ident = parseVarIdent();
		consume(ASSIGN);
		return new AssignStmt(ident, parseAnd());
	}

	/* parses the 'if' statement
	 * Stmt ::= 'if' '(' Exp ')' Block ('else' Block)? 
	 */
	private IfStmt parseIfStmt() throws ParserException {
		consume(IF); // or nextToken() since IF has already been recognized
		consume(OPEN_PAR);
		var exp = parseAnd();
		consume(CLOSE_PAR);
		var thenBlock = parseBlock();
		if (buf_tokenizer.tokenType() != ELSE)
			return new IfStmt(exp, thenBlock);
		consume(ELSE); // or nextToken() since ELSE has already been recognized
		var elseBlock = parseBlock();
		return new IfStmt(exp, thenBlock, elseBlock);
	}
	
	/*
	'for' IDENT 'in' Exp Block 																		
	*/
	private ForStmt parseForStmt() throws ParserException {											//NEW ADD
		consume(FOR);
		var ident = parseVarIdent();
		consume(IN);
		var exp = parseRange();
		var forBlock = parseBlock();
		return new ForStmt(ident, exp, forBlock);
	}


	/* parses a block of statements
	 * Block ::= '{' StmtSeq '}'
	 */
	private Block parseBlock() throws ParserException {
		consume(OPEN_BLOCK);
		var stmts = parseStmtSeq();
		consume(CLOSE_BLOCK);
		return new Block(stmts);
	}
	
	/*
	 * parses expressions, starting from the lowest precedence operator AND which is left-associative
	 * Exp ::= Eq ('&&' Eq)* 
	 *
	 */
	private Exp parseAnd() throws ParserException {
		var exp = parseEq();
		while (buf_tokenizer.tokenType() == AND) {
			nextToken();
			exp = new And(exp, parseEq());
		}
		return exp;
	}

	/*
	 * parses expressions, starting from the lowest precedence operator EQ which is left-associative
	 * Eq ::= Add ('==' Add)* 
	 * Exp ::= Eq ('!=' Eq)*  
	 */
	private Exp parseEq() throws ParserException {													//NEW ADD
		var exp = parseRange();
		while (buf_tokenizer.tokenType() == EQ  || buf_tokenizer.tokenType() == NOTEQ) {
			var token= buf_tokenizer.tokenType();
			nextToken();
			exp = (token == EQ)      
					? new Eq(exp, parseRange())                      
							: new NotEq(exp, parseRange());
		}
		return exp;
	}
	
	/*
	 * parses expressions, starting from the lowest precedence operator RANGE which is left-associative	
	 * Exp ::= [ Exp : Exp ] 
	 *
	 */
		
	private Exp parseRange() throws ParserException {												//NEW ADD 
		if(buf_tokenizer.tokenType() == OPEN_SQUARE) {
			consume(OPEN_SQUARE);
			var left = parseBound();
			consume(RANGE);
			var right =parseBound();
			consume(CLOSE_SQUARE);
			var exp = new Range(left, right);
			return exp;
		}
		return parseBound();
	}
	
	/*
	 * parses expressions, starting from the lowest precedence operator BOUNDS which is left-associative			
	 * Exp ::=  'bound' Exp 
	 *
	 */
	
	private Exp parseBound() throws ParserException {											//NEW ADD
		if(buf_tokenizer.tokenType() == BOUNDS) {
			nextToken();
			return new boundExp(parseRange());
		}
		return parseAdd();
	}

	/*
	 * parses expressions, starting from the lowest precedence operator PLUS which is left-associative
	 * Add ::= Mul ('+' Mul)*
	 */
	private Exp parseAdd() throws ParserException {
		var exp = parseMul();
		while (buf_tokenizer.tokenType() == PLUS) {
			nextToken();
			exp = new Add(exp, parseMul());
		}
		return exp;
	}

	/*
	 * parses expressions, starting from the lowest precedence operator TIMES which is left-associative
	 * Mul::= Atom ('*' Atom)*
	 */
	private Exp parseMul() throws ParserException {
		var exp = parseAtom();
		while (buf_tokenizer.tokenType() == TIMES) {
			nextToken();
			exp = new Mul(exp, parseAtom());
		}
		return exp;
	}

	/* parses expressions of type Atom
	 * Atom ::= '<<' Exp ',' Exp '>>' | 'fst' Atom | 'snd' Atom | '-' Atom | '!' Atom | BOOL | NUM | IDENT | '(' Exp ')'
	 */
	private Exp parseAtom() throws ParserException {
		switch (buf_tokenizer.tokenType()) {
		default:
			unexpectedTokenError();
		case NUM:
			return parseNum();
		case BOUNDS:											//NEW ADD
			return parseBound();
		case IDENT:
			return parseVarIdent();
		case MINUS:
			return parseMinus();
		case OPEN_PAR:
			return parseRoundPar();
		case BOOL:
			return parseBoolean();
		case NOT:
			return parseNot();
		case START_PAIR:
			return parsePairLit();
		case FST:
			return parseFst();
		case SND:
			return parseSnd();
		}
	}

	// parses number literals
	private IntLiteral parseNum() throws ParserException {
		var val = buf_tokenizer.intValue();
		consume(NUM); // or nextToken() since NUM has already been recognized
		return new IntLiteral(val);
	}

	// parses boolean literals
	private BoolLiteral parseBoolean() throws ParserException {
		var val = buf_tokenizer.boolValue();
		consume(BOOL); // or nextToken() since BOOL has already been recognized
		return new BoolLiteral(val);
	}

	// parses variable identifiers
	private VarIdentAST parseVarIdent() throws ParserException {
		var name = buf_tokenizer.tokenString();
		consume(IDENT); // or nextToken() since IDENT has already been recognized
		return new VarIdentAST(name);
	}

	/* parses expressions with unary operator MINUS
	 * Atom ::= '-' Atom 
	 */
	private Sign parseMinus() throws ParserException {
		consume(MINUS); // or nextToken() since MINUS has already been recognized
		return new Sign(parseAtom());
	}

	/* parses expressions with unary operator FST
	 * Atom ::= 'fst' Atom 
	 */
	private Fst parseFst() throws ParserException {
		consume(FST); // or nextToken() since FST has already been recognized
		return new Fst(parseAtom());
	}

	/* parses expressions with unary operator SND
	 * Atom ::= 'snd' Atom 
	 */	
	private Snd parseSnd() throws ParserException {
		consume(SND); // or nextToken() since SND has already been recognized
		return new Snd(parseAtom());
	}

	/* parses expressions with unary operator NOT
	 * Atom ::= '!' Atom 
	 */	
	 private Not parseNot() throws ParserException {
		consume(NOT); // or nextToken() since NOT has already been recognized
		return new Not(parseAtom());
	}

	/* parses pair literals
	 * Atom ::= '<<' Exp ',' Exp '>>'
	 */
	private PairLit parsePairLit() throws ParserException {
		consume(START_PAIR); // or nextToken() since START_PAIR has already been recognized
		var left = parseAnd();
		consume(EXP_SEP);
		var right = parseAnd();
		consume(END_PAIR);
		return new PairLit(left, right);
	}

	/* parses expressions delimited by parentheses
	 * Atom ::= '(' Exp ')'
	 */
	
	private Exp parseRoundPar() throws ParserException {
		consume(OPEN_PAR); // or nextToken() since OPEN_PAR has already been recognized
		var exp = parseAnd();
		consume(CLOSE_PAR);
		return exp;
	}
/*
	private static BufferedReader tryOpenInput(String inputPath) throws FileNotFoundException {
		return new BufferedReader(inputPath == null ? new InputStreamReader(System.in) : new FileReader(inputPath));
	}

	public static void main(String[] args) {
		try (var buf_reader = tryOpenInput(args.length > 0 ? args[0] : null);
				var buf_tokenizer = new BufferedTokenizer(buf_reader);
				var buf_parser = new BufferedParser(buf_tokenizer);) {
			var prog = buf_parser.parseProg();
			System.out.println(prog);
		} catch (IOException e) {
			err.println("I/O error: " + e.getMessage());
		} catch (ParserException e) {
			err.println("Syntax error " + e.getMessage());
		} catch (Throwable e) {
			err.println("Unexpected error.");
			e.printStackTrace();
		}
	}
	*/
}