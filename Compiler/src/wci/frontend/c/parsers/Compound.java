package wci.frontend.c.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;

public class Compound extends StatementParser {

	public Compound(CParserTD parent) {
		super(parent);
	}

	// sync sets for var declaration
	private static final EnumSet<CTokenType> VAR_SET = EnumSet.of(INT, FLOAT, CHAR);

	public ICodeNode parse(Token token) throws Exception {
		token = nextToken(); // consume {
		
		// parse variable declarations
		VarDecParser varDecParser = new VarDecParser(this);
		varDecParser.setDefinition(VARIABLE);
		varDecParser.parse(token, null);
		token = currentToken();
		
		// create compound node
		ICodeNode compoundNode = ICodeFactory.createICodeNode(COMPOUND);

		// parse statement list terminated by }
		StatementParser statementParser = new StatementParser(this);
		statementParser.parseList(token, compoundNode, RIGHT_BRACE, MISSING_RIGHT_BRACE);

		return compoundNode;
	}

}
