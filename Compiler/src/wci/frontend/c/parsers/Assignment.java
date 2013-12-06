package wci.frontend.c.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.ID;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;

public class Assignment extends StatementParser {
	private boolean isReturn = false; // set true to parse function for assignment

	public Assignment(CParserTD parent) {
		super(parent);
	}

	// sync set for =
	private static final EnumSet<CTokenType> EQUALS_SET = EnumSet.of(PLUS, MINUS, IDENTIFIER, INT, FLOAT, CHAR, CTokenType.NOT, LEFT_PAREN, SINGLE_EQUALS, SEMICOLON, RIGHT_BRACE, ELSE);

	public ICodeNode parse(Token token) throws Exception {
		ICodeNode assignNode = ICodeFactory.createICodeNode(ASSIGN); // create assign node
		SymTabEntry targetid = null;
		String targetname = token.getText();
		if (targetname.equals("return") || isReturn) {
			SymTab symtab = symTabStack.getLocalSymTab();
			targetname = ((SymTabImpl)symtab).funcname;
			if (targetname == null)
				targetname = "unknownfunction";
		}
		targetid = symTabStack.lookup(targetname);
		if (targetid == null)
			errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
		else {
			targetid.appendLineNumber(token.getLineNumber());
        	token = nextToken();  // consume the identifier token
        	// create variable node and set name attribute
        	ICodeNode variableNode = ICodeFactory.createICodeNode(VARIABLE);
        	variableNode.setAttribute(ID, targetid);
        	variableNode.setTypeSpec(targetid.getTypeSpec()); 
        	// assign node adopts variable node as first child
        	assignNode.addChild(variableNode);
        	
        	// Set ASSIGN node type spec
        	assignNode.setTypeSpec(targetid.getTypeSpec()!=null?
        			targetid.getTypeSpec():Predefined.undefinedType);
        }
        // sync on = token
        token = synchronize(EQUALS_SET);
        if (token.getType() == SINGLE_EQUALS)
        	token = nextToken();  // consume  =
        else
        	if (isReturn == false)
        		errorHandler.flag(token, MISSING_SINGLE_EQUALS, this);
        // parse expression; assign adopts expressions node as second child
        ExpressionParser expressionParser = new ExpressionParser(this);
        assignNode.addChild(expressionParser.parse(token));
		return assignNode;
	}

	public ICodeNode parseReturn(Token token) throws Exception {
		isReturn = true;
		return parse(token);
	}
	
}
