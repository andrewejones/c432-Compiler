package wci.frontend.c.parsers;

import java.util.ArrayList;
import java.util.EnumSet;
import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.icodeimpl.*;
import wci.intermediate.typeimpl.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.symtabimpl.RoutineCodeImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.*;

public class CallParser extends StatementParser {
	
	public CallParser(CParserTD parent) {
		super(parent);
	}

	public ICodeNode parse(Token token) throws Exception {
		SymTabEntry pfId = symTabStack.lookup(token.getText().toLowerCase());
		RoutineCode routineCode = (RoutineCode) pfId.getAttribute(ROUTINE_CODE);
		StatementParser callParser = (routineCode == DECLARED) || (routineCode == FORWARD) ? new CallDeclaredParser(this) : new CallStandardParser(this);
		return callParser.parse(token);
	}

	// sync set ,
	private static final EnumSet<CTokenType> COMMA_SET = EnumSet.of(PLUS, MINUS, IDENTIFIER, INT, FLOAT, CHAR, CTokenType.NOT, LEFT_PAREN, COMMA, RIGHT_PAREN);

	protected ICodeNode parseActualParameters(Token token, SymTabEntry pfId, boolean isDeclared, boolean isReadReadln, boolean isWriteWriteln) throws Exception {
		ExpressionParser expressionParser = new ExpressionParser(this);
		ICodeNode parmsNode = ICodeFactory.createICodeNode(PARAMETERS);
		ArrayList<SymTabEntry> formalParms = null;
		int parmCount = 0;
		int parmIndex = -1;
		if (isDeclared) {
			formalParms = (ArrayList<SymTabEntry>) pfId.getAttribute(ROUTINE_PARMS);
			parmCount = formalParms != null ? formalParms.size() : 0;
		}
		if (token.getType() != LEFT_PAREN) {
			if (parmCount != 0)
				errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
			return null;
		}
		token = nextToken(); // consume opening (

		// loop to parse parameters
		while (token.getType() != RIGHT_PAREN) {
			ICodeNode actualNode = expressionParser.parse(token);
			// Declared procedure or function: Check the number of actual
			// parameters, and check each actual parameter against the
			// corresponding formal parameter.
			if (isDeclared) {
				if (++parmIndex < parmCount) {
					SymTabEntry formalId = formalParms.get(parmIndex);
					checkActualParameter(token, formalId, actualNode);
				} else if (parmIndex == parmCount) {
					errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
				}
			}

			// write or writeln: The type of each actual parameter must be a
			// scalar, boolean, or a C string. Parse any field width and
			// precision.
			else if (isWriteWriteln) {

				// Create a WRITE_PARM node which adopts the expression node.
				ICodeNode exprNode = actualNode;
				actualNode = ICodeFactory.createICodeNode(WRITE_PARM);
				actualNode.addChild(exprNode);

				TypeSpec type = exprNode.getTypeSpec().baseType();
				TypeForm form = type.getForm();

				if (!((form == SCALAR) || (type == Predefined.booleanType) || (type
						.isCString()))) {
					errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
				}

				// Optional field width.
				token = currentToken();
				actualNode.addChild(parseWriteSpec(token));

				// Optional precision.
				token = currentToken();
				actualNode.addChild(parseWriteSpec(token));
			}

			parmsNode.addChild(actualNode);
			token = synchronize(COMMA_SET);
			TokenType tokenType = token.getType();

			// Look for the comma.
			if (tokenType == COMMA) {
				token = nextToken(); // consume ,
			} else if (ExpressionParser.EXPR_START_SET.contains(tokenType)) {
				errorHandler.flag(token, MISSING_COMMA, this);
			} else if (tokenType != RIGHT_PAREN) {
				token = synchronize(ExpressionParser.EXPR_START_SET);
			}
		}

		token = nextToken(); // consume closing )

		if ((parmsNode.getChildren().size() == 0)
				|| (isDeclared && (parmIndex != parmCount - 1))) {
			errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
		}

		return parmsNode;
	}

	/**
	 * Check an actual parameter against the corresponding formal parameter.
	 * 
	 * @param token
	 *            the current token.
	 * @param formalId
	 *            the symbol table entry of the formal parameter.
	 * @param actualNode
	 *            the parse tree node of the actual parameter.
	 */
	private void checkActualParameter(Token token, SymTabEntry formalId,
			ICodeNode actualNode) {
		TypeSpec formalType = formalId.getTypeSpec();
		TypeSpec actualType = actualNode.getTypeSpec();

		// Value parameter: The actual parameter must be assignment-compatible
		// with the formal parameter.
		if (!TypeChecker.areAssignmentCompatible(formalType, actualType)) {
			errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
		}
	}

	/**
	 * Parse the field width or the precision for an actual parameter of a call
	 * to write or writeln.
	 * 
	 * @param token
	 *            the current token.
	 * @return the INTEGER_CONSTANT node or null
	 * @throws Exception
	 *             if an error occurred.
	 */
	private ICodeNode parseWriteSpec(Token token) throws Exception {
		if (token.getType() == COLON) {
			token = nextToken(); // consume :

			ExpressionParser expressionParser = new ExpressionParser(this);
			ICodeNode specNode = expressionParser.parse(token);

			if (specNode.getType() == INTEGER_CONSTANT) {
				return specNode;
			} else {
				errorHandler.flag(token, INVALID_NUMBER, this);
				return null;
			}
		} else {
			return null;
		}
	}
}
