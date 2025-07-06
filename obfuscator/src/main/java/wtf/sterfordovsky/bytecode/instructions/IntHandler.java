/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import wtf.sterfordovsky.utils.Util;
import org.objectweb.asm.tree.IntInsnNode;

public class IntHandler
extends GenericInstructionHandler<IntInsnNode> {
    @Override
    protected void process(MethodContext context, IntInsnNode node) {
        this.props.put("operand", String.valueOf(node.operand));
        if (node.getOpcode() == 188) {
            this.instructionName = this.instructionName + "_" + node.operand;
        }
    }

    @Override
    public String insnToString(MethodContext context, IntInsnNode node) {
        return String.format("%s %d", Util.getOpcodeString(node.getOpcode()), node.operand);
    }

    @Override
    public int getNewStackPointer(IntInsnNode node, int currentStackPointer) {
        switch (node.getOpcode()) {
            case 16: 
            case 17: {
                return currentStackPointer + 1;
            }
            case 188: {
                return currentStackPointer;
            }
        }
        throw new RuntimeException();
    }
}

