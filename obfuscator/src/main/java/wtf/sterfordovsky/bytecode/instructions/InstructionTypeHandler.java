/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import org.objectweb.asm.tree.AbstractInsnNode;

public interface InstructionTypeHandler<T extends AbstractInsnNode> {
    public void accept(MethodContext var1, T var2);

    public String insnToString(MethodContext var1, T var2);

    public int getNewStackPointer(T var1, int var2);
}

