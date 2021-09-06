package Lpocej.parser.ast;

import Lpocej.visitors.Visitor;

public interface AST {
	<T> T accept(Visitor<T> visitor);
}
