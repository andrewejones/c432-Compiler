package wci.frontend.c;

/**
 * <h1>CErrorCode</h1>
 *
 * <p>C translation error codes.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public enum CErrorCode
{
    MISSING_EOF("Missing EOF"),
    MISSING_RIGHT_BRACE("Missing }"),
    MISSING_LEFT_PAREN("Missing )"),
    MISSING_RIGHT_PAREN("Missing )"),
    MISSING_SEMICOLON("Missing ;"),
    MISSING_SINGLE_EQUALS("Missing ="),
    IDENTIFIER_UNDEFINED("Identifier undefined"),
    INVALID_CHARACTER("Invalid character"),
    INVALID_NUMBER("Invalid number"),
    RANGE_INTEGER("Integer literal out of range"),
    RANGE_REAL("Real literal out of range"),
    UNEXPECTED_EOL("Unexpected end of line"),
    UNEXPECTED_EOF("Unexpected end of file"),
    UNEXPECTED_TOKEN("Unexpected token"),

    // Fatal errors.
    IO_ERROR(-101, "Object I/O error"),
    TOO_MANY_ERRORS(-102, "Too many syntax errors");

    private int status;      // exit status
    private String message;  // error message

    /**
     * Constructor.
     * @param message the error message.
     */
    CErrorCode(String message)
    {
        this.status = 0;
        this.message = message;
    }

    /**
     * Constructor.
     * @param status the exit status.
     * @param message the error message.
     */
    CErrorCode(int status, String message)
    {
        this.status = status;
        this.message = message;
    }

    /**
     * Getter.
     * @return the exit status.
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * @return the message.
     */
    public String toString()
    {
        return message;
    }
}
