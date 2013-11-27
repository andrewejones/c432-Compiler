package wci.frontend.c.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>StatementParser</h1>
 *
 * <p>Parse a C statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class StatementParser extends CParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public StatementParser(CParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for starting a statement.
    protected static final EnumSet<CTokenType> STMT_START_SET =
        EnumSet.of(LEFT_BRACE, CTokenType.IF, WHILE,
                   IDENTIFIER, SEMICOLON);

    // Synchronization set for following a statement.
    protected static final EnumSet<CTokenType> STMT_FOLLOW_SET =
        EnumSet.of(SEMICOLON, RIGHT_BRACE, ELSE);

    /**
     * Parse a statement.
     * To be overridden by the specialized statement parser subclasses.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        ICodeNode statementNode = null;

        switch ((CTokenType) token.getType()) {

            case LEFT_BRACE: {
                CompoundStatementParser compoundParser =
                    new CompoundStatementParser(this);
                statementNode = compoundParser.parse(token);
                break;
            }

            // An assignment statement begins with a variable's identifier.
            case IDENTIFIER: {
                AssignmentStatementParser assignmentParser =
                    new AssignmentStatementParser(this);
                statementNode = assignmentParser.parse(token);
                break;
            }

            case WHILE: {
                WhileStatementParser whileParser =
                    new WhileStatementParser(this);
                statementNode = whileParser.parse(token);
                break;
            }

            case IF: {
                IfStatementParser ifParser = new IfStatementParser(this);
                statementNode = ifParser.parse(token);
                break;
            }

            default: {
                statementNode = ICodeFactory.createICodeNode(NO_OP);
                break;
            }
        }

        // Set the current line number as an attribute.
        setLineNumber(statementNode, token);

        return statementNode;
    }

    /**
     * Set the current line number as a statement node attribute.
     * @param node ICodeNode
     * @param token Token
     */
    protected void setLineNumber(ICodeNode node, Token token)
    {
        if (node != null) {
            node.setAttribute(LINE, token.getLineNumber());
        }
    }

    /**
     * Parse a statement list.
     * @param token the current token.
     * @param parentNode the parent node of the statement list.
     * @param terminator the token type of the node that terminates the list.
     * @param errorCode the error code if the terminator token is missing.
     * @throws Exception if an error occurred.
     */
    protected void parseList(Token token, ICodeNode parentNode,
                             CTokenType terminator,
                             CErrorCode errorCode)
        throws Exception
    {
        // Synchronization set for the terminator.
        EnumSet<CTokenType> terminatorSet = STMT_START_SET.clone();
        terminatorSet.add(terminator);

        // Loop to parse each statement until the RIGHT_BRACE token
        // or the end of the source file.
        while (!(token instanceof EofToken) &&
               (token.getType() != terminator)) {

            // Parse a statement.  The parent node adopts the statement node.
            ICodeNode statementNode = parse(token);
            parentNode.addChild(statementNode);

            token = currentToken();
            TokenType tokenType = token.getType();

            // Look for the semicolon between statements.
            if (tokenType == SEMICOLON) {
                token = nextToken();  // consume the ;
            }

            // If at the start of the next statement, then missing a semicolon.
            else if (STMT_START_SET.contains(tokenType)) {
                errorHandler.flag(token, MISSING_SEMICOLON, this);
            }

            // Synchronize at the start of the next statement
            // or at the terminator.
            token = synchronize(terminatorSet);
        }

        // Look for the terminator token.
        if (token.getType() == terminator) {
            token = nextToken();  // consume the terminator token
        }
        else {
            errorHandler.flag(token, errorCode, this);
        }
    }
}
