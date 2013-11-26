package wci.frontend.c.tokens;

import wci.frontend.*;
import wci.frontend.c.*;
import static wci.frontend.c.CTokenType.*;

/**
 * <h1>CErrorToken</h1>
 *
 * <p>C error token.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CErrorToken extends CToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch subsequent characters.
     * @param errorCode the error code.
     * @param tokenText the text of the erroneous token.
     * @throws Exception if an error occurred.
     */
    public CErrorToken(Source source, CErrorCode errorCode,
                            String tokenText)
        throws Exception
    {
        super(source);

        this.text = tokenText;
        this.type = ERROR;
        this.value = errorCode;
    }

    /**
     * Do nothing.  Do not consume any source characters.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
    }
}
