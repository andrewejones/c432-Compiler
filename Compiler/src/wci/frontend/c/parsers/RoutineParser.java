package wci.frontend.c.parsers;

import java.util.ArrayList;
import java.util.EnumSet;
import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.c.symtabimpl.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.symtabimpl.RoutineCodeImpl.*;

public class RoutineParser extends DeclarationsParser {

	public RoutineParser(CParserTD parent) {
		super(parent);
	}

	private static int dummyCounter = 0; // counter for dummy routine names
	private static final EnumSet<CTokenType> DATA_TYPE_SET = EnumSet.of(INTEGER, REAL, CHAR);
	
	public SymTabEntry parse(Token token, SymTabEntry parentId) throws Exception {
		Definition routineDefn = null;
		String dummyName = null;
		SymTabEntry routineId = null;
		TokenType routineType = token.getType();
		TypeSpec type = null;
		if (routineType == VOID) {
			token = nextToken(); // consume void
			routineDefn = DefinitionImpl.PROCEDURE;
			dummyName = "DummyProcedureName_".toLowerCase() + String.format("%03d", ++dummyCounter);
		} else if (DATA_TYPE_SET.contains(routineType)) {
			// need to parse return type...
			VarDecParser varDecParser = new VarDecParser(this);
			type = varDecParser.parseTypeSpec(token);
			token = currentToken();
			if (token.getText().toLowerCase().equals("main")) { // force main to be void
				routineDefn = DefinitionImpl.PROCEDURE;
				dummyName = "DummyProcedureName_".toLowerCase() + String.format("%03d", ++dummyCounter);
			} else {
				routineDefn = DefinitionImpl.FUNCTION;
				dummyName = "DummyFunctionName_".toLowerCase() + String.format("%03d", ++dummyCounter);
			}
		} else {
			errorHandler.flag(token, UNEXPECTED_TOKEN, this);
			return null;
		}
		// parse routine name
		String name = token.getText();
		routineId = parseRoutineName(token, dummyName);
		routineId.appendLineNumber(token.getLineNumber());
		routineId.setDefinition(routineDefn);
		token = currentToken();
		// create intermediate code for routine
		ICode iCode = ICodeFactory.createICode();
		routineId.setAttribute(ROUTINE_ICODE, iCode);
		routineId.setAttribute(ROUTINE_ROUTINES, new ArrayList<SymTabEntry>());
		// push routines symbol table onto stack; if forwarded, push existing symbol table
		if (routineId.getAttribute(ROUTINE_CODE) == FORWARD)
			symTabStack.push((SymTab) routineId.getAttribute(ROUTINE_SYMTAB));
		else {
			SymTab symtab = symTabStack.push();
			routineId.setAttribute(ROUTINE_SYMTAB, symtab);
			((SymTabImplC)symtab).setFunctionName(name);
			if (routineDefn == DefinitionImpl.FUNCTION)
				((SymTabImplC)symtab).setIsFunction(true);
		}
		// non forwarded procedure, add to parents list of routines
		if (routineId.getAttribute(ROUTINE_CODE) != FORWARD) {
			@SuppressWarnings("unchecked")
			ArrayList<SymTabEntry> subroutines = (ArrayList<SymTabEntry>) parentId.getAttribute(ROUTINE_ROUTINES);
			subroutines.add(routineId);
		}
		// if routine was forwarded ignore parameters
		if (routineId.getAttribute(ROUTINE_CODE) == FORWARD) {
			if (token.getType() == LEFT_PAREN) {
				do {
					token = nextToken();
				} while (token.getType() != RIGHT_PAREN);
				token = nextToken(); // consume )
			}
		// parse routines parameters
		} else
			parseHeader(token, routineId, type);
		token = currentToken();
		// parse routines block or forward declaration
		if (token.getType() == SEMICOLON) {
			token = nextToken(); // consume ;
			routineId.setAttribute(ROUTINE_CODE, FORWARD);
		} else if (token.getType() == LEFT_BRACE) {
			routineId.setAttribute(ROUTINE_CODE, DECLARED);
			Compound compound = new Compound(this);
			ICodeNode rootNode = compound.parse(token);
			iCode.setRoot(rootNode);
		} else
			errorHandler.flag(token, UNEXPECTED_TOKEN, this);
		// pop routine symbol table off stack
		symTabStack.pop();
		return routineId;
	}

