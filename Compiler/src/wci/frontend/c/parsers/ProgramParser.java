package wci.frontend.c.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.DefinitionImpl;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.ID;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.LINE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.CALL;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.COMPOUND;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;

public class ProgramParser extends DeclarationsParser {

	public ProgramParser(CParserTD parent) {
		super(parent);
	}

	// sync set
	static final EnumSet<CTokenType> ROUTINE_SET = EnumSet.of(VOID, INTEGER, CHAR, REAL);

	public SymTabEntry parse(Token token, SymTabEntry parentId) throws Exception {
		// create dummy program named test
		SymTabEntry routineId = symTabStack.enterLocal("test");
		routineId.setDefinition(DefinitionImpl.PROGRAM);
		// create intermediate code for calling main()
		ICode iCode = ICodeFactory.createICode();
		routineId.setAttribute(ROUTINE_ICODE, iCode);
		routineId.setAttribute(ROUTINE_ROUTINES, new ArrayList<SymTabEntry>());
		// push symbol table onto stack
		routineId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
		// set program identifier in symbol table stack
		symTabStack.setProgramId(routineId);
		// bump variable slot number to 1
		symTabStack.getLocalSymTab().nextSlotNumber();
		// parse routines
        RoutineParser routineParser = new RoutineParser(this);
        // loop through and parse all routines
        while (ROUTINE_SET.contains(token.getType())) {
        	routineParser.parse(token, routineId);
        	token = currentToken();
        	if (token.getType() != null && !ROUTINE_SET.contains(token.getType())) {
        		token = synchronize(ROUTINE_SET);
        	}
        }
        
		// check if main() exists
        SymTabEntry mainId = symTabStack.lookupLocal("main");
		if (mainId == null)
			errorHandler.flag(token, MISSING_MAIN, this);
		else { // call main()
			ICodeNode callNode = ICodeFactory.createICodeNode(CALL);
			callNode.setAttribute(ID, mainId);
			callNode.setAttribute(LINE, mainId.getLineNumbers().get(0).intValue());
			iCode.setRoot(callNode);
		}
		
		// pop program symbol table off stack
		symTabStack.pop();
		
		return null;
	}
	
}
