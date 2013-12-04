package wci.frontend.c.parsers;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

public class Block extends CParserTD {

	public Block(CParserTD parent) {
		super(parent);
	}

	public ICodeNode parse(Token token, SymTabEntry routineId) throws Exception {
		StatementParser statementParser = new StatementParser(this);

		token = synchronize(StatementParser.STMT_START_SET);
		TokenType tokenType = token.getType();
		ICodeNode rootNode = null;
		
		// look for {
		if (tokenType == LEFT_BRACE)
			rootNode = statementParser.parse(token);
		// missing {, attempt to parse anyway
		else {
			errorHandler.flag(token, MISSING_LEFT_BRACE, this);
			if (StatementParser.STMT_START_SET.contains(tokenType)) {
				rootNode = ICodeFactory.createICodeNode(COMPOUND);
				statementParser.parseList(token, rootNode, RIGHT_BRACE,
						MISSING_RIGHT_BRACE);
			}
		}
		return rootNode;
	}
	
}
