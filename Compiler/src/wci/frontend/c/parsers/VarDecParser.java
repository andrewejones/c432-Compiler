package wci.frontend.c.parsers;

import java.util.ArrayList;
import java.util.EnumSet;
import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;

public class VarDecParser extends DeclarationsParser {
	private Definition definition; // how to define identifier

	public VarDecParser(CParserTD parent) {
		super(parent);
	}

	protected void setDefinition(Definition definition) {
		this.definition = definition;
	}

	// sync set for variable identifier
	static final EnumSet<CTokenType> IDENTIFIER_SET = EnumSet.of(IDENTIFIER, LEFT_BRACE, RIGHT_BRACE, SEMICOLON);
	// sync set to start sublist identifier
	static final EnumSet<CTokenType> IDENTIFIER_START_SET = EnumSet.of(IDENTIFIER, COMMA);
	// synch set to follow sublist identifier
	private static final EnumSet<CTokenType> IDENTIFIER_FOLLOW_SET = EnumSet.of(SEMICOLON);
	// synch set for ,
	private static final EnumSet<CTokenType> COMMA_SET = EnumSet.of(COMMA, IDENTIFIER, SEMICOLON);
	// synch set for start of next definition or declaration
	static final EnumSet<CTokenType> NEXT_START_SET = EnumSet.of(IDENTIFIER, SEMICOLON);

	public SymTabEntry parse(Token token, SymTabEntry parentId) throws Exception {
		while (token.getText().compareTo("int") == 0 || token.getText().compareTo("float") == 0 || token.getText().compareTo("char") == 0) {
			// parse identifier sublist and type spec
			parseIdentifierSublist(token, IDENTIFIER_FOLLOW_SET, COMMA_SET);
			token = currentToken();
			TokenType tokenType = token.getType();
			// look for semicolons
			if (tokenType == SEMICOLON)
				while (token.getType() == SEMICOLON)
					token = nextToken(); // consume ;
			// if at next definition or declaration then missing a semicolon
			else if (NEXT_START_SET.contains(tokenType))
				errorHandler.flag(token, MISSING_SEMICOLON, this);
			token = currentToken();
		}
		return null;
	}

	protected ArrayList<SymTabEntry> parseIdentifierSublist(Token token, EnumSet<CTokenType> followSet, EnumSet<CTokenType> commaSet) throws Exception {
		// parse type spec
		TypeSpec type = parseTypeSpec(token);
		token = currentToken();
		ArrayList<SymTabEntry> sublist = new ArrayList<SymTabEntry>();
		do {
			token = synchronize(IDENTIFIER_START_SET);
			SymTabEntry id = parseIdentifier(token);
			if (id != null)
				sublist.add(id);
			token = synchronize(commaSet);
			TokenType tokenType = token.getType();
			// look for ,
			if (tokenType == COMMA) {
				token = nextToken(); // consume ,
				if (followSet.contains(token.getType()))
					errorHandler.flag(token, MISSING_IDENTIFIER, this);
			} else if (IDENTIFIER_START_SET.contains(tokenType))
				errorHandler.flag(token, MISSING_COMMA, this);
		} while (!followSet.contains(token.getType()));
		// assign type specification to each identifier
		for (SymTabEntry variableId : sublist)
			variableId.setTypeSpec(type);
		return sublist;
	}

	protected SymTabEntry parseIdentifier(Token token) throws Exception {
		SymTabEntry id = null;
		if (token.getType() == IDENTIFIER) {
			String name = token.getText().toLowerCase();
			id = symTabStack.lookupLocal(name);
			// enter new identifier into symbol table
			if (id == null) {
				id = symTabStack.enterLocal(name);
				id.setDefinition(definition);
				id.appendLineNumber(token.getLineNumber());
			} else
				errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
			token = nextToken(); // consume identifier
		} else
			errorHandler.flag(token, MISSING_IDENTIFIER, this);
		return id;
	}

	protected TypeSpec parseTypeSpec(Token token) throws Exception {
		SymTabEntry id = symTabStack.lookup(token.getText().toLowerCase());
		id.appendLineNumber(token.getLineNumber());
		token = nextToken(); // consume the identifier
		return id.getTypeSpec();
	}
	
}
