package wci.frontend.c.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;

public class WhileStatementParser extends StatementParser {

	public WhileStatementParser(CParserTD parent) {
		super(parent);
	}

	// sync set for {
	private static final EnumSet<CTokenType> DO_SET = EnumSet.of(LEFT_BRACE, CTokenType.IF, WHILE, IDENTIFIER, SEMICOLON, CTokenType.RETURN, SEMICOLON, RIGHT_BRACE, ELSE);

	// sync set for ( expression )
	private static final EnumSet<CTokenType> PAREN_SET = EnumSet.of(LEFT_PAREN, RIGHT_PAREN);

	public ICodeNode parse(Token token) throws Exception {
		token = nextToken(); // consume while

		// create loop, test, and not nodes
		ICodeNode loopNode = ICodeFactory.createICodeNode(LOOP);
		ICodeNode breakNode = ICodeFactory.createICodeNode(TEST);
		ICodeNode notNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);

		loopNode.addChild(breakNode); // loop node adopts test node as first child
		breakNode.addChild(notNode); // test node adopts not node as only child

		token = synchronize(PAREN_SET); // sync at (
		if (token.getType() == LEFT_PAREN)
			token = nextToken(); // consume (
		else
			errorHandler.flag(token, MISSING_LEFT_PAREN, this);

		// parse expression; not node adopts expression subtree as only child
		ExpressionParser expressionParser = new ExpressionParser(this);
		ICodeNode exprNode = expressionParser.parse(token);
		notNode.addChild(exprNode);

		// type check: test expression must be boolean
		TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec() : Predefined.undefinedType;
		if (!TypeChecker.isBoolean(exprType))
			errorHandler.flag(token, INCOMPATIBLE_TYPES, this);

		token = synchronize(PAREN_SET); // synch at )
		if (token.getType() == RIGHT_PAREN)
			token = nextToken(); // consume )
		else
			errorHandler.flag(token, MISSING_RIGHT_PAREN, this);

		// parse statement; loop node adopts statement subtree as second child
		StatementParser statementParser = new StatementParser(this);
		loopNode.addChild(statementParser.parse(token));

		return loopNode;
	}
	
}
