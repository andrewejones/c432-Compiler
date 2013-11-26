package wci.frontend.c;

import wci.frontend.*;
import static wci.frontend.Source.EOF;

/**
 * <h1>CScanner</h1>
 *
 * <p>The C scanner.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CScanner extends Scanner
{
    /**
     * Constructor
     * @param source the source to be used with this scanner.
     */
    public CScanner(Source source)
    {
        super(source);
    }

    /**
     * Extract and return the next C token from the source.
     * @return the next token.
     * @throws Exception if an error occurred.
     */
    protected Token extractToken()
        throws Exception
    {
        Token token;
        char currentChar = currentChar();

        // Construct the next token.  The current character determines the
        // token type.
        if (currentChar == EOF) {
            token = new EofToken(source);
        }
        else {
            token = new Token(source);
        }

        return token;
    }
}
