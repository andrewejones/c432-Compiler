package wci.intermediate.c.symtabimpl;

import wci.intermediate.*;
import wci.intermediate.symtabimpl.SymTabImpl;

public class SymTabImplC extends SymTabImpl implements SymTab {
	private String functionName = null;
	private boolean isFunction = false;
	private boolean returnSeen = false;

	public String getFunctionName() {
		return functionName;
	}

	public boolean getIsFunction() {
		return isFunction;
	}

	public boolean getReturnSeen() {
		return returnSeen;
	}

	public void setFunctionName(String funcName) {
		functionName = funcName;
	}

	public void setIsFunction(boolean isFunc) {
		isFunction = isFunc;
	}

	public void setReturnSeen(boolean retSeen) {
		returnSeen = retSeen;
	}

	public SymTabImplC(int nestingLevel) {
		super(nestingLevel);
	}

}
