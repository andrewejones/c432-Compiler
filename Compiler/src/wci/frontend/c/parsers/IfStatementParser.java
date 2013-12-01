package wci.frontend.c.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;

/**
 * <h1>IfStatementParser</h1>
 *
 * <p>Parse a C IF statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class IfStatementParser extends StatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public IfStatementParser(CParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for PAREN.
    private static final EnumSet<CTokenType> PAREN_SET =
        StatementParser.STMT_START_SET.clone();
    static {
        PAREN_SET.add(LEFT_PAREN);
        PAREN_SET.add(RIGHT_PAREN);
    }

    /**
     * Parse an IF statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        token = nextToken();  // consume the IF

        // Create an IF node.
        ICodeNode ifNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);

        // Synchronize at LEFT_PAREN
        token = synchronize(PAREN_SET);
        if (token.getType() == LEFT_PAREN) {
            token = nextToken();  // consume the LEFT PAREN
        }
        else {
            errorHandler.flag(token, MISSING_LEFT_PAREN, this);
        }
        
        // Parse the expression.
        // The IF node adopts the expression subtree as its first child.
        ExpressionParser expressionParser = new ExpressionParser(this);
        ifNode.addChild(expressionParser.parse(token));

        // Synchronize at RIGHT_PAREN
        token = synchronize(PAREN_SET);
        if (token.getType() == RIGHT_PAREN) {
            token = nextToken();  // consume the RIGHT_PAREN
        }
        else {
            errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
        }

        // Parse the statement.
        // The IF node adopts the statement subtree as its second child.
        StatementParser statementParser = new StatementParser(this);
        ICodeNode exprNode = expressionParser.parse(token);
        ifNode.addChild(exprNode);

        // Type check: The expression type must be boolean.
        TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                             : Predefined.undefinedType;
        if (!TypeChecker.isBoolean(exprType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }
        token = currentToken();

        // Look for an ELSE.
        if (token.getType() == ELSE) {
            token = nextToken();  // consume the ELSE

            // Parse the ELSE statement.
            // The IF node adopts the statement subtree as its third child.
            ifNode.addChild(statementParser.parse(token));
        }

        return ifNode;
    }
}
