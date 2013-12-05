package wci.frontend.c.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.DefinitionImpl;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.COMPOUND;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;

public class ProgramParser extends DeclarationsParser {

	public ProgramParser(CParserTD parent) {
		super(parent);
	}

	// sync set
	static final EnumSet<CTokenType> PROGRAM_START_SET = EnumSet.of(SEMICOLON);

	public SymTabEntry parse(Token token, SymTabEntry parentId) throws Exception {
		
		// create dummy program named test
		SymTabEntry routineId = symTabStack.enterLocal("'Team Redundancy Team'");
		routineId.setDefinition(DefinitionImpl.PROGRAM);

		// create intermediate code for calling main()
		ICode iCode = ICodeFactory.createICode();
		routineId.setAttribute(ROUTINE_ICODE, iCode);
		routineId.setAttribute(ROUTINE_ROUTINES, new ArrayList<SymTabEntry>());

		// push symbol table onto stack
		routineId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());

		// set program identifier in symbol table stack
		symTabStack.setProgramId(routineId);

		
		// NEED TO PARSE VARIABLE AND FUNCTION DECLARATIONS
		
		/*
		VarDecParser varDecParser = new VarDecParser(this);
		varDecParser.setDefinition(VARIABLE);
		varDecParser.parse(token, routineId);
		*/
		/*
		Compound compound = new Compound(this);
		ICodeNode rootNode = compound.parse(token);
        iCode.setRoot(rootNode);
		*/
        
        RoutineParser routineParser = new RoutineParser(this);
        routineParser.parse(token, routineId);
        
		
		/*
		// check if main() exists
		if (symTabStack.lookupLocal("main") == null)
			errorHandler.flag(token, MISSING_MAIN, this);
		else { // call main()
			
			// NEED TO ADD CALL TO main()
			
			
			
			
			
			
			
		}
		*/
		
		// pop program symbol table off stack
		symTabStack.pop();
		
		return null;
	}
	
}
