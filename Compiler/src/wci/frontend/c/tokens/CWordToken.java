package wci.frontend.c.tokens;

import wci.frontend.*;
import wci.frontend.c.*;
import static wci.frontend.c.CTokenType.*;

/**
 * <h1>CWordToken</h1>
 *
 * <p> C word tokens (identifiers and reserved words).</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CWordToken extends CToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public CWordToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a C word token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        StringBuilder textBuffer = new StringBuilder();
        char currentChar = currentChar();

        // Get the word characters (letter or digit).  The scanner has
        // already determined that the first character is a letter.
        while (Character.isLetterOrDigit(currentChar)) {
            textBuffer.append(currentChar);
            currentChar = nextChar();  // consume character
        }

        text = textBuffer.toString();

        // Is it a reserved word or an identifier?
        type = (RESERVED_WORDS.contains(text.toLowerCase()))
               ? CTokenType.valueOf(text.toUpperCase())  // reserved word
               : IDENTIFIER;                                  // identifier
    }
}
