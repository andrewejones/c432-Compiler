package wci.frontend.c.parsers;

import java.util.EnumSet;
import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import static wci.frontend.c.CTokenType.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;

public class DeclarationsParser extends CParserTD {

	public DeclarationsParser(CParserTD parent) {
		super(parent);
	}

	static final EnumSet<CTokenType> TYPE_SET = EnumSet.of(INTEGER, REAL, CHAR);

	public SymTabEntry parse(Token token, SymTabEntry parentId) throws Exception {
		token = synchronize(TYPE_SET);
		VarDecParser variableDeclarationsParser = new VarDecParser(this);
		variableDeclarationsParser.setDefinition(VARIABLE);
		variableDeclarationsParser.parse(token, parentId);
		token = currentToken();
		return null;
	}
}
