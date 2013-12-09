package wci.frontend.c.parsers;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.icodeimpl.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.*;

public class CallStandardParser extends CallParser {

	public CallStandardParser(CParserTD parent) {
		super(parent);
	}

	public ICodeNode parse(Token token) throws Exception {
		ICodeNode callNode = ICodeFactory.createICodeNode(CALL);
		SymTabEntry pfId = symTabStack.lookup(token.getText().toLowerCase());
		RoutineCode routineCode = (RoutineCode) pfId.getAttribute(ROUTINE_CODE);
		callNode.setAttribute(ID, pfId);
		token = nextToken(); // consume identifier
		switch ((RoutineCodeImpl) routineCode) {
		
		
		case WRITE:
		case WRITELN:
			return parseWriteWriteln(token, callNode, pfId);
		default:
			return null; // should never get here
		}
	}

	private ICodeNode parseWriteWriteln(Token token, ICodeNode callNode, SymTabEntry pfId) throws Exception {
		// parse any parameters
		ICodeNode parmsNode = parseActualParameters(token, pfId, false, false, true);
		callNode.addChild(parmsNode);
		// write must have parameters
		if ((pfId == Predefined.writeId) && (callNode.getChildren().size() == 0))
			errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
		return callNode;
	}

}
