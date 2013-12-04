package wci.frontend.c.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;

/**
 * <h1>DeclarationsParser</h1>
 *
 * <p>Parse C declarations.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class DeclarationsParser extends CParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public DeclarationsParser(CParserTD parent)
    {
        super(parent);
    }

    static final EnumSet<CTokenType> DECLARATION_START_SET = EnumSet.of(IDENTIFIER);
    
    /**
     * Parse declarations.
     * To be overridden by the specialized declarations parser subclasses.
     * @param token the initial token.
     * @param parentId the symbol table entry of the parent routine's name.
     * @return null
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry parentId)
        throws Exception
    {
    	token = synchronize(DECLARATION_START_SET);
    	
        VarDecParser variableDeclarationsParser = new VarDecParser(this);
        variableDeclarationsParser.setDefinition(VARIABLE);
        variableDeclarationsParser.parse(token, parentId);
	    token = currentToken();
        
        /*
        token = synchronize(ROUTINE_START_SET);
        TokenType tokenType = token.getType();

        while ((tokenType == PROCEDURE) || (tokenType == FUNCTION)) {
            DeclaredRoutineParser routineParser =
                new DeclaredRoutineParser(this);
            routineParser.parse(token, parentId);

            // Look for one or more semicolons after a definition.
            token = currentToken();
            if (token.getType() == SEMICOLON) {
                while (token.getType() == SEMICOLON) {
                    token = nextToken();  // consume the ;
                }
            }

            token = synchronize(ROUTINE_START_SET);
            tokenType = token.getType();
        }
        */
        

        return null;
    }
}

    
    /*
    public void parse(Token token)
        throws Exception
    {
        // will need to check if it's a variable or a function (LATER)...
    	
    	// Current token should be INT/FLOAT/CHAR
        VariableDeclarationsParser variableDeclarationsParser =
            new VariableDeclarationsParser(this);
        variableDeclarationsParser.setDefinition(VARIABLE);
        
        // Parse all variable declarations
        while (token.getType() != LEFT_BRACE) {
        	variableDeclarationsParser.parse(token);
            token = synchronize(VAR_SET);
        }
        
        token = synchronize(ROUTINE_START_SET);
    }
}
*/
    