package wci.backend.interpreter.executors;

import java.util.ArrayList;

import wci.intermediate.*;
import wci.backend.interpreter.*;

import static wci.intermediate.ICodeNodeType.*;
import static wci.backend.interpreter.RuntimeErrorCode.*;

/**
 * <h1>ReturnExecutor</h1>
 *
 * <p>Execute an IF statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ReturnExecutor extends StatementExecutor
{
    /**
     * Constructor.
     * @param the parent executor.
     */
    public ReturnExecutor(Executor parent)
    {
        super(parent);
    }

    /**
     * Execute a RETURN statement.
     * @param node the root node of the statement.
     * @return null.
     */
    public Object execute(ICodeNode node)
    {
    	// NEEDS IMPLEMENTED
    	
    	
    	
    	
    	
        return null;
    }
}
