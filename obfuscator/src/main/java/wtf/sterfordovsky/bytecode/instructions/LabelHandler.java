/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import org.objectweb.asm.tree.LabelNode;

public class LabelHandler
extends GenericInstructionHandler<LabelNode> {
    @Override
    public void accept(MethodContext context, LabelNode node) {
        context.method.tryCatchBlocks.stream().filter(x -> x.start.equals(node)).forEachOrdered(context.tryCatches::add);
        context.method.tryCatchBlocks.stream().filter(x -> x.end.equals(node)).forEachOrdered(context.tryCatches::remove);
        try {
            super.accept(context, node);
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
        context.output.append(String.format("%s: %s\n", context.getLabelPool().getName(node.getLabel()), this.trimmedTryCatchBlock));
    }

    @Override
    public String insnToString(MethodContext context, LabelNode node) {
        return String.format("LABEL %s", context.getLabelPool().getName(node.getLabel()));
    }

    @Override
    public int getNewStackPointer(LabelNode node, int currentStackPointer) {
        return currentStackPointer;
    }

    @Override
    protected void process(MethodContext context, LabelNode node) {
        throw new UnsupportedOperationException("break at super.process()");
    }
}

