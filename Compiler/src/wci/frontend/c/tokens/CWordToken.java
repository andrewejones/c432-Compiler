package wci.frontend.c.tokens;

import java.util.HashMap;

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
    
 // map for predefined lookup
 	private static HashMap<String, String> typemap = new HashMap<String, String>();
 	static {
 		typemap.put("int", "integer");
 		typemap.put("float", "real");
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
        
        if (typemap.containsKey(text) && RESERVED_WORDS.contains(typemap.get(text).toLowerCase()))
	        type = CTokenType.valueOf(typemap.get(text).toUpperCase());
        else
	        type = (RESERVED_WORDS.contains(text.toLowerCase())) ? CTokenType.valueOf(text.toUpperCase()) : IDENTIFIER;
        
    }
}
