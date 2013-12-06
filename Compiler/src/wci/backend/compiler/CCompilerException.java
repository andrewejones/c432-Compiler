package wci.backend.compiler;

/**
 * <h1>CCompilerException</h1>
 *
 * <p>Error during the C compiler's code generation.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CCompilerException extends Exception
{
    public CCompilerException(String message)
    {
        super(message);
    }
}
