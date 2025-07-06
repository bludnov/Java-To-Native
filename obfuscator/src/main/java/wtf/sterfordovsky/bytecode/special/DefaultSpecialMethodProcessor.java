/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.special;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import wtf.sterfordovsky.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class DefaultSpecialMethodProcessor
implements SpecialMethodProcessor {
    @Override
    public String preProcess(MethodContext context) {
        if (Util.getFlag(context.clazz.access, 512)) {
            List<Type> arguments = Arrays.stream(Type.getArgumentTypes(context.method.desc)).collect(Collectors.toList());
            arguments.add(0, Type.getType(Object.class));
            String resultDesc = Type.getMethodDescriptor(Type.getReturnType(context.method.desc), arguments.toArray(new Type[0]));
            String methodName = String.format("interfacestatic_%d_%d", context.classIndex, context.methodIndex);
            context.proxyMethod = context.obfuscator.getHiddenMethodsPool().getMethod(methodName, resultDesc, methodNode -> {
                methodNode.signature = context.method.signature;
                methodNode.access = 4425;
                methodNode.visibleAnnotations = new ArrayList<AnnotationNode>();
                methodNode.visibleAnnotations.add(new AnnotationNode("Ljava/lang/invoke/LambdaForm$Hidden;"));
                methodNode.visibleAnnotations.add(new AnnotationNode("Ljdk/internal/vm/annotation/Hidden;"));
            });
            return methodName;
        }
        context.method.access |= 0x100;
        return "native_" + context.method.name + context.methodIndex;
    }

    @Override
    public void postProcess(MethodContext context) {
        context.method.instructions.clear();
        if (Util.getFlag(context.clazz.access, 512)) {
            InsnList list = new InsnList();
            if (Util.getFlag(context.method.access, 8)) {
                list.add(new LdcInsnNode(Type.getObjectType(context.clazz.name)));
            }
            int localVarsPosition = 0;
            for (Type arg : context.argTypes) {
                list.add(new VarInsnNode(arg.getOpcode(21), localVarsPosition));
                localVarsPosition += arg.getSize();
            }
            if (context.nativeMethod == null) {
                throw new RuntimeException("Native method not created?!");
            }
            list.add(new MethodInsnNode(184, context.proxyMethod.getClassNode().name, context.proxyMethod.getMethodNode().name, context.proxyMethod.getMethodNode().desc, false));
            list.add(new InsnNode(Type.getReturnType(context.method.desc).getOpcode(172)));
            context.method.instructions = list;
        }
    }
}

