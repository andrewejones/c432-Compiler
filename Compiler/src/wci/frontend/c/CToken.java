package wci.frontend.c;

import wci.frontend.*;

/**
 * <h1>CToken</h1>
 *
 * <p>Base class for C token classes.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CToken extends Token
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    protected CToken(Source source)
        throws Exception
    {
        super(source);
    }
}
