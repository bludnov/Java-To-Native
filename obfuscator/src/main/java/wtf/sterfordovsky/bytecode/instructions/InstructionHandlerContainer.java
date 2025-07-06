/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import org.objectweb.asm.tree.AbstractInsnNode;

public class InstructionHandlerContainer<T extends AbstractInsnNode> {
    private final InstructionTypeHandler<T> handler;
    private final Class<T> clazz;

    public InstructionHandlerContainer(InstructionTypeHandler<T> handler, Class<T> clazz) {
        this.handler = handler;
        this.clazz = clazz;
    }

    public void accept(MethodContext context, AbstractInsnNode node) {
        this.handler.accept(context, this.clazz.cast(node));
    }

    public String insnToString(MethodContext context, AbstractInsnNode node) {
        return this.handler.insnToString(context, this.clazz.cast(node));
    }

    public int getNewStackPointer(AbstractInsnNode node, int stackPointer) {
        return this.handler.getNewStackPointer(this.clazz.cast(node), stackPointer);
    }
}

