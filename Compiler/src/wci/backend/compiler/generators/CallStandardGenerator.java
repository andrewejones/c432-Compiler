package wci.backend.compiler.generators;

import java.util.ArrayList;

import wci.frontend.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.backend.compiler.*;

import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.RoutineCodeImpl.*;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.backend.compiler.Instruction.*;

/**
 * <h1>CallStandardGenerator</h1>
 *
 * <p>Generate code to call a standard procedure or function.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CallStandardGenerator extends CallGenerator
{
    private ExpressionGenerator exprGenerator;

    /**
     * Constructor.
     * @param the parent executor.
     */
    public CallStandardGenerator(CodeGenerator parent)
    {
        super(parent);
    }

    /**
     * Generate code to call to a standard procedure or function.
     * @param node the CALL node.
     * @return the function value, or null for a procedure call.
     */
    public void generate(ICodeNode node)
    {
        SymTabEntry routineId = (SymTabEntry) node.getAttribute(ID);
        RoutineCode routineCode =
                        (RoutineCode) routineId.getAttribute(ROUTINE_CODE);
        exprGenerator = new ExpressionGenerator(this);
        ICodeNode actualNode = null;

        // Get the actual parameters of the call.
        if (node.getChildren().size() > 0) {
            ICodeNode parmsNode = node.getChildren().get(0);
            actualNode = parmsNode.getChildren().get(0);
        }

        switch ((RoutineCodeImpl) routineCode) {
            case WRITE:
            case WRITELN: generateWriteWriteln(node, routineCode); break;

            case EOF:
            case EOLN:    generateEofEoln(node, routineCode); break;
        }
    }


    /**
     * Generate code for a call to eof or eoln.
     * @param callNode the CALL node.
     * @param routineCode the routine code.
     */
    private void generateEofEoln(ICodeNode callNode, RoutineCode routineCode)
    {
        String programName = symTabStack.getProgramId().getName();
        String standardInName = programName + "/_standardIn";

        // Generate code to call the appropriate CTextIn method.
        emit(GETSTATIC, standardInName, "LCTextIn;");
        if (routineCode == EOLN) {
            emit(INVOKEVIRTUAL, "CTextIn.atEoln()Z");
        }
        else {
            emit(INVOKEVIRTUAL, "CTextIn.atEof()Z");
        }

        localStack.increase(1);
    }

    /**
     * Generate code for a call to write or writeln.
     * @param callNode the CALL node.
     * @param routineCode the routine code.
     */
    private void generateWriteWriteln(ICodeNode callNode,
                                      RoutineCode routineCode)
    {
        ICodeNode parmsNode = callNode.getChildren().size() > 0
                                  ? callNode.getChildren().get(0)
                                  : null;
        StringBuilder buffer = new StringBuilder();
        int exprCount = 0;

        buffer.append("\"");

        // There are actual parameters.
        if (parmsNode != null) {
            ArrayList<ICodeNode> actuals = parmsNode.getChildren();

            // Loop to process each WRITE parameter
            // and build the format string.
            for (ICodeNode writeParmNode : actuals) {
                ArrayList<ICodeNode> children = writeParmNode.getChildren();
                ICodeNode exprNode = children.get(0);
                ICodeNodeType nodeType = exprNode.getType();

                // Append string constants directly to the format string.
                if (nodeType == STRING_CONSTANT) {
                    String str = (String)exprNode.getAttribute(VALUE);
                    buffer.append(str.replaceAll("%", "%%"));
                }

                // Create and append the appropriate format specification.
                else {
                    TypeSpec dataType = exprNode.getTypeSpec().baseType();
                    String typeCode = dataType.isCString()          ? "s"
                                    : dataType == Predefined.integerType ? "d"
                                    : dataType == Predefined.realType    ? "f"
                                    : dataType == Predefined.booleanType ? "s"
                                    : dataType == Predefined.charType    ? "c"
                                    :                                      "s";

                    ++exprCount;  // count the non-constant string parameters
                    buffer.append("%");

                    // Process any field width and precision values.
                    if (children.size() > 1) {
                        int w = (Integer) children.get(1).getAttribute(VALUE);
                        buffer.append(w == 0 ? 1 : w);
                    }
                    if (children.size() > 2) {
                        int p = (Integer) children.get(2).getAttribute(VALUE);
                        buffer.append(".");
                        buffer.append(p == 0 ? 1 : p);
                    }

                    buffer.append(typeCode);
                }
            }

            buffer.append(routineCode == WRITELN ? "\\n\"" : "\"");
        }

        emit(GETSTATIC, "java/lang/System/out", "Ljava/io/PrintStream;");
        localStack.increase(1);

        // WRITELN with no parameters.
        if (parmsNode == null) {
            emit(INVOKEVIRTUAL, "java/io/PrintStream.println()V");
            localStack.decrease(1);
        }

        // WRITE or WRITELN with parameters.
        else {
            ArrayList<ICodeNode> actuals = parmsNode.getChildren();

            // Load the format string.
            emit(LDC, buffer.toString());
            localStack.increase(1);

            // Generate code to create the values array for String.format().
            if (exprCount > 0) {
                emitLoadConstant(exprCount);
                emit(ANEWARRAY, "java/lang/Object");
                localStack.use(3, 1);

                int index = 0;
                ExpressionGenerator exprGenerator =
                                        new ExpressionGenerator(this);

                // Loop to generate code to evaluate each actual parameter.
                for (ICodeNode writeParmNode : actuals) {
                    ArrayList<ICodeNode> children = writeParmNode.getChildren();
                    ICodeNode exprNode = children.get(0);
                    ICodeNodeType nodeType = exprNode.getType();
                    TypeSpec dataType = exprNode.getTypeSpec().baseType();

                    // Skip string constants, which were made part of
                    // the format string.
                    if (nodeType != STRING_CONSTANT) {
                        emit(DUP);
                        emitLoadConstant(index++);
                        localStack.increase(2);

                        exprGenerator.generate(exprNode);

                        String signature = dataType.getForm() == SCALAR
                            ? valueOfSignature(dataType)
                            : null;

                        // Boolean: Write "true" or "false".
                        if (dataType == Predefined.booleanType) {
                            Label trueLabel = Label.newLabel();
                            Label nextLabel = Label.newLabel();
                            emit(IFNE, trueLabel);
                            emit(LDC, "\"false\"");
                            emit(Instruction.GOTO, nextLabel);
                            emitLabel(trueLabel);
                            emit(LDC, "\"true\"");
                            emitLabel(nextLabel);

                            localStack.use(1);
                        }

                        // Convert a scalar value to an object.
                        if (signature != null) {
                            emit(INVOKESTATIC, signature);
                        }

                        // Store the value into the values vector.
                        emit(AASTORE);
                        localStack.decrease(3);
                    }
                }

                // Format the string.
                emit(INVOKESTATIC,
                     "java/lang/String/format(Ljava/lang/String;" +
                     "[Ljava/lang/Object;)Ljava/lang/String;");
                localStack.decrease(2);
            }

            // Print.
            emit(INVOKEVIRTUAL,
                 "java/io/PrintStream.print(Ljava/lang/String;)V");
            localStack.decrease(2);
        }
    }
    
 }
