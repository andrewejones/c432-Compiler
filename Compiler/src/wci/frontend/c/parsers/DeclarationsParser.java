package wci.frontend.c.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.Predefined;
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

    static final EnumSet<CTokenType> VAR_SET =
            EnumSet.of(LEFT_BRACE, IDENTIFIER);

    static final EnumSet<CTokenType> ROUTINE_START_SET =
            EnumSet.of(LEFT_BRACE);

    /**
     * Parse declarations.
     * To be overridden by the specialized declarations parser subclasses.
     * @param token the initial token.
     * @throws Exception if an error occurred.
     */
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
