package wci.frontend.c.parsers;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import static wci.frontend.c.CTokenType.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

public class CallDeclaredParser extends CallParser {

	public CallDeclaredParser(CParserTD parent) {
		super(parent);
	}

	public ICodeNode parse(Token token) throws Exception {
		// create the CALL node
		ICodeNode callNode = ICodeFactory.createICodeNode(CALL);
		SymTabEntry pfId = symTabStack.lookup(token.getText().toLowerCase());
		callNode.setAttribute(ID, pfId);
		callNode.setTypeSpec(pfId.getTypeSpec());
		token = nextToken(); // consume identifier
		ICodeNode parmsNode = parseActualParameters(token, pfId, true, false, false);
		callNode.addChild(parmsNode);
		return callNode;
	}
	
}
