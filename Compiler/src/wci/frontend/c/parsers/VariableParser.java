package wci.frontend.c.parsers;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.icodeimpl.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.FUNCTION;
import static wci.intermediate.symtabimpl.DefinitionImpl.UNDEFINED;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;
import static wci.intermediate.symtabimpl.DefinitionImpl.VALUE_PARM;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

public class VariableParser extends StatementParser {
	private boolean isReturn = false; // set true to parse function name as target of assignment

	public VariableParser(CParserTD parent) {
		super(parent);
	}

	public ICodeNode parse(Token token) throws Exception {
		// look up identifier in symbol table stack
		String name = token.getText().toLowerCase();
		SymTabEntry variableId = symTabStack.lookup(name);
		// if not found flag error and enter the identifier as undefined with undefined type
		if (variableId == null) {
			errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
			variableId = symTabStack.enterLocal(name);
			variableId.setDefinition(UNDEFINED);
			variableId.setTypeSpec(Predefined.undefinedType);
		}
		return parse(token, variableId);
	}

	public ICodeNode parseReturn(Token token) throws Exception {
		isReturn = true;
		return parse(token);
	}

	public ICodeNode parse(Token token, SymTabEntry variableId) throws Exception {
		// check how variable is defined
		Definition defnCode = variableId.getDefinition();
		if (!((defnCode == VARIABLE) || (defnCode == VALUE_PARM) || (isReturn && (defnCode == FUNCTION))))
			errorHandler.flag(token, INVALID_IDENTIFIER_USAGE, this);
		variableId.appendLineNumber(token.getLineNumber());
		ICodeNode variableNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.VARIABLE);
		variableNode.setAttribute(ID, variableId);
		token = nextToken(); // consume the identifier
		TypeSpec variableType = variableId.getTypeSpec();
		variableNode.setTypeSpec(variableType);
		return variableNode;
	}

}
