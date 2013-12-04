package wci.frontend.c.parsers;

import java.util.ArrayList;
import java.util.EnumSet;
import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.symtabimpl.RoutineCodeImpl.*;

public class RoutineParser extends DeclarationsParser {

	public RoutineParser(CParserTD parent) {
		super(parent);
	}

	private static int dummyCounter = 1; // counter for dummy routine names

	public SymTabEntry parse(Token token, SymTabEntry parentId) throws Exception {
		Definition routineDefn = null;
		String dummyName = null;
		SymTabEntry routineId = null;
		TokenType routineType = token.getType();
		switch ((CTokenType) routineType) {
			case PROCEDURE: {
				token = nextToken(); // consume PROCEDURE
				routineDefn = DefinitionImpl.PROCEDURE;
				dummyName = "DummyProcedureName_".toLowerCase()
						+ String.format("%03d", ++dummyCounter);
				break;
			}
			case FUNCTION: {
				token = nextToken(); // consume FUNCTION
				routineDefn = DefinitionImpl.FUNCTION;
				dummyName = "DummyFunctionName_".toLowerCase()
						+ String.format("%03d", ++dummyCounter);
				break;
			}
			default: {
				break;
			}
		}

		// parse routine name
		routineId = parseRoutineName(token, dummyName);
		routineId.setDefinition(routineDefn);
		token = currentToken();
		
		// create intermediate code for routine
		ICode iCode = ICodeFactory.createICode();
		routineId.setAttribute(ROUTINE_ICODE, iCode);
		routineId.setAttribute(ROUTINE_ROUTINES, new ArrayList<SymTabEntry>());

		// push routines symbol table onto stack; if forwarded, push existing symbol table
		if (routineId.getAttribute(ROUTINE_CODE) == FORWARD) {
			SymTab symTab = (SymTab) routineId.getAttribute(ROUTINE_SYMTAB);
			symTabStack.push(symTab);
		} else
			routineId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());

		// non forwarded procedure: add to parents list of routines
		if (routineId.getAttribute(ROUTINE_CODE) != FORWARD) {
			ArrayList<SymTabEntry> subroutines = (ArrayList<SymTabEntry>) parentId.getAttribute(ROUTINE_ROUTINES);
			subroutines.add(routineId);
		}

		// if routine was forwarded ignore parameters
		if (routineId.getAttribute(ROUTINE_CODE) == FORWARD)
			if (token.getType() == LEFT_PAREN)
				do
					token = nextToken();
				while(token.getType() != RIGHT_PAREN);

		// parse routines parameters and return type
		else
			parseHeader(token, routineId);

		// Look for the semicolon.
		token = currentToken();
		if (token.getType() == SEMICOLON) {
			do {
				token = nextToken(); // consume ;
			} while (token.getType() == SEMICOLON);
		} else {
			errorHandler.flag(token, MISSING_SEMICOLON, this);
		}

		// Parse the routine's block or forward declaration.
		if ((token.getType() == IDENTIFIER)
				&& (token.getText().equalsIgnoreCase("forward"))) {
			token = nextToken(); // consume forward
			routineId.setAttribute(ROUTINE_CODE, FORWARD);
		} else {
			routineId.setAttribute(ROUTINE_CODE, DECLARED);

			Block blockParser = new Block(this);
			ICodeNode rootNode = blockParser.parse(token, routineId);
			iCode.setRoot(rootNode);
		}

		// Pop the routine's symbol table off the stack.
		symTabStack.pop();

