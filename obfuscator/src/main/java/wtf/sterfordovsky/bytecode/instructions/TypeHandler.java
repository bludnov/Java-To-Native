/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import wtf.sterfordovsky.runtime.cached.MethodProcessor;
import wtf.sterfordovsky.utils.Util;
import org.objectweb.asm.tree.TypeInsnNode;

public class TypeHandler
extends GenericInstructionHandler<TypeInsnNode> {
    @Override
    protected void process(MethodContext context, TypeInsnNode node) {
        this.props.put("desc", node.desc);
        int classId = context.getCachedClasses().getId(node.desc);
        context.output.append(String.format("if (!cclasses[%d] || env->IsSameObject(cclasses[%d], NULL)) { cclasses_mtx[%d].lock(); if (!cclasses[%d] || env->IsSameObject(cclasses[%d], NULL)) { if (jclass clazz = %s) { cclasses[%d] = (jclass) env->NewWeakGlobalRef(clazz); env->DeleteLocalRef(clazz); } } cclasses_mtx[%d].unlock(); %s } ", classId, classId, classId, classId, classId, MethodProcessor.getClassGetter(context, node.desc), classId, classId, this.trimmedTryCatchBlock));
        this.props.put("desc_ptr", context.getCachedClasses().getPointer(node.desc));
    }

    @Override
    public String insnToString(MethodContext context, TypeInsnNode node) {
        return String.format("%s %s", Util.getOpcodeString(node.getOpcode()), node.desc);
    }

    @Override
    public int getNewStackPointer(TypeInsnNode node, int currentStackPointer) {
        switch (node.getOpcode()) {
            case 189: 
            case 192: 
            case 193: {
                return currentStackPointer;
            }
            case 187: {
                return currentStackPointer + 1;
            }
        }
        throw new RuntimeException();
    }
}

