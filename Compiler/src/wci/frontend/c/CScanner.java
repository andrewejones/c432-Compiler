package wci.frontend.c;

import wci.frontend.*;
import wci.frontend.c.tokens.*;
import static wci.frontend.Source.EOF;
import static wci.frontend.c.CErrorCode.*;
import static wci.frontend.c.CTokenType.*;

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
        skipWhiteSpace();

        Token token;
        char currentChar = currentChar();

        // Construct the next token.  The current character determines the
        // token type.
        if (currentChar == EOF) {
            token = new EofToken(source);
        }
        else if (Character.isLetter(currentChar)) {
            token = new CWordToken(source);
        }
        else if (Character.isDigit(currentChar)) {
            token = new CNumberToken(source);
        }
        else if (currentChar == '\'') {
            token = new CCharToken(source);
        }
        else if (CTokenType.SPECIAL_SYMBOLS
                 .containsKey(Character.toString(currentChar))) {
            token = new CSpecialSymbolToken(source);
        }
        else {
            token = new CErrorToken(source, INVALID_CHARACTER,
                                         Character.toString(currentChar));
            nextChar();  // consume character
        }

        return token;
    }

    /**
     * Skip whitespace characters by consuming them.  A comment is whitespace.
     * @throws Exception if an error occurred.
     */
    private void skipWhiteSpace()
        throws Exception
    {
        char currentChar = currentChar();
        char peekChar = peekChar();
		// check for white space and comments
		while (Character.isWhitespace(currentChar) || (currentChar == '/' && peekChar == '/') || (currentChar == '/' && peekChar == '*')) {
			// is it a // comment?
			if (currentChar == '/' && peekChar == '/')
				do
					currentChar = nextChar(); // consume comment characters
				while ((currentChar != '\n') && (currentChar != EOF));
			// is it a /* comment?
			else if (currentChar == '/' && peekChar == '*') {
				currentChar = nextChar(); // consume first / character
				do {
					currentChar = nextChar(); // consume comment characters
					peekChar = peekChar(); // update peek
				} while (!(currentChar == '*' && peekChar == '/') && (currentChar != EOF));
				if (currentChar == '*') {
					currentChar = nextChar(); // consume the '*'
					currentChar = nextChar(); // consume the '/'
				}
			}
			// not a comment
			else
				currentChar = nextChar(); // consume whitespace character
			if (currentChar != EOF)
				peekChar = peekChar();
		}
    }
}
