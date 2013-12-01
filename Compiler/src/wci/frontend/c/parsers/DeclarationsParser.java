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

    static final EnumSet<CTokenType> DECLARATION_START_SET =
        EnumSet.of(CONST, TYPE, VAR, PROCEDURE, FUNCTION, BEGIN);

    static final EnumSet<CTokenType> TYPE_START_SET =
        DECLARATION_START_SET.clone();
    static {
        TYPE_START_SET.remove(CONST);
    }

    static final EnumSet<CTokenType> VAR_START_SET =
        TYPE_START_SET.clone();
    static {
        VAR_START_SET.remove(TYPE);
    }

    static final EnumSet<CTokenType> ROUTINE_START_SET =
        VAR_START_SET.clone();
    static {
        ROUTINE_START_SET.remove(VAR);
    }

    /**
     * Parse declarations.
     * To be overridden by the specialized declarations parser subclasses.
     * @param token the initial token.
     * @throws Exception if an error occurred.
     */
    public void parse(Token token)
        throws Exception
    {
        token = synchronize(DECLARATION_START_SET);

        if (token.getType() == CONST) {
            token = nextToken();  // consume CONST

            ConstantDefinitionsParser constantDefinitionsParser =
                new ConstantDefinitionsParser(this);
            constantDefinitionsParser.parse(token);
        }

        token = synchronize(TYPE_START_SET);

        if (token.getType() == TYPE) {
            token = nextToken();  // consume TYPE

            TypeDefinitionsParser typeDefinitionsParser =
                new TypeDefinitionsParser(this);
            typeDefinitionsParser.parse(token);
        }

        token = synchronize(VAR_START_SET);

        if (token.getType() == VAR) {
            token = nextToken();  // consume VAR

            VariableDeclarationsParser variableDeclarationsParser =
                new VariableDeclarationsParser(this);
            variableDeclarationsParser.setDefinition(VARIABLE);
            variableDeclarationsParser.parse(token);
        }

        token = synchronize(ROUTINE_START_SET);
    }
}
