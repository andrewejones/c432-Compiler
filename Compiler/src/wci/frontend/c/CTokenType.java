package wci.frontend.c;

import java.util.Hashtable;
import java.util.HashSet;
import wci.frontend.TokenType;

public enum CTokenType implements TokenType
{
	// reserved words
	ELSE, FUNCTION, IF, PROCEDURE, PROGRAM, RETURN, CHAR, FLOAT, INT, VOID, WHILE,

	// special symbols
	PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), MOD("%"),
	AND("&&"), OR("||"),
	COMMA(","), COLON(":"), SEMICOLON(";"), SINGLE_QUOTE("'"), DOUBLE_QUOTE("\""),
	LEFT_PAREN("("), RIGHT_PAREN(")"), LEFT_BRACKET("["), RIGHT_BRACKET("]"), LEFT_BRACE("{"), RIGHT_BRACE("}"),
	SINGLE_EQUALS("="), DOUBLE_EQUALS("=="), NOT("!"), NOT_EQUALS("!="), LESS_THAN("<"), LESS_EQUALS("<="),  GREATER_THAN(">"), GREATER_EQUALS(">="),

	IDENTIFIER,
	ERROR, END_OF_FILE;

	private static final int FIRST_RESERVED_INDEX = ELSE.ordinal();
	private static final int LAST_RESERVED_INDEX = WHILE.ordinal();
	private static final int FIRST_SPECIAL_INDEX = PLUS.ordinal();
	private static final int LAST_SPECIAL_INDEX = GREATER_EQUALS.ordinal();
	private String text; // token text

	CTokenType() {
		this.text = this.toString().toLowerCase();
	}

	CTokenType(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	// set of lower case reserved words
	public static HashSet<String> RESERVED_WORDS = new HashSet<String>();
	static {
		CTokenType values[] = CTokenType.values();
		for (int i = FIRST_RESERVED_INDEX; i <= LAST_RESERVED_INDEX; ++i)
			RESERVED_WORDS.add(values[i].getText().toLowerCase());
	}

	// hash table of special symbols; symbol text is key to token type
	public static Hashtable<String, CTokenType> SPECIAL_SYMBOLS = new Hashtable<String, CTokenType>();
	static {
		CTokenType values[] = CTokenType.values();
		for (int i = FIRST_SPECIAL_INDEX; i <= LAST_SPECIAL_INDEX; ++i)
			SPECIAL_SYMBOLS.put(values[i].getText(), values[i]);
	}

}
