/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import wtf.sterfordovsky.utils.Util;
import org.objectweb.asm.tree.JumpInsnNode;

public class JumpHandler
extends GenericInstructionHandler<JumpInsnNode> {
    @Override
    protected void process(MethodContext context, JumpInsnNode node) {
        this.props.put("label", String.valueOf(context.getLabelPool().getName(node.label.getLabel())));
    }

    @Override
    public String insnToString(MethodContext methodContext, JumpInsnNode node) {
        return String.format("%s %s", Util.getOpcodeString(node.getOpcode()), methodContext.getLabelPool().getName(node.label.getLabel()));
    }

    @Override
    public int getNewStackPointer(JumpInsnNode node, int currentStackPointer) {
        switch (node.getOpcode()) {
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 198: 
            case 199: {
                return currentStackPointer - 1;
            }
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: {
                return currentStackPointer - 2;
            }
            case 167: {
                return currentStackPointer;
            }
        }
        throw new RuntimeException();
    }
}

