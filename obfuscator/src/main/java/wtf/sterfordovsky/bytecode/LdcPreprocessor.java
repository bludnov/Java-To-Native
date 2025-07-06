/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode;

import wtf.sterfordovsky.runtime.TypesVMP;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LdcPreprocessor
implements Preprocessor {
    @Override
    public void process(ClassNode classNode, MethodNode methodNode, TypesVMP typesVMP) {
        AbstractInsnNode insnNode = methodNode.instructions.getFirst();
        while (insnNode != null) {
            if (insnNode instanceof LdcInsnNode) {
                Type type;
                LdcInsnNode ldcInsnNode = (LdcInsnNode)insnNode;
                if (ldcInsnNode.cst instanceof Handle) {
                    methodNode.instructions.insertBefore((AbstractInsnNode)ldcInsnNode, MethodHandleUtils.generateMethodHandleLdcInsn((Handle)ldcInsnNode.cst));
                    AbstractInsnNode nextInsnNode = insnNode.getNext();
                    methodNode.instructions.remove(insnNode);
                    insnNode = nextInsnNode;
                    continue;
                }
                if (ldcInsnNode.cst instanceof Type && (type = (Type)ldcInsnNode.cst).getSort() == 11) {
                    methodNode.instructions.insertBefore((AbstractInsnNode)ldcInsnNode, MethodHandleUtils.generateMethodTypeLdcInsn(type));
                    AbstractInsnNode nextInsnNode = insnNode.getNext();
                    methodNode.instructions.remove(insnNode);
                    insnNode = nextInsnNode;
                }
            }
            insnNode = insnNode.getNext();
        }
    }
}

