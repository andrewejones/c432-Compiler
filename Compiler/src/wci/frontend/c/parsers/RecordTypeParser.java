package wci.frontend.c.parsers;

import java.util.EnumSet;
import java.util.ArrayList;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;

import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.RECORD;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;

/**
 * <h1>RecordTypeParser</h1>
 *
 * <p>Parse a C record type specification.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
class RecordTypeParser extends TypeSpecificationParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    protected RecordTypeParser(CParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for the END.
    private static final EnumSet<CTokenType> END_SET =
        DeclarationsParser.VAR_START_SET.clone();
    static {
        END_SET.add(END);
        END_SET.add(SEMICOLON);
    }

    /**
     * Parse a C record type specification.
     * @param token the current token.
     * @return the record type specification.
     * @throws Exception if an error occurred.
     */
    public TypeSpec parse(Token token)
        throws Exception
    {
        TypeSpec recordType = TypeFactory.createType(RECORD);
        token = nextToken();  // consume RECORD

        // Push a symbol table for the RECORD type specification.
        recordType.setAttribute(RECORD_SYMTAB, symTabStack.push());

        // Parse the field declarations.
        VariableDeclarationsParser variableDeclarationsParser =
            new VariableDeclarationsParser(this);
        variableDeclarationsParser.setDefinition(FIELD);
        variableDeclarationsParser.parse(token);

        // Pop off the record's symbol table.
        symTabStack.pop();

        // Synchronize at the END.
        token = synchronize(END_SET);

        // Look for the END.
        if (token.getType() == END) {
            token = nextToken();  // consume END
        }
        else {
            errorHandler.flag(token, MISSING_END, this);
        }

        return recordType;
    }
}
