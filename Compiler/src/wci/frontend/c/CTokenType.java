package wci.frontend.c;

import java.util.Hashtable;
import java.util.HashSet;

import wci.frontend.TokenType;

/**
 * <h1>CTokenType</h1>
 *
 * <p>C token types.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public enum CTokenType implements TokenType
{
    // Reserved words.
    ELSE, IF, WHILE,

    // Special symbols.
    PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), MOD("%"),
    AND("&&"), OR("||"),
    DOT("."), COMMA(","), SEMICOLON(";"), SINGLE_QUOTE("'"),
    LEFT_PAREN("("), RIGHT_PAREN(")"), LEFT_BRACKET("["), RIGHT_BRACKET("]"), LEFT_BRACE("{"), RIGHT_BRACE("}"),
    SINGLE_EQUALS("="), DOUBLE_EQUALS("=="), NOT("!"), NOT_EQUALS("!="), LESS_THAN("<"), LESS_EQUALS("<="),  GREATER_THAN(">"), GREATER_EQUALS(">="),
    
    CHAR, IDENTIFIER, INT, FLOAT,
    ERROR, END_OF_FILE;

    private static final int FIRST_RESERVED_INDEX = ELSE.ordinal();
    private static final int LAST_RESERVED_INDEX  = WHILE.ordinal();

    private static final int FIRST_SPECIAL_INDEX = PLUS.ordinal();
    private static final int LAST_SPECIAL_INDEX  = GREATER_EQUALS.ordinal();

    private String text;  // token text

    /**
     * Constructor.
     */
    CTokenType()
    {
        this.text = this.toString().toLowerCase();
    }

    /**
     * Constructor.
     * @param text the token text.
     */
    CTokenType(String text)
    {
        this.text = text;
    }

    /**
     * Getter.
     * @return the token text.
     */
    public String getText()
    {
        return text;
    }

    // Set of lower-cased C reserved word text strings.
    public static HashSet<String> RESERVED_WORDS = new HashSet<String>();
    static {
        CTokenType values[] = CTokenType.values();
        for (int i = FIRST_RESERVED_INDEX; i <= LAST_RESERVED_INDEX; ++i) {
            RESERVED_WORDS.add(values[i].getText().toLowerCase());
        }
    }

    // Hash table of C special symbols.  Each special symbol's text
    // is the key to its C token type.
    public static Hashtable<String, CTokenType> SPECIAL_SYMBOLS =
        new Hashtable<String, CTokenType>();
    static {
        CTokenType values[] = CTokenType.values();
        for (int i = FIRST_SPECIAL_INDEX; i <= LAST_SPECIAL_INDEX; ++i) {
            SPECIAL_SYMBOLS.put(values[i].getText(), values[i]);
        }
    }
}
