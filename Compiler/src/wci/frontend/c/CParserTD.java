package wci.frontend.c;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.parsers.*;
import wci.intermediate.*;
import wci.message.*;

import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.message.MessageType.PARSER_SUMMARY;

/**
 * <h1>CParserTD</h1>
 *
 * <p>The top-down C parser.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CParserTD extends Parser
{
    protected static CErrorHandler errorHandler = new CErrorHandler();

    /**
     * Constructor.
     * @param scanner the scanner to be used with this parser.
     */
    public CParserTD(Scanner scanner)
    {
        super(scanner);
    }

    /**
     * Constructor for subclasses.
     * @param parent the parent parser.
     */
    public CParserTD(CParserTD parent)
    {
        super(parent.getScanner());
    }

    /**
     * Getter.
     * @return the error handler.
     */
    public CErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    /**
     * Parse a C source program and generate the symbol table
     * and the intermediate code.
     * @throws Exception if an error occurred.
     */
    public void parse()
        throws Exception
    {
        long startTime = System.currentTimeMillis();
        iCode = ICodeFactory.createICode();

        try {
            Token token = nextToken();
            ICodeNode rootNode = null;

            // Look for the LEFT_BRACE token to parse a compound statement.
            if (token.getType() == LEFT_BRACE) {
                StatementParser statementParser = new StatementParser(this);
                rootNode = statementParser.parse(token);
                token = currentToken();
            }
            else {
                errorHandler.flag(token, UNEXPECTED_TOKEN, this);
            }

            token = currentToken();

            // Set the parse tree root node.
            if (rootNode != null) {
                iCode.setRoot(rootNode);
            }

            // Send the parser summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
            sendMessage(new Message(PARSER_SUMMARY,
                                    new Number[] {token.getLineNumber(),
                                                  getErrorCount(),
                                                  elapsedTime}));
        }
        catch (java.io.IOException ex) {
            errorHandler.abortTranslation(IO_ERROR, this);
        }
    }

    /**
     * Return the number of syntax errors found by the parser.
     * @return the error count.
     */
    public int getErrorCount()
    {
        return errorHandler.getErrorCount();
    }

    /**
     * Synchronize the parser.
     * @param syncSet the set of token types for synchronizing the parser.
     * @return the token where the parser has synchronized.
     * @throws Exception if an error occurred.
     */
    public Token synchronize(EnumSet syncSet)
        throws Exception
    {
        Token token = currentToken();

        // If the current token is not in the synchronization set,
        // then it is unexpected and the parser must recover.
        if (!syncSet.contains(token.getType())) {

            // Flag the unexpected token.
            errorHandler.flag(token, UNEXPECTED_TOKEN, this);

            // Recover by skipping tokens that are not
            // in the synchronization set.
            do {
                token = nextToken();
            } while (!(token instanceof EofToken) &&
                     !syncSet.contains(token.getType()));
       }

       return token;
    }
}
