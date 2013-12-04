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

public class IfStatementParser extends StatementParser {

	public IfStatementParser(CParserTD parent) {
		super(parent);
	}

	// sync set for {
	private static final EnumSet<CTokenType> THEN_SET = EnumSet.of(LEFT_BRACE, CTokenType.IF, WHILE, IDENTIFIER, SEMICOLON, CTokenType.RETURN, SEMICOLON, RIGHT_BRACE, ELSE);

	// sync set for ( expression )
	private static final EnumSet<CTokenType> PAREN_SET = EnumSet.of(LEFT_PAREN, RIGHT_PAREN);

	public ICodeNode parse(Token token) throws Exception {
		token = nextToken(); // consume if
		
		ICodeNode ifNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF); // create if node
		
		token = synchronize(PAREN_SET); // sync at (
		if (token.getType() == LEFT_PAREN)
			token = nextToken(); // consume (
		else
			errorHandler.flag(token, MISSING_LEFT_PAREN, this);

		// parse expression; if node adopts expression subtree as first child
		ExpressionParser expressionParser = new ExpressionParser(this);
		ICodeNode exprNode = expressionParser.parse(token);
		ifNode.addChild(exprNode);

		// type check: expression must be boolean
		TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec() : Predefined.undefinedType;
		if (!TypeChecker.isBoolean(exprType))
			errorHandler.flag(token, INCOMPATIBLE_TYPES, this);

		token = synchronize(PAREN_SET); // sync at )
		if (token.getType() == RIGHT_PAREN)
			token = nextToken(); // consume )
		else
			errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
		
		token = synchronize(THEN_SET); // sync at statement token
		
		// parse then statement; if node adopts statement subtree as second child
		StatementParser statementParser = new StatementParser(this);
		ifNode.addChild(statementParser.parse(token));
		token = currentToken();

		if (token.getType() == ELSE) { // look for else
			token = nextToken(); // consume else
			// parse else statement; if node adopts statement subtree as third child
			ifNode.addChild(statementParser.parse(token));
		}

		return ifNode;
	}
	
}
