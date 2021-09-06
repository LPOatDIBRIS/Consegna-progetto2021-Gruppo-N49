package Lpocej.parser;

import Lpocej.parser.ast.Prog;

public interface Parser extends AutoCloseable {

	Prog parseProg() throws ParserException;

}