package wci.frontend.c.tokens;

import wci.frontend.*;
import wci.frontend.c.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.frontend.c.CTokenType.*;

/**
 * <h1>CSpecialSymbolToken</h1>
 *
 * <p> C special symbol tokens.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CSpecialSymbolToken extends CToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public CSpecialSymbolToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a C special symbol token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        char currentChar = currentChar();

        text = Character.toString(currentChar);
        type = null;

        switch (currentChar) {

            // Single-character special symbols.
            case '+':  case '-':  case '*':  case '/': case '%':
            case ',': case ';':  case '\'':
            case '(':  case ')': case '[':  case ']':  case '{':  case '}': {
                nextChar();  // consume character
                break;
            }

            // = or ==
            case '=': {
                currentChar = nextChar();  // consume '=';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }
            
            // ! or !=
            case '!': {
                currentChar = nextChar();  // consume '=';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }

            // < or <=
            case '<': {
                currentChar = nextChar();  // consume '<';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }

            // > or >=
            case '>': {
                currentChar = nextChar();  // consume '>';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }

            default: {
                nextChar();  // consume bad character
                type = ERROR;
                value = INVALID_CHARACTER;
            }
        }

        // Set the type if it wasn't an error.
        if (type == null) {
            type = SPECIAL_SYMBOLS.get(text);
        }
    }
}
