package wci.frontend.c.tokens;

import wci.frontend.*;
import wci.frontend.c.*;
import static wci.frontend.Source.EOL;
import static wci.frontend.Source.EOF;
import static wci.frontend.c.CErrorCode.*;
import static wci.frontend.c.CTokenType.*;

/**
 * <h1>CCharToken</h1>
 *
 * <p> C char tokens.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CStringToken extends CToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public CStringToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a C char token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        StringBuilder textBuffer = new StringBuilder();
        StringBuilder valueBuffer = new StringBuilder();

        char currentChar = nextChar();  // consume initial quote
        textBuffer.append('"');

        // Get character
        while ((currentChar != '"') && (currentChar != EOL) && (currentChar != EOF)) {
            textBuffer.append(currentChar);
            valueBuffer.append(currentChar);
            currentChar = nextChar();  // consume character
        }

        if (currentChar == '"') {
            nextChar();  // consume final quote
            textBuffer.append('"');

            type = CHAR;
            value = valueBuffer.toString();
        }
        else if (currentChar == EOL) {
            type = ERROR;
            value = UNEXPECTED_EOL;
        }
        else {
            type = ERROR;
            value = UNEXPECTED_EOF;
        }

        text = textBuffer.toString();
    }
}
