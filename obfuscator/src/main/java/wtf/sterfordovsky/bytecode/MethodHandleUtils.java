/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class MethodHandleUtils {
    private static AbstractInsnNode getTypeLoadInsnNode(Type type) {
        switch (type.getSort()) {
            case 9: 
            case 10: {
                return new LdcInsnNode(type);
            }
            case 1: {
                return new FieldInsnNode(178, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
            }
            case 3: {
                return new FieldInsnNode(178, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
            }
            case 2: {
                return new FieldInsnNode(178, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
            }
            case 8: {
                return new FieldInsnNode(178, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
            }
            case 6: {
                return new FieldInsnNode(178, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
            }
            case 5: {
                return new FieldInsnNode(178, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
            }
            case 7: {
                return new FieldInsnNode(178, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
            }
            case 4: {
                return new FieldInsnNode(178, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
            }
        }
        throw new RuntimeException(String.format("Unsupported TypeLoad type: %s", type));
    }

    public static InsnList generateMethodTypeLdcInsn(Type type) {
        if (type.getSort() != 11) {
            throw new RuntimeException(String.format("Not a MT: %s", type));
        }
        InsnList insntructions = new InsnList();
        insntructions.add(new LdcInsnNode(type.getDescriptor()));
        insntructions.add(PreprocessorUtils.CLASSLOADER_LOCAL.get());
        insntructions.add(new MethodInsnNode(184, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;"));
        return insntructions;
    }

    public static InsnList generateMethodHandleLdcInsn(Handle handle) {
        InsnList instructions = new InsnList();
        instructions.add(PreprocessorUtils.LOOKUP_LOCAL.get());
        instructions.add(new LdcInsnNode(Type.getObjectType(handle.getOwner())));
        switch (handle.getTag()) {
            case 1: 
            case 2: 
            case 3: 
            case 4: {
                instructions.add(new LdcInsnNode(handle.getName()));
                instructions.add(MethodHandleUtils.getTypeLoadInsnNode(Type.getType(handle.getDesc())));
                String methodName = "";
                switch (handle.getTag()) {
                    case 1: {
                        methodName = "findGetter";
                        break;
                    }
                    case 2: {
                        methodName = "findStaticGetter";
                        break;
                    }
                    case 3: {
                        methodName = "findSetter";
                        break;
                    }
                    case 4: {
                        methodName = "findStaticSetter";
                    }
                }
                instructions.add(new MethodInsnNode(182, "java/lang/invoke/MethodHandles$Lookup", methodName, "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;"));
                break;
            }
            case 5: 
            case 9: {
                instructions.add(new LdcInsnNode(handle.getName()));
                instructions.add(new LdcInsnNode(handle.getDesc()));
                instructions.add(PreprocessorUtils.CLASSLOADER_LOCAL.get());
                instructions.add(new MethodInsnNode(184, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;"));
                instructions.add(new MethodInsnNode(182, "java/lang/invoke/MethodHandles$Lookup", "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;"));
                break;
            }
            case 6: {
                instructions.add(new LdcInsnNode(handle.getName()));
                instructions.add(new LdcInsnNode(handle.getDesc()));
                instructions.add(PreprocessorUtils.CLASSLOADER_LOCAL.get());
                instructions.add(new MethodInsnNode(184, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;"));
                instructions.add(new MethodInsnNode(182, "java/lang/invoke/MethodHandles$Lookup", "findStatic", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;"));
                break;
            }
            case 7: {
                instructions.add(new LdcInsnNode(handle.getName()));
                instructions.add(new LdcInsnNode(handle.getDesc()));
                instructions.add(PreprocessorUtils.CLASSLOADER_LOCAL.get());
                instructions.add(new MethodInsnNode(184, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;"));
                instructions.add(PreprocessorUtils.CLASS_LOCAL.get());
                instructions.add(new MethodInsnNode(182, "java/lang/invoke/MethodHandles$Lookup", "findSpecial", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;"));
                break;
            }
            case 8: {
                instructions.add(new LdcInsnNode(handle.getDesc()));
                instructions.add(PreprocessorUtils.CLASSLOADER_LOCAL.get());
                instructions.add(new MethodInsnNode(184, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;"));
                instructions.add(new MethodInsnNode(182, "java/lang/invoke/MethodHandles$Lookup", "findConstructor", "(Ljava/lang/Class;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;"));
            }
        }
        return instructions;
    }
}

