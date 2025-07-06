/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import org.objectweb.asm.tree.IincInsnNode;

public class IincHandler
extends GenericInstructionHandler<IincInsnNode> {
    @Override
    protected void process(MethodContext context, IincInsnNode node) {
        this.props.put("incr", String.valueOf(node.incr));
        this.props.put("var", String.valueOf(node.var));
    }

    @Override
    public String insnToString(MethodContext context, IincInsnNode node) {
        return String.format("IINC %d %d", node.var, node.incr);
    }

    @Override
    public int getNewStackPointer(IincInsnNode node, int currentStackPointer) {
        return currentStackPointer;
    }
}