	private SymTabEntry parseRoutineName(Token token, String dummyName) throws Exception {
		SymTabEntry routineId = null;
		// parse routine name identifier
		if (token.getType() == IDENTIFIER) {
			String routineName = token.getText().toLowerCase();
			routineId = symTabStack.lookupLocal(routineName);
			// not already defined, enter into the symbol table
			if (routineId == null)
				routineId = symTabStack.enterLocal(routineName);
			// if already defined, it should be a forwarded
			else if (routineId.getAttribute(ROUTINE_CODE) != FORWARD) {
				routineId = null;
				errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
			}
			token = nextToken(); // consume routine name
		} else
			errorHandler.flag(token, MISSING_IDENTIFIER, this);
		// create dummy name symbol table entry if needed
		if (routineId == null)
			routineId = symTabStack.enterLocal(dummyName);
		return routineId;
	}

	private void parseHeader(Token token, SymTabEntry routineId, TypeSpec type) throws Exception {
		// parse parameters
		parseFormalParameters(token, routineId);
		token = currentToken();
		// if function, set return type
		if (routineId.getDefinition() == DefinitionImpl.FUNCTION) {
			if (type == null)
				type = Predefined.undefinedType;
			routineId.setTypeSpec(type);
			token = currentToken();
		}
	}

	// sync set for formal parameter sublist
	private static final EnumSet<CTokenType> PARAMETER_SET = EnumSet.of(INTEGER, REAL, CHAR, RIGHT_PAREN);
	// sync set for (
	private static final EnumSet<CTokenType> LEFT_PAREN_SET = EnumSet.of(LEFT_PAREN);

	protected void parseFormalParameters(Token token, SymTabEntry routineId) throws Exception {
		// parse formal parameters
		token = synchronize(LEFT_PAREN_SET);
		if (token.getType() == LEFT_PAREN) {
			token = nextToken(); // consume (
			ArrayList<SymTabEntry> parms = new ArrayList<SymTabEntry>();
			token = synchronize(PARAMETER_SET);
			TokenType tokenType = token.getType();
			// loops to parse sublists of formal parameter declarations
			while (DATA_TYPE_SET.contains(tokenType)) {
				parms.addAll(parseParmSublist(token, routineId));
				token = currentToken();
				tokenType = token.getType();
			}
			// closing )
			if (token.getType() == RIGHT_PAREN)
				token = nextToken(); // consume )
			else
				errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
			routineId.setAttribute(ROUTINE_PARMS, parms);
		} else
			errorHandler.flag(token, MISSING_LEFT_PAREN, this);
	}

	private ArrayList<SymTabEntry> parseParmSublist(Token token, SymTabEntry routineId) throws Exception {
		// parse parameter sublist and its type spec
		ArrayList<SymTabEntry> sublist = new ArrayList<SymTabEntry>();
		VarDecParser varDecParser = new VarDecParser(this);
		varDecParser.setDefinition(VALUE_PARM);
		TypeSpec type = null;
		SymTabEntry entry = null;
		do {
			type = varDecParser.parseTypeSpec(token);
			token = currentToken();
			entry = varDecParser.parseIdentifier(token);
			entry.setTypeSpec(type);
			sublist.add(entry);
			token = currentToken();
			if (token.getType() == COMMA)
				token = nextToken();
		} while(token.getType() != RIGHT_PAREN);
		for (SymTabEntry parmId : sublist) 
			parmId.setDefinition(VALUE_PARM);
		token = currentToken();
		return sublist;
	}
	
}
