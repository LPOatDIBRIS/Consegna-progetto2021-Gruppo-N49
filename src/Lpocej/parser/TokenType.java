package Lpocej.parser;

public enum TokenType { 
	// symbols
	ASSIGN, MINUS, PLUS, TIMES, NOT, AND, EQ, NOTEQ, START_PAIR, END_PAIR, STMT_SEP, EXP_SEP, OPEN_PAR, CLOSE_PAR, OPEN_BLOCK, CLOSE_BLOCK, OPEN_SQUARE, CLOSE_SQUARE, RANGE,				//NEW ADD   (NOTEQ,OPEN_SQUARE, CLOSE_SQUARE, NOTEQ, RANGE) NEW SIMBOLS 
	// keywords
	PRINT, VAR, BOOL, IF, ELSE, FST, SND, BOUNDS, FOR, IN, 				//NEW ADD  (BOUNDS , FOR , IN ) NEW KEYWORDS
	// non singleton categories
	SKIP, IDENT, NUM,   
	// end-of-file
	EOF, 
		
}