/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.runtime.cached;

import wtf.sterfordovsky.builders.other.CatchesBlock;
import wtf.sterfordovsky.utils.NativeObfuscator;
import wtf.sterfordovsky.utils.Util;
import wtf.sterfordovsky.bytecode.instructions.FieldHandler;
import wtf.sterfordovsky.bytecode.instructions.FrameHandler;
import wtf.sterfordovsky.bytecode.instructions.IincHandler;
import wtf.sterfordovsky.bytecode.instructions.InsnHandler;
import wtf.sterfordovsky.bytecode.instructions.InstructionHandlerContainer;
import wtf.sterfordovsky.bytecode.instructions.InstructionTypeHandler;
import wtf.sterfordovsky.bytecode.instructions.IntHandler;
import wtf.sterfordovsky.bytecode.instructions.InvokeDynamicHandler;
import wtf.sterfordovsky.bytecode.instructions.JumpHandler;
import wtf.sterfordovsky.bytecode.instructions.LabelHandler;
import wtf.sterfordovsky.bytecode.instructions.LdcHandler;
import wtf.sterfordovsky.bytecode.instructions.LineNumberHandler;
import wtf.sterfordovsky.bytecode.instructions.LookupSwitchHandler;
import wtf.sterfordovsky.bytecode.instructions.MethodHandler;
import wtf.sterfordovsky.bytecode.instructions.MultiANewArrayHandler;
import wtf.sterfordovsky.bytecode.instructions.TableSwitchHandler;
import wtf.sterfordovsky.bytecode.instructions.TypeHandler;
import wtf.sterfordovsky.bytecode.instructions.VarHandler;
import wtf.sterfordovsky.bytecode.special.ClInitSpecialMethodProcessor;
import wtf.sterfordovsky.bytecode.special.DefaultSpecialMethodProcessor;
import wtf.sterfordovsky.bytecode.special.SpecialMethodProcessor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class MethodProcessor {
    public static final Map<Integer, String> INSTRUCTIONS = new HashMap<Integer, String>();
    public static final String[] CPP_TYPES;
    public static final int[] TYPE_TO_STACK;
    public static final int[] STACK_TO_STACK;
    private final NativeObfuscator obfuscator;
    private final InstructionHandlerContainer<?>[] handlers;

    public MethodProcessor(NativeObfuscator obfuscator) {
        this.obfuscator = obfuscator;
        this.handlers = new InstructionHandlerContainer[16];
        this.addHandler(0, new InsnHandler(), InsnNode.class);
        this.addHandler(1, new IntHandler(), IntInsnNode.class);
        this.addHandler(2, new VarHandler(), VarInsnNode.class);
        this.addHandler(3, new TypeHandler(), TypeInsnNode.class);
        this.addHandler(4, new FieldHandler(), FieldInsnNode.class);
        this.addHandler(5, new MethodHandler(), MethodInsnNode.class);
        this.addHandler(6, new InvokeDynamicHandler(), InvokeDynamicInsnNode.class);
        this.addHandler(7, new JumpHandler(), JumpInsnNode.class);
        this.addHandler(8, new LabelHandler(), LabelNode.class);
        this.addHandler(9, new LdcHandler(), LdcInsnNode.class);
        this.addHandler(10, new IincHandler(), IincInsnNode.class);
        this.addHandler(11, new TableSwitchHandler(), TableSwitchInsnNode.class);
        this.addHandler(12, new LookupSwitchHandler(), LookupSwitchInsnNode.class);
        this.addHandler(13, new MultiANewArrayHandler(), MultiANewArrayInsnNode.class);
        this.addHandler(14, new FrameHandler(), FrameNode.class);
        this.addHandler(15, new LineNumberHandler(), LineNumberNode.class);
    }

    private <T extends AbstractInsnNode> void addHandler(int id, InstructionTypeHandler<T> handler, Class<T> instructionClass) {
        this.handlers[id] = new InstructionHandlerContainer<T>(handler, instructionClass);
    }

    private SpecialMethodProcessor getSpecialMethodProcessor(String name) {
        switch (name) {
            case "<init>": {
                return null;
            }
            case "<clinit>": {
                return new ClInitSpecialMethodProcessor();
            }
        }
        return new DefaultSpecialMethodProcessor();
    }

    public static boolean shouldProcess(MethodNode method) {
        return !Util.getFlag(method.access, 1024) && !Util.getFlag(method.access, 256) && !method.name.equals("<init>");
    }

    public static String getClassGetter(MethodContext context, String desc) {
        if (desc.startsWith("[")) {
            return "env->FindClass(" + context.getStringPool().get(desc) + ")";
        }
        if (desc.endsWith(";")) {
            desc = desc.substring(1, desc.length() - 1);
        }
        return "utils::find_class_wo_static(env, classloader, " + context.getCachedStrings().getPointer(desc.replace('/', '.')) + ")";
    }

    public void processMethod(MethodContext context) {
        int i;
        MethodNode method = context.method;
        StringBuilder output = context.output;
        SpecialMethodProcessor specialMethodProcessor = this.getSpecialMethodProcessor(method.name);
        if (specialMethodProcessor == null) {
            throw new RuntimeException(String.format("Could not find special method processor for %s", method.name));
        }
        output.append("// ").append(Util.escapeCommentString(method.name)).append(Util.escapeCommentString(method.desc)).append("\n");
        String methodName = specialMethodProcessor.preProcess(context);
        methodName = "__ngen_" + methodName.replace('/', '_');
        context.cppNativeMethodName = methodName = Util.escapeCppNameString(methodName);
        boolean isStatic = Util.getFlag(method.access, 8);
        context.ret = Type.getReturnType(method.desc);
        Type[] args = Type.getArgumentTypes(method.desc);
        context.argTypes = new ArrayList<Type>(Arrays.asList(args));
        if (!isStatic) {
            context.argTypes.add(0, Type.getType(Object.class));
        }
        if (context.proxyMethod != null) {
            context.nativeMethod = context.proxyMethod.getMethodNode();
            context.nativeMethod.access |= 0x100;
        } else {
            context.nativeMethods.append(String.format("            { %s, %s, (void *)&%s },\n", this.obfuscator.getStringPool().get(context.method.name), this.obfuscator.getStringPool().get(method.desc), methodName));
        }
        output.append(String.format("%s JNICALL %s(JNIEnv *env, ", CPP_TYPES[context.ret.getSort()], methodName));
        if (context.proxyMethod != null) {
            output.append("jobject ignored_hidden, ");
        }
        output.append(isStatic ? "jclass clazz" : "jobject obj");
        ArrayList<String> argNames = new ArrayList<String>();
        if (!isStatic) {
            argNames.add("obj");
        }
        for (i = 0; i < args.length; ++i) {
            argNames.add("arg" + i);
            output.append(String.format(", %s arg%d", CPP_TYPES[args[i].getSort()], i));
        }
        output.append(") {").append("\n");
        if (context.proxyMethod != null) {
            output.append("    env->DeleteLocalRef(ignored_hidden);\n");
        }
        if (!isStatic) {
            output.append("    jclass clazz = utils::get_class_from_object(env, obj);\n");
            output.append("    if (env->ExceptionCheck()) { ").append(String.format("return (%s) 0;", CPP_TYPES[context.ret.getSort()])).append(" }\n");
        }
        output.append("    jobject classloader = utils::get_classloader_from_class(env, clazz);\n");
        output.append("    if (env->ExceptionCheck()) { ").append(String.format("return (%s) 0;", CPP_TYPES[context.ret.getSort()])).append(" }\n");
        output.append("    if (classloader == nullptr) { env->FatalError(").append(context.getStringPool().get("classloader == null")).append(String.format("); return (%s) 0; }\n", CPP_TYPES[context.ret.getSort()]));
        output.append("\n");
        if (!isStatic) {
            output.append("    env->DeleteLocalRef(clazz);\n");
            output.append("    clazz = utils::find_class_wo_static(env, classloader, ").append(context.getCachedStrings().getPointer(context.clazz.name.replace('/', '.'))).append(");\n");
            output.append("    if (env->ExceptionCheck()) { ").append(String.format("return (%s) 0;", CPP_TYPES[context.ret.getSort()])).append(" }\n");
        }
        output.append("    jobject lookup = nullptr;\n");
        if (method.tryCatchBlocks != null) {
            for (TryCatchBlockNode tryCatch : method.tryCatchBlocks) {
                context.getLabelPool().getName(tryCatch.start.getLabel());
                context.getLabelPool().getName(tryCatch.end.getLabel());
                context.getLabelPool().getName(tryCatch.handler.getLabel());
            }
            Set<String> classesForTryCatches = method.tryCatchBlocks.stream().filter(tryCatchBlock -> tryCatchBlock.type != null).map(x -> x.type).collect(Collectors.toSet());
            classesForTryCatches.forEach(clazz -> {
                int classId = context.getCachedClasses().getId((String)clazz);
                context.output.append(String.format("    // try-catch-class %s\n", Util.escapeCommentString(clazz)));
                context.output.append(String.format("    if (!cclasses[%d] || env->IsSameObject(cclasses[%d], NULL)) { cclasses_mtx[%d].lock(); if (!cclasses[%d] || env->IsSameObject(cclasses[%d], NULL)) { if (jclass clazz = %s) { cclasses[%d] = (jclass) env->NewWeakGlobalRef(clazz); env->DeleteLocalRef(clazz); } } cclasses_mtx[%d].unlock(); if (env->ExceptionCheck()) { return (%s) 0; } }\n", classId, classId, classId, classId, classId, MethodProcessor.getClassGetter(context, clazz), classId, classId, CPP_TYPES[context.ret.getSort()]));
            });
        }
        if (method.maxStack > 0) {
            output.append("    jvalue ");
            for (i = 0; i < method.maxStack; ++i) {
                output.append(String.format("cstack%s = {}", i));
                if (i == method.maxStack - 1) continue;
                output.append(", ");
            }
            output.append(";\n");
        }
        if (method.maxLocals > 0) {
            output.append("    jvalue ");
            for (i = 0; i < method.maxLocals; ++i) {
                output.append(String.format("clocal%s = {}", i));
                if (i == method.maxLocals - 1) continue;
                output.append(", ");
            }
            output.append(";\n");
        }
        output.append("    std::unordered_set<jobject> refs;\n");
        output.append("\n");
        int localIndex = 0;
        for (int i2 = 0; i2 < context.argTypes.size(); ++i2) {
            Type current = context.argTypes.get(i2);
            output.append("    ").append(this.obfuscator.getSnippets().getSnippet("LOCAL_LOAD_ARG_" + current.getSort(), Util.createMap("index", localIndex, "arg", argNames.get(i2)))).append("\n");
            localIndex += current.getSize();
        }
        output.append("\n");
        context.argTypes.forEach(t -> context.locals.add(TYPE_TO_STACK[t.getSort()]));
        context.stackPointer = 0;
        for (int instruction = 0; instruction < method.instructions.size(); ++instruction) {
            AbstractInsnNode node = method.instructions.get(instruction);
            context.output.append("    // ").append(Util.escapeCommentString(this.handlers[node.getType()].insnToString(context, node))).append("; Stack: ").append(context.stackPointer).append("\n");
            this.handlers[node.getType()].accept(context, node);
            context.stackPointer = this.handlers[node.getType()].getNewStackPointer(node, context.stackPointer);
            context.output.append("    // New stack: ").append(context.stackPointer).append("\n");
        }
        output.append(String.format("    return (%s) 0;\n", CPP_TYPES[context.ret.getSort()]));
        boolean hasAddedNewBlocks = true;
        HashSet<CatchesBlock> proceedBlocks = new HashSet<CatchesBlock>();
        while (hasAddedNewBlocks) {
            hasAddedNewBlocks = false;
            for (CatchesBlock catchBlock : new ArrayList<CatchesBlock>(context.catches.keySet())) {
                if (proceedBlocks.contains(catchBlock)) continue;
                proceedBlocks.add(catchBlock);
                output.append("    ").append(context.catches.get(catchBlock)).append(": ");
                CatchesBlock.CatchBlock currentCatchBlock = catchBlock.getCatches().get(0);
                if (currentCatchBlock.getClazz() == null) {
                    output.append(context.getSnippets().getSnippet("TRYCATCH_ANY_L", Util.createMap("handler_block", context.getLabelPool().getName(currentCatchBlock.getHandler().getLabel()))));
                    output.append("\n");
                    continue;
                }
                output.append(context.getSnippets().getSnippet("TRYCATCH_CHECK_STACK", Util.createMap("exception_class_ptr", context.getCachedClasses().getPointer(currentCatchBlock.getClazz()), "handler_block", context.getLabelPool().getName(currentCatchBlock.getHandler().getLabel()))));
                output.append("\n");
                if (catchBlock.getCatches().size() == 1) {
                    output.append("    ");
                    output.append(context.getSnippets().getSnippet("TRYCATCH_END_STACK", Util.createMap("rettype", CPP_TYPES[context.ret.getSort()])));
                    output.append("\n");
                    continue;
                }
                CatchesBlock nextCatchesBlock = new CatchesBlock(catchBlock.getCatches().stream().skip(1L).collect(Collectors.toList()));
                if (context.catches.get(nextCatchesBlock) == null) {
                    context.catches.put(nextCatchesBlock, String.format("L_CATCH_%d", context.catches.size()));
                    hasAddedNewBlocks = true;
                }
                output.append("    ");
                output.append(context.getSnippets().getSnippet("TRYCATCH_ANY_L", Util.createMap("handler_block", context.catches.get(nextCatchesBlock))));
                output.append("\n");
            }
        }
        output.append("}\n\n");
        method.localVariables.clear();
        method.tryCatchBlocks.clear();
        specialMethodProcessor.postProcess(context);
    }

    public static String nameFromNode(MethodNode m, ClassNode cn) {
        return cn.name + '#' + m.name + '!' + m.desc;
    }

    static {
        try {
            for (Field f : Opcodes.class.getFields()) {
                INSTRUCTIONS.put((int)((Integer)f.get(null)), f.getName());
            }
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
        CPP_TYPES = new String[]{"void", "jboolean", "jchar", "jbyte", "jshort", "jint", "jfloat", "jlong", "jdouble", "jarray", "jobject", "jobject"};
        TYPE_TO_STACK = new int[]{1, 1, 1, 1, 1, 1, 1, 2, 2, 0, 0, 0};
        STACK_TO_STACK = new int[]{1, 1, 1, 2, 2, 0, 0, 0, 0};
    }
}

