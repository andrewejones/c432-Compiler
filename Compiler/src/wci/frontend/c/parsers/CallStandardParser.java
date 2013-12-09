package wci.frontend.c.parsers;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.icodeimpl.*;

import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.*;

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
            case READ:
            case READLN:  return parseReadReadln(token, callNode, pfId);

            case WRITE:
            case WRITELN: return parseWriteWriteln(token, callNode, pfId);

            default:      return null;  // should never get here
        }
    }

    /**
     * Parse a call to read or readln.
     * @param token the current token.
     * @param callNode the CALL node.
     * @param pfId the symbol table entry of the standard routine name.
     * @return ICodeNode the CALL node.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseReadReadln(Token token, ICodeNode callNode,
                                      SymTabEntry pfId)
        throws Exception
    {
        // Parse any actual parameters.
        ICodeNode parmsNode = parseActualParameters(token, pfId,
                                                    false, true, false);
        callNode.addChild(parmsNode);

        // Read must have parameters.
        if ((pfId == Predefined.realId) &&
            (callNode.getChildren().size() == 0))
        {
            errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
        }

        return callNode;
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

    /**
     * Parse a call to eof or eoln.
     * @param token the current token.
     * @param callNode the CALL node.
     * @param pfId the symbol table entry of the standard routine name.
     * @return ICodeNode the CALL node.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseEofEoln(Token token, ICodeNode callNode,
                                   SymTabEntry pfId)
        throws Exception
    {
        // Parse any actual parameters.
        ICodeNode parmsNode = parseActualParameters(token, pfId,
                                                    false, false, false);
        callNode.addChild(parmsNode);

        // There should be no actual parameters.
        if (checkParmCount(token, parmsNode, 0)) {
            callNode.setTypeSpec(Predefined.booleanType);
        }

        return callNode;
    }

    /**
     * Check the number of actual parameters.
     * @param token the current token.
     * @param parmsNode the PARAMETERS node.
     * @param count the correct number of parameters.
     * @return true if the count is correct.
     */
    private boolean checkParmCount(Token token, ICodeNode parmsNode, int count)
    {
        if ( ((parmsNode == null) && (count == 0)) ||
             (parmsNode.getChildren().size() == count) ) {
            return true;
        }
        else {
            errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
            return false;
        }
    }
}