		return routineId;
	}

	/**
	 * Parse a routine's name.
	 * 
	 * @param token
	 *            the current token.
	 * @param routineDefn
	 *            how the routine is defined.
	 * @param dummyName
	 *            a dummy name in case of parsing problem.
	 * @return the symbol table entry of the declared routine's name.
	 * @throws Exception
	 *             if an error occurred.
	 */
	private SymTabEntry parseRoutineName(Token token, String dummyName)
			throws Exception {
		SymTabEntry routineId = null;

		// Parse the routine name identifier.
		if (token.getType() == IDENTIFIER) {
			String routineName = token.getText().toLowerCase();
			routineId = symTabStack.lookupLocal(routineName);

			// Not already defined locally: Enter into the local symbol table.
			if (routineId == null) {
				routineId = symTabStack.enterLocal(routineName);
			}

			// If already defined, it should be a forward definition.
			else if (routineId.getAttribute(ROUTINE_CODE) != FORWARD) {
				routineId = null;
				errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
			}

			token = nextToken(); // consume routine name identifier
		} else {
			errorHandler.flag(token, MISSING_IDENTIFIER, this);
		}

		// If necessary, create a dummy routine name symbol table entry.
		if (routineId == null) {
			routineId = symTabStack.enterLocal(dummyName);
		}

		return routineId;
	}

	private void parseHeader(Token token, SymTabEntry routineId) throws Exception {
		// parse routines parameters
		parseFormalParameters(token, routineId);
		token = currentToken();

		// If this is a function, parse and set its return type.
		if (routineId.getDefinition() == DefinitionImpl.FUNCTION) {
			VarDecParser variableDeclarationsParser = new VarDecParser(this);
			variableDeclarationsParser.setDefinition(DefinitionImpl.FUNCTION);
			TypeSpec type = variableDeclarationsParser.parseTypeSpec(token);

			token = currentToken();

			// The return type cannot be an array or record.
			if (type != null) {
				TypeForm form = type.getForm();
				if ((form == TypeFormImpl.ARRAY)
						|| (form == TypeFormImpl.RECORD)) {
					errorHandler.flag(token, INVALID_TYPE, this);
				}
			}

			// Missing return type.
			else {
				type = Predefined.undefinedType;
			}

			routineId.setTypeSpec(type);
			token = currentToken();
		}
	}

	// Synchronization set for a formal parameter sublist.
	private static final EnumSet<CTokenType> PARAMETER_SET = EnumSet.of(
			LEFT_BRACE, IDENTIFIER, RIGHT_PAREN);

	// Synchronization set for the opening left parenthesis.
	private static final EnumSet<CTokenType> LEFT_PAREN_SET = EnumSet.of(
			LEFT_BRACE, LEFT_PAREN, SEMICOLON);

	// Synchronization set for the closing right parenthesis.
	private static final EnumSet<CTokenType> RIGHT_PAREN_SET = EnumSet.of(
			LEFT_BRACE, RIGHT_PAREN, SEMICOLON);

	protected void parseFormalParameters(Token token, SymTabEntry routineId) throws Exception {
		// Parse the formal parameters if there is an opening left parenthesis.
		token = synchronize(LEFT_PAREN_SET);
		if (token.getType() == LEFT_PAREN) {
			token = nextToken(); // consume (

			ArrayList<SymTabEntry> parms = new ArrayList<SymTabEntry>();

			token = synchronize(PARAMETER_SET);
			TokenType tokenType = token.getType();

			// Loop to parse sublists of formal parameter declarations.
			while (tokenType == IDENTIFIER) {
				parms.addAll(parseParmSublist(token, routineId));
				token = currentToken();
				tokenType = token.getType();
			}

			// Closing right parenthesis.
			if (token.getType() == RIGHT_PAREN) {
				token = nextToken(); // consume )
			} else {
				errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
			}

			routineId.setAttribute(ROUTINE_PARMS, parms);
		}
	}

	// Synchronization set to follow a formal parameter identifier.
	private static final EnumSet<CTokenType> PARAMETER_FOLLOW_SET = EnumSet.of(
			RIGHT_PAREN, SEMICOLON);
	static {
		PARAMETER_FOLLOW_SET.addAll(DeclarationsParser.DECLARATION_START_SET);
	}

	// Synchronization set for the , token.
	private static final EnumSet<CTokenType> COMMA_SET = EnumSet.of(COMMA,
			IDENTIFIER, RIGHT_PAREN, SEMICOLON);
	static {
		COMMA_SET.addAll(DeclarationsParser.DECLARATION_START_SET);
	}

	/**
	 * Parse a sublist of formal parameter declarations.
	 * 
	 * @param token
	 *            the current token.
	 * @param routineId
	 *            the symbol table entry of the declared routine's name.
	 * @return the sublist of symbol table entries for the parm identifiers.
	 * @throws Exception
	 *             if an error occurred.
	 */
	private ArrayList<SymTabEntry> parseParmSublist(Token token,
			SymTabEntry routineId) throws Exception {
		boolean isProgram = routineId.getDefinition() == DefinitionImpl.PROGRAM;
		Definition parmDefn = isProgram ? PROGRAM_PARM : null;
		TokenType tokenType = token.getType();

		// Value parameter
		if (!isProgram) {
			parmDefn = VALUE_PARM;
		}

		// Parse the parameter sublist and its type specification.
		VarDecParser variableDeclarationsParser = new VarDecParser(this);
		variableDeclarationsParser.setDefinition(parmDefn);
		ArrayList<SymTabEntry> sublist = variableDeclarationsParser
				.parseIdentifierSublist(token, PARAMETER_FOLLOW_SET, COMMA_SET);
		token = currentToken();
		tokenType = token.getType();

		if (!isProgram) {

			// Look for one or more semicolons after a sublist.
			if (tokenType == SEMICOLON) {
				while (token.getType() == SEMICOLON) {
					token = nextToken(); // consume the ;
				}
			}

			// If at the start of the next sublist, then missing a semicolon.
			else if (VarDecParser.NEXT_START_SET.contains(tokenType)) {
				errorHandler.flag(token, MISSING_SEMICOLON, this);
			}

			token = synchronize(PARAMETER_SET);
		}

		return sublist;
	}
}
