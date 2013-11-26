package wci.frontend.c;

import wci.frontend.*;
import wci.message.Message;
import static wci.frontend.c.CErrorCode.*;
import static wci.frontend.c.CTokenType.*;
import static wci.message.MessageType.*;

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
     * Parse a C source program and generate the symbol table
     * and the intermediate code.
     */
    public void parse()
        throws Exception
    {
        Token token;
        long startTime = System.currentTimeMillis();

        try {
            // Loop over each token until the end of file.
            while (!((token = nextToken()) instanceof EofToken)) {
                TokenType tokenType = token.getType();

                if (tokenType != ERROR) {

                    // Format each token.
                    sendMessage(new Message(TOKEN,
                                            new Object[] {token.getLineNumber(),
                                                          token.getPosition(),
                                                          tokenType,
                                                          token.getText(),
                                                          token.getValue()}));
                }
                else {
                    errorHandler.flag(token, (CErrorCode) token.getValue(),
                                      this);
                }

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
}
