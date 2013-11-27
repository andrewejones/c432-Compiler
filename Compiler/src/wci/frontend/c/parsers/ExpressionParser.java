package wci.frontend.c.parsers;

import java.util.EnumSet;
import java.util.HashMap;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;

import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CTokenType.NOT;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>ExpressionParser</h1>
 *
 * <p>Parse a C expression.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ExpressionParser extends StatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public ExpressionParser(CParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for starting an expression.
    static final EnumSet<CTokenType> EXPR_START_SET =
        EnumSet.of(PLUS, MINUS, IDENTIFIER, INT, FLOAT, CHAR,
                   CTokenType.NOT, LEFT_PAREN);

    /**
     * Parse an expression.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        return parseExpression(token);
    }

    // Set of relational operators.
    private static final EnumSet<CTokenType> REL_OPS =
        EnumSet.of(DOUBLE_EQUALS, NOT_EQUALS, LESS_THAN, LESS_EQUALS,
                   GREATER_THAN, GREATER_EQUALS);

    // Map relational operator tokens to node types.
    private static final HashMap<CTokenType, ICodeNodeType>
        REL_OPS_MAP = new HashMap<CTokenType, ICodeNodeType>();
    static {
        REL_OPS_MAP.put(DOUBLE_EQUALS, EQ);
        REL_OPS_MAP.put(NOT_EQUALS, NE);
        REL_OPS_MAP.put(LESS_THAN, LT);
        REL_OPS_MAP.put(LESS_EQUALS, LE);
        REL_OPS_MAP.put(GREATER_THAN, GT);
        REL_OPS_MAP.put(GREATER_EQUALS, GE);
    };

    /**
     * Parse an expression.
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseExpression(Token token)
        throws Exception
    {
        // Parse a simple expression and make the root of its tree
        // the root node.
        ICodeNode rootNode = parseSimpleExpression(token);

        token = currentToken();
        TokenType tokenType = token.getType();

        // Look for a relational operator.
        if (REL_OPS.contains(tokenType)) {

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = REL_OPS_MAP.get(tokenType);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            token = nextToken();  // consume the operator

            // Parse the second simple expression.  The operator node adopts
            // the simple expression's tree as its second child.
            opNode.addChild(parseSimpleExpression(token));

            // The operator node becomes the new root node.
            rootNode = opNode;
        }

        return rootNode;
    }

    // Set of additive operators.
    private static final EnumSet<CTokenType> ADD_OPS =
        EnumSet.of(PLUS, MINUS, CTokenType.OR);

    // Map additive operator tokens to node types.
    private static final HashMap<CTokenType, ICodeNodeTypeImpl>
        ADD_OPS_OPS_MAP = new HashMap<CTokenType, ICodeNodeTypeImpl>();
    static {
        ADD_OPS_OPS_MAP.put(PLUS, ADD);
        ADD_OPS_OPS_MAP.put(MINUS, SUBTRACT);
        ADD_OPS_OPS_MAP.put(CTokenType.OR, ICodeNodeTypeImpl.OR);
    };

    /**
     * Parse a simple expression.
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseSimpleExpression(Token token)
        throws Exception
    {
        TokenType signType = null;  // type of leading sign (if any)

        // Look for a leading + or - sign.
        TokenType tokenType = token.getType();
        if ((tokenType == PLUS) || (tokenType == MINUS)) {
            signType = tokenType;
            token = nextToken();  // consume the + or -
        }

        // Parse a term and make the root of its tree the root node.
        ICodeNode rootNode = parseTerm(token);

        // Was there a leading - sign?
        if (signType == MINUS) {

            // Create a NEGATE node and adopt the current tree
            // as its child. The NEGATE node becomes the new root node.
            ICodeNode negateNode = ICodeFactory.createICodeNode(NEGATE);
            negateNode.addChild(rootNode);
            rootNode = negateNode;
        }

        token = currentToken();
        tokenType = token.getType();

        // Loop over additive operators.
        while (ADD_OPS.contains(tokenType)) {

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = ADD_OPS_OPS_MAP.get(tokenType);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            token = nextToken();  // consume the operator

            // Parse another term.  The operator node adopts
            // the term's tree as its second child.
            opNode.addChild(parseTerm(token));

            // The operator node becomes the new root node.
            rootNode = opNode;

            token = currentToken();
            tokenType = token.getType();
        }

        return rootNode;
    }

    // Set of multiplicative operators.
    private static final EnumSet<CTokenType> MULT_OPS =
        EnumSet.of(STAR, SLASH, CTokenType.MOD, CTokenType.AND);

    // Map multiplicative operator tokens to node types.
    private static final HashMap<CTokenType, ICodeNodeType>
        MULT_OPS_OPS_MAP = new HashMap<CTokenType, ICodeNodeType>();
    static {
        MULT_OPS_OPS_MAP.put(STAR, MULTIPLY);
        MULT_OPS_OPS_MAP.put(SLASH, FLOAT_DIVIDE);
        MULT_OPS_OPS_MAP.put(SLASH, INTEGER_DIVIDE);
        MULT_OPS_OPS_MAP.put(CTokenType.MOD, ICodeNodeTypeImpl.MOD);
        MULT_OPS_OPS_MAP.put(CTokenType.AND, ICodeNodeTypeImpl.AND);
    };

    /**
     * Parse a term.
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseTerm(Token token)
        throws Exception
    {
        // Parse a factor and make its node the root node.
        ICodeNode rootNode = parseFactor(token);

        token = currentToken();
        TokenType tokenType = token.getType();

        // Loop over multiplicative operators.
        while (MULT_OPS.contains(tokenType)) {

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = MULT_OPS_OPS_MAP.get(tokenType);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            token = nextToken();  // consume the operator

            // Parse another factor.  The operator node adopts
            // the term's tree as its second child.
            opNode.addChild(parseFactor(token));

            // The operator node becomes the new root node.
            rootNode = opNode;

            token = currentToken();
            tokenType = token.getType();
        }

        return rootNode;
    }

    /**
     * Parse a factor.
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseFactor(Token token)
        throws Exception
    {
        TokenType tokenType = token.getType();
        ICodeNode rootNode = null;

        switch ((CTokenType) tokenType) {

            case IDENTIFIER: {
                // Look up the identifier in the symbol table stack.
                // Flag the identifier as undefined if it's not found.
                String name = token.getText().toLowerCase();
                SymTabEntry id = symTabStack.lookup(name);
                if (id == null) {
                    errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
                    id = symTabStack.enterLocal(name);
                }

                rootNode = ICodeFactory.createICodeNode(VARIABLE);
                rootNode.setAttribute(ID, id);
                id.appendLineNumber(token.getLineNumber());

                token = nextToken();  // consume the identifier
                break;
            }

            case INT: {
                // Create an INTEGER_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();  // consume the number
                break;
            }

            case FLOAT: {
                // Create an REAL_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(REAL_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();  // consume the number
                break;
            }

            case CHAR: {
                // Create a STRING_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(STRING_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();  // consume the string
                break;
            }

            case NOT: {
                token = nextToken();  // consume the NOT

                // Create a NOT node as the root node.
                rootNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);

                // Parse the factor.  The NOT node adopts the
                // factor node as its child.
                rootNode.addChild(parseFactor(token));

                break;
            }

            case LEFT_PAREN: {
                token = nextToken();      // consume the (

                // Parse an expression and make its node the root node.
                rootNode = parseExpression(token);

                // Look for the matching ) token.
                token = currentToken();
                if (token.getType() == RIGHT_PAREN) {
                    token = nextToken();  // consume the )
                }
                else {
                    errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
                }

                break;
            }

            default: {
                errorHandler.flag(token, UNEXPECTED_TOKEN, this);
                break;
            }
        }

        return rootNode;
    }
}
