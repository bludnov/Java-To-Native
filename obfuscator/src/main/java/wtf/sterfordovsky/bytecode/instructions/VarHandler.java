/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import wtf.sterfordovsky.utils.Util;
import org.objectweb.asm.tree.VarInsnNode;

public class VarHandler
extends GenericInstructionHandler<VarInsnNode> {
    @Override
    protected void process(MethodContext context, VarInsnNode node) {
        this.props.put("var", String.valueOf(node.var));
    }

    @Override
    public String insnToString(MethodContext context, VarInsnNode node) {
        return String.format("%s %d", Util.getOpcodeString(node.getOpcode()), node.var);
    }

    @Override
    public int getNewStackPointer(VarInsnNode node, int currentStackPointer) {
        switch (node.getOpcode()) {
            case 21: 
            case 23: 
            case 25: {
                return currentStackPointer + 1;
            }
            case 22: 
            case 24: {
                return currentStackPointer + 2;
            }
            case 54: 
            case 56: 
            case 58: {
                return currentStackPointer - 1;
            }
            case 55: 
            case 57: {
                return currentStackPointer - 2;
            }
        }
        throw new RuntimeException();
    }
}

