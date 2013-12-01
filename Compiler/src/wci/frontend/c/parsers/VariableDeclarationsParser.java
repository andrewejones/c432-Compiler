package wci.frontend.c.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;

/**
 * <h1>VariableDeclarationsParser</h1>
 *
 * <p>Parse C variable declarations.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class VariableDeclarationsParser extends DeclarationsParser
{
    private Definition definition;  // how to define the identifier

    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public VariableDeclarationsParser(CParserTD parent)
    {
        super(parent);
    }

    /**
     * Setter.
     * @param definition the definition to set.
     */
    protected void setDefinition(Definition definition)
    {
        this.definition = definition;
    }

    // Synchronization set for a variable identifier.
    static final EnumSet<CTokenType> IDENTIFIER_SET =
        EnumSet.of(IDENTIFIER, LEFT_BRACE, RIGHT_BRACE, SEMICOLON);

    // Synchronization set for the start of the next definition or declaration.
    static final EnumSet<CTokenType> NEXT_START_SET =
        DeclarationsParser.ROUTINE_START_SET.clone();
    static {
        NEXT_START_SET.add(IDENTIFIER);
        NEXT_START_SET.add(SEMICOLON);
    }

    /**
     * Parse variable declarations.
     * @param token the initial token.
     * @throws Exception if an error occurred.
     */
    public void parse(Token token)
        throws Exception
    {
        token = synchronize(IDENTIFIER_SET);
	   
        // Parse the type specification.
        TypeSpec type = parseTypeSpec(token);
        
        // Parse the identifier sublist and its type specification.
        parseIdentifierSublist(token, type);
        
        token = currentToken();
        TokenType tokenType = token.getType();

        // Look for semicolon after definition.
        if (tokenType == SEMICOLON) {
                token = nextToken();  // consume the ;
        }

    }

    // Synchronization set to start a sublist identifier.
    static final EnumSet<CTokenType> IDENTIFIER_START_SET =
        EnumSet.of(IDENTIFIER, COMMA);

    // Synchronization set to follow a sublist identifier.
    private static final EnumSet<CTokenType> IDENTIFIER_FOLLOW_SET =
        EnumSet.of(SEMICOLON, LEFT_BRACE);

    // Synchronization set for the , token.
    private static final EnumSet<CTokenType> COMMA_SET =
        EnumSet.of(COMMA, IDENTIFIER, SEMICOLON);

    /**
     * Parse a sublist of identifiers and their type specification.
     * @param token the current token.
     * @return the sublist of identifiers in a declaration.
     * @throws Exception if an error occurred.
     */
    protected ArrayList<SymTabEntry> parseIdentifierSublist(Token token, TypeSpec type)
        throws Exception
    {
        ArrayList<SymTabEntry> sublist = new ArrayList<SymTabEntry>();

        do {
            token = synchronize(IDENTIFIER_START_SET);
            SymTabEntry id = parseIdentifier(token);

            if (id != null) {
                sublist.add(id);
            }

            token = synchronize(COMMA_SET);
            TokenType tokenType = token.getType();

            // Look for the comma.
            if (tokenType == COMMA) {
                token = nextToken();  // consume the comma

                if (IDENTIFIER_FOLLOW_SET.contains(token.getType())) {
                    errorHandler.flag(token, MISSING_IDENTIFIER, this);
                }
            }
            else if (IDENTIFIER_START_SET.contains(tokenType)) {
                errorHandler.flag(token, MISSING_COMMA, this);
            }
        } while (!IDENTIFIER_FOLLOW_SET.contains(token.getType()));

        // Assign the type specification to each identifier in the list.
        for (SymTabEntry variableId : sublist) {
            variableId.setTypeSpec(type);
        }

        return sublist;
    }

    /**
     * Parse an identifier.
     * @param token the current token.
     * @return the symbol table entry of the identifier.
     * @throws Exception if an error occurred.
     */
    private SymTabEntry parseIdentifier(Token token)
        throws Exception
    {
        SymTabEntry id = null;

        if (token.getType() == IDENTIFIER) {
            String name = token.getText().toLowerCase();
            id = symTabStack.lookupLocal(name);

            // Enter a new identifier into the symbol table.
            if (id == null) {
                id = symTabStack.enterLocal(name);
                id.setDefinition(definition);
                id.appendLineNumber(token.getLineNumber());
            }
            else {
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
            }

            token = nextToken();   // consume the identifier token
        }
        else {
            errorHandler.flag(token, MISSING_IDENTIFIER, this);
        }

        return id;
    }

    /**
     * Parse the type specification.
     * @param token the current token.
     * @return the type specification.
     * @throws Exception if an error occurs.
     */
    protected TypeSpec parseTypeSpec(Token token)
        throws Exception
    {
        // Parse the type specification.
        TypeSpecificationParser typeSpecificationParser =
            new TypeSpecificationParser(this);
        TypeSpec type = typeSpecificationParser.parse(token);

        return type;
    }
}
