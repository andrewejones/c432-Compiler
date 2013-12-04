package wci.frontend.c.parsers;

import java.util.EnumSet;
import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;

public class Assignment extends StatementParser {
	private boolean isFunctionTarget = false; // set true to parse function for assignment

	public Assignment(CParserTD parent) {
		super(parent);
	}

	// sync set for =
	private static final EnumSet<CTokenType> EQUALS_SET = EnumSet.of(PLUS, MINUS, IDENTIFIER, INT, FLOAT, CHAR, CTokenType.NOT, LEFT_PAREN, SINGLE_EQUALS, SEMICOLON, RIGHT_BRACE, ELSE);

	public ICodeNode parse(Token token) throws Exception {
		ICodeNode assignNode = ICodeFactory.createICodeNode(ASSIGN); // create assign node

		// parse variable
		VariableParser variableParser = new VariableParser(this);
		ICodeNode targetNode = isFunctionTarget ? variableParser.parseFunctionNameTarget(token) : variableParser.parse(token);
		TypeSpec targetType = targetNode != null ? targetNode.getTypeSpec() : Predefined.undefinedType;

		assignNode.addChild(targetNode); // assign node adopts variable node as first child

		token = synchronize(EQUALS_SET); // sync on =
		if (token.getType() == SINGLE_EQUALS)
			token = nextToken(); // consume =
		else
			errorHandler.flag(token, MISSING_SINGLE_EQUALS, this);

		// parse expression; assign node adopts expressions node as second child
		ExpressionParser expressionParser = new ExpressionParser(this);
		ICodeNode exprNode = expressionParser.parse(token);
		assignNode.addChild(exprNode);

		// type check: assignment compatible
		TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec() : Predefined.undefinedType;
		if (!TypeChecker.areAssignmentCompatible(targetType, exprType))
			errorHandler.flag(token, INCOMPATIBLE_TYPES, this);

		assignNode.setTypeSpec(targetType);
		return assignNode;
	}

	public ICodeNode parseFunctionNameAssignment(Token token) throws Exception {
		isFunctionTarget = true;
		return parse(token);
	}
	
}
