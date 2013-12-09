package wci.backend.compiler.generators;

import wci.intermediate.*;
import wci.backend.compiler.*;

import static wci.backend.compiler.Instruction.*;

public class ReturnGenerator extends StatementGenerator {
	
	static Label newLabel;

	public ReturnGenerator(CodeGenerator parent) {
		super(parent);
	}

	public void generate(ICodeNode node) throws PascalCompilerException {
		newLabel = Label.newLabel();
		emit(GOTO, newLabel);
	}

	static public Label getReturnLabel() {
		return newLabel;
	}
}
