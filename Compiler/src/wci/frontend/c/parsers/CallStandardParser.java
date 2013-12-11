package wci.frontend.c.parsers;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

public class CallStandardParser extends CallParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public CallStandardParser(CParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a call to a declared procedure or function.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        ICodeNode callNode = ICodeFactory.createICodeNode(CALL);
        SymTabEntry pfId = symTabStack.lookup(token.getText().toLowerCase());
        RoutineCode routineCode = (RoutineCode) pfId.getAttribute(ROUTINE_CODE);
        callNode.setAttribute(ID, pfId);

        token = nextToken(); // consume procedure or function identifier

        switch ((RoutineCodeImpl) routineCode) {
            case WRITE:
            case WRITELN: return parseWriteWriteln(token, callNode, pfId);

            default:      return null;  // should never get here
        }
    }

    /**
     * Parse a call to write or writeln.
     * @param token the current token.
     * @param callNode the CALL node.
     * @param pfId the symbol table entry of the standard routine name.
     * @return ICodeNode the CALL node.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseWriteWriteln(Token token, ICodeNode callNode,
                                        SymTabEntry pfId)
        throws Exception
    {
        // Parse any actual parameters.
        ICodeNode parmsNode = parseActualParameters(token, pfId,
                                                    false, false, true);
        callNode.addChild(parmsNode);

        // Write must have parameters.
        if ((pfId == Predefined.writeId) &&
            (callNode.getChildren().size() == 0))
        {
            errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
        }

        return callNode;
    }

}
