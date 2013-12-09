package wci.frontend.c;

public enum CErrorCode {
	ALREADY_FORWARDED("Already forwarded"),
	IDENTIFIER_REDEFINED("Identifier redefined"),
	IDENTIFIER_UNDEFINED("Identifier undefined"),
	INCOMPATIBLE_TYPES("Incompatible types"),
	INVALID_CHARACTER("Invalid character"),
	INVALID_CONSTANT("Invalid constant"),
	INVALID_FIELD("Invalid field"),
	INVALID_IDENTIFIER_USAGE("Invalid identifier usage"),
	INVALID_NUMBER("Invalid number"),
	INVALID_SUBRANGE_TYPE("Invalid subrange type"),
	INVALID_PARM("Invalid parameter"),
	INVALID_TYPE("Invalid type"),
	MIN_GT_MAX("Min greater than max"),
	MISSING_COMMA("Missing comma"),
	MISSING_IDENTIFIER("Missing identifier"),
	MISSING_LEFT_BRACE("Missing {"),
	MISSING_LEFT_BRACKET("Missing ["),
	MISSING_LEFT_PAREN("Missing ("),
	MISSING_MAIN("Missing main()"),
	MISSING_RIGHT_BRACE("Missing }"),
	MISSING_RIGHT_BRACKET("Missing ]"),
	MISSING_RIGHT_PAREN("Missing )"),
	MISSING_SEMICOLON("Missing ;"),
	MISSING_SINGLE_EQUALS("Missing ="),
	NOT_CONSTANT_IDENTIFIER("Not a constant identifier"),
	NOT_TYPE_IDENTIFIER("Not a type identifier"),
	RANGE_INTEGER("Integer literal out of range"),
	RANGE_REAL("Real literal out of range"),
	TOO_MANY_SUBSCRIPTS("Too many subscripts"),
	UNEXPECTED_EOL("Unexpected end of line"),
	UNEXPECTED_EOF("Unexpected end of file"),
	UNEXPECTED_TOKEN("Unexpected token"),
	WRONG_NUMBER_OF_PARMS("Wrong number of parameters"),

	// fatal errors
	IO_ERROR(-101, "Object I/O error"),
	TOO_MANY_ERRORS(-102, "Too many syntax errors");

	private int status; // exit status
	private String message; // error message

	CErrorCode(String message) {
		this.status = 0;
		this.message = message;
	}

	CErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public String toString() {
		return message;
	}
	
}
