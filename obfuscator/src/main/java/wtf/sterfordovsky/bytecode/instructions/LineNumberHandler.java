/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import org.objectweb.asm.tree.LineNumberNode;

public class LineNumberHandler
implements InstructionTypeHandler<LineNumberNode> {
    @Override
    public void accept(MethodContext context, LineNumberNode node) {
        context.line = node.line;
    }

    @Override
    public String insnToString(MethodContext context, LineNumberNode node) {
        return String.format("Line %d", node.line);
    }

    @Override
    public int getNewStackPointer(LineNumberNode node, int currentStackPointer) {
        return currentStackPointer;
    }
}

