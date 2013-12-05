package wci.frontend.c.parsers;

import java.util.EnumSet;
import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

public class StatementParser extends CParserTD {

	public StatementParser(CParserTD parent) {
		super(parent);
	}

	// sync set for start of statement
	protected static final EnumSet<CTokenType> STMT_START_SET = EnumSet.of(LEFT_BRACE, CTokenType.IF, WHILE, IDENTIFIER, SEMICOLON, CTokenType.RETURN);
	// sync set for end of statement
	protected static final EnumSet<CTokenType> STMT_FOLLOW_SET = EnumSet.of(SEMICOLON, RIGHT_BRACE);

	public ICodeNode parse(Token token) throws Exception {
		ICodeNode statementNode = null;
		switch ((CTokenType) token.getType()) {
			case LEFT_BRACE: {
				Compound compoundParser = new Compound(this);
				statementNode = compoundParser.parse(token);
				break;
			}
			case IDENTIFIER: {
				String name = token.getText().toLowerCase();
				SymTabEntry id = symTabStack.lookup(name);
				Definition idDefn = id != null ? id.getDefinition() : UNDEFINED;
				// assignment or procedure call
				switch ((DefinitionImpl) idDefn) {
					case VARIABLE:
					case VALUE_PARM:
					case UNDEFINED: {
						Assignment assignmentParser = new Assignment(this);
						statementNode = assignmentParser.parse(token);
						break;
					}
					case FUNCTION: {
						Assignment assignmentParser = new Assignment(this);
						statementNode = assignmentParser.parseFunctionNameAssignment(token);
						break;
					}
					case PROCEDURE: {
						CallParser callParser = new CallParser(this);
						statementNode = callParser.parse(token);
						break;
					}
					default: {
						errorHandler.flag(token, UNEXPECTED_TOKEN, this);
						token = nextToken(); // consume identifier
					}
				}
				break;
			}
			case WHILE: {
				WhileStatementParser whileParser = new WhileStatementParser(this);
				statementNode = whileParser.parse(token);
				break;
			}
			case IF: {
				IfStatementParser ifParser = new IfStatementParser(this);
				statementNode = ifParser.parse(token);
				break;
			}
			case RETURN: {
				// need to know function name here!!!
				// if function type is int/float/char, set function identifier value equal to expression.
				// whether void/char/int, float, skip to end of function (pop from stack...?)
				
				
				
				
				//errorHandler.flag(token, UNEXPECTED_TOKEN, this);
				token = nextToken();
				token = nextToken();
				
				symTabStack.pop();
				
				
				
				break;
				
			}
			default: {
				statementNode = ICodeFactory.createICodeNode(NO_OP);
				break;
			}
		}
		// set current line number as attribute
		setLineNumber(statementNode, token);
		return statementNode;
	}

	protected void setLineNumber(ICodeNode node, Token token) {
		if (node != null)
			node.setAttribute(LINE, token.getLineNumber());
	}

	protected void parseList(Token token, ICodeNode parentNode, CTokenType terminator, CErrorCode errorCode) throws Exception {
		// sync set for terminator
		EnumSet<CTokenType> terminatorSet = STMT_START_SET.clone();
		terminatorSet.add(terminator);
		// loop to parse statements until the } or EOF
		while (!(token instanceof EofToken) && (token.getType() != terminator)) {
			
			
			
			// parse statement; parent node adopts statement node
			ICodeNode statementNode = parse(token);
			parentNode.addChild(statementNode);
			// sync at next statement or terminator
			token = synchronize(terminatorSet);
			
			// look for semicolon between statements
			if (token.getType() == SEMICOLON)
				token = nextToken(); // consume ;
		}
		// look for terminator
		if (token.getType() == terminator)
			token = nextToken(); // consume terminator
		else
			errorHandler.flag(token, errorCode, this);
	}
	
}
