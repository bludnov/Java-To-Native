/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.special;

import wtf.sterfordovsky.runtime.cached.MethodContext;

import java.util.ArrayList;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class ClInitSpecialMethodProcessor
implements SpecialMethodProcessor {
    @Override
    public String preProcess(MethodContext context) {
        String name = String.format("special_clinit_%d_%d", context.classIndex, context.methodIndex);
        context.proxyMethod = context.obfuscator.getHiddenMethodsPool().getMethod(name, "(Ljava/lang/Class;)V", methodNode -> {
            methodNode.signature = context.method.signature;
            methodNode.access = 4425;
            methodNode.visibleAnnotations = new ArrayList<AnnotationNode>();
            methodNode.visibleAnnotations.add(new AnnotationNode("Ljava/lang/invoke/LambdaForm$Hidden;"));
            methodNode.visibleAnnotations.add(new AnnotationNode("Ljdk/internal/vm/annotation/Hidden;"));
        });
        return name;
    }

    @Override
    public void postProcess(MethodContext context) {
        InsnList instructions = context.method.instructions;
        instructions.clear();
        instructions.add(new LdcInsnNode((Object)context.classIndex));
        instructions.add(new LdcInsnNode(Type.getObjectType(context.clazz.name)));
        instructions.add(new MethodInsnNode(184, context.obfuscator.getNativeDir() + "/Loader", "registerNativesForClass", "(ILjava/lang/Class;)V", false));
        instructions.add(new LdcInsnNode(Type.getObjectType(context.clazz.name)));
        instructions.add(new MethodInsnNode(184, context.proxyMethod.getClassNode().name, context.proxyMethod.getMethodNode().name, context.proxyMethod.getMethodNode().desc, false));
        instructions.add(new InsnNode(177));
    }
}

