package wci.frontend.c.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;

/**
 * <h1>TypeSpecificationParser</h1>
 *
 * <p>Parse a C type specification.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
class TypeSpecificationParser extends CParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    protected TypeSpecificationParser(CParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for starting a type specification.
    static final EnumSet<CTokenType> TYPE_START_SET =
        SimpleTypeParser.SIMPLE_TYPE_START_SET.clone();
    static {
        TYPE_START_SET.add(SEMICOLON);
    }

    /**
     * Parse a C type specification.
     * @param token the current token.
     * @return the type specification.
     * @throws Exception if an error occurred.
     */
    public TypeSpec parse(Token token)
        throws Exception
    {
        // Synchronize at the start of a type specification.
        token = synchronize(TYPE_START_SET);

        switch ((CTokenType) token.getType()) {
            default: {
                SimpleTypeParser simpleTypeParser = new SimpleTypeParser(this);
                return simpleTypeParser.parse(token);
            }
        }
    }
}
