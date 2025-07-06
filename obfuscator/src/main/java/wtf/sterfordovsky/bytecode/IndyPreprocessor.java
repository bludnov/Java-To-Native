/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode;

import wtf.sterfordovsky.runtime.TypesVMP;
import wtf.sterfordovsky.utils.Util;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class IndyPreprocessor
implements Preprocessor {
    private static void processIndy(ClassNode classNode, MethodNode methodNode, InvokeDynamicInsnNode invokeDynamicInsnNode, TypesVMP typesVMP) {
        LabelNode bootstrapStart = new LabelNode(new Label());
        LabelNode bootstrapEnd = new LabelNode(new Label());
        LabelNode bsmeStart = new LabelNode(new Label());
        LabelNode invokeStart = new LabelNode(new Label());
        InsnList bootstrapInstructions = new InsnList();
        bootstrapInstructions.add(bootstrapStart);
        switch (typesVMP) {
            case DEFAULT_JAVA: {
                Object[] newArgs;
                Type[] bsmArguments = Type.getArgumentTypes(invokeDynamicInsnNode.bsm.getDesc());
                int targetArgLength = bsmArguments.length - 3;
                int originArgLength = invokeDynamicInsnNode.bsmArgs.length;
                if (originArgLength < targetArgLength) {
                    newArgs = new Object[targetArgLength];
                    System.arraycopy(invokeDynamicInsnNode.bsmArgs, 0, newArgs, 0, originArgLength);
                    if (targetArgLength - originArgLength != 1) {
                        throw new RuntimeException("Impossible BSM arguments length");
                    }
                    if (bsmArguments[originArgLength + 3].getSort() != 9) {
                        throw new RuntimeException("Last argument of BSM is not a vararg array");
                    }
                    newArgs[originArgLength] = new Object[0];
                    invokeDynamicInsnNode.bsmArgs = newArgs;
                } else if (originArgLength > targetArgLength || bsmArguments[bsmArguments.length - 1].getSort() == 9 && Type.getType(invokeDynamicInsnNode.bsmArgs[invokeDynamicInsnNode.bsmArgs.length - 1].getClass()).getSort() != 9) {
                    newArgs = new Object[targetArgLength];
                    System.arraycopy(invokeDynamicInsnNode.bsmArgs, 0, newArgs, 0, targetArgLength - 1);
                    Object[] varArgs = new Object[originArgLength - targetArgLength + 1];
                    System.arraycopy(invokeDynamicInsnNode.bsmArgs, targetArgLength - 1, varArgs, 0, originArgLength - targetArgLength + 1);
                    newArgs[targetArgLength - 1] = varArgs;
                    invokeDynamicInsnNode.bsmArgs = newArgs;
                }
                if (!(bsmArguments.length >= 3 && bsmArguments[0].getDescriptor().equals("Ljava/lang/invoke/MethodHandles$Lookup;") && bsmArguments[1].getDescriptor().equals("Ljava/lang/String;") && bsmArguments[2].getDescriptor().equals("Ljava/lang/invoke/MethodType;"))) {
                    InsnList resultInstructions = new InsnList();
                    resultInstructions.add(new TypeInsnNode(187, "java/lang/BootstrapMethodError"));
                    resultInstructions.add(new InsnNode(89));
                    resultInstructions.add(new LdcInsnNode("Wrong 3 first arguments in bsm"));
                    resultInstructions.add(new MethodInsnNode(183, "java/lang/BootstrapMethodError", "<init>", "(Ljava/lang/String;)V"));
                    resultInstructions.add(new InsnNode(191));
                    methodNode.instructions.insert((AbstractInsnNode)invokeDynamicInsnNode, resultInstructions);
                    methodNode.instructions.remove(invokeDynamicInsnNode);
                    return;
                }
                Type[] arguments = Type.getArgumentTypes(invokeDynamicInsnNode.desc);
                bootstrapInstructions.add(new LdcInsnNode((Object)arguments.length));
                bootstrapInstructions.add(new TypeInsnNode(189, "java/lang/Object"));
                int index = arguments.length;
                for (Type argument : Util.reverse(Arrays.stream(arguments)).collect(Collectors.toList())) {
                    --index;
                    if (argument.getSize() == 1) {
                        if (argument.getSort() != 9 && argument.getSort() != 10) {
                            bootstrapInstructions.add(new InsnNode(95));
                            bootstrapInstructions.add(IndyPreprocessor.getBoxingInsnNode(argument));
                            bootstrapInstructions.add(new InsnNode(95));
                        }
                    } else if (argument.getSize() == 2) {
                        bootstrapInstructions.add(new InsnNode(91));
                        bootstrapInstructions.add(new InsnNode(87));
                        bootstrapInstructions.add(IndyPreprocessor.getBoxingInsnNode(argument));
                        bootstrapInstructions.add(new InsnNode(95));
                    }
                    bootstrapInstructions.add(new InsnNode(89));
                    bootstrapInstructions.add(new InsnNode(93));
                    bootstrapInstructions.add(new InsnNode(88));
                    bootstrapInstructions.add(new LdcInsnNode((Object)index));
                    bootstrapInstructions.add(new InsnNode(95));
                    bootstrapInstructions.add(new InsnNode(83));
                }
                bootstrapInstructions.add(PreprocessorUtils.LOOKUP_LOCAL.get());
                bootstrapInstructions.add(new LdcInsnNode(invokeDynamicInsnNode.name));
                bootstrapInstructions.add(MethodHandleUtils.generateMethodTypeLdcInsn(Type.getMethodType(invokeDynamicInsnNode.desc)));
                for (Object bsmArgument : invokeDynamicInsnNode.bsmArgs) {
                    if (bsmArgument instanceof String) {
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                        continue;
                    }
                    if (bsmArgument instanceof Type) {
                        if (((Type)bsmArgument).getSort() == 11) {
                            bootstrapInstructions.add(MethodHandleUtils.generateMethodTypeLdcInsn((Type)bsmArgument));
                            continue;
                        }
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                        continue;
                    }
                    if (bsmArgument instanceof Integer) {
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                        continue;
                    }
                    if (bsmArgument instanceof Long) {
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                        continue;
                    }
                    if (bsmArgument instanceof Float) {
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                        continue;
                    }
                    if (bsmArgument instanceof Double) {
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                        continue;
                    }
                    if (bsmArgument instanceof Handle) {
                        bootstrapInstructions.add(MethodHandleUtils.generateMethodHandleLdcInsn((Handle)bsmArgument));
                        continue;
                    }
                    if (bsmArgument instanceof Object[]) {
                        Object[] objects = (Object[])bsmArgument;
                        bootstrapInstructions.add(new LdcInsnNode((Object)objects.length));
                        bootstrapInstructions.add(new TypeInsnNode(189, "java/lang/Object"));
                        int index2 = 0;
                        for (Object object : objects) {
                            bootstrapInstructions.add(new InsnNode(89));
                            bootstrapInstructions.add(new LdcInsnNode((Object)index2));
                            if (object instanceof String) {
                                bootstrapInstructions.add(new LdcInsnNode(object));
                            } else if (object instanceof Type) {
                                if (((Type)object).getSort() == 11) {
                                    bootstrapInstructions.add(MethodHandleUtils.generateMethodTypeLdcInsn((Type)object));
                                } else {
                                    bootstrapInstructions.add(new LdcInsnNode(object));
                                }
                            } else if (object instanceof Integer) {
                                bootstrapInstructions.add(new LdcInsnNode(object));
                                bootstrapInstructions.add(IndyPreprocessor.getBoxingInsnNode(Type.INT_TYPE));
                            } else if (object instanceof Long) {
                                bootstrapInstructions.add(new LdcInsnNode(object));
                                bootstrapInstructions.add(IndyPreprocessor.getBoxingInsnNode(Type.LONG_TYPE));
                            } else if (object instanceof Float) {
                                bootstrapInstructions.add(new LdcInsnNode(object));
                                bootstrapInstructions.add(IndyPreprocessor.getBoxingInsnNode(Type.FLOAT_TYPE));
                            } else if (object instanceof Double) {
                                bootstrapInstructions.add(new LdcInsnNode(object));
                                bootstrapInstructions.add(IndyPreprocessor.getBoxingInsnNode(Type.DOUBLE_TYPE));
                            } else if (object instanceof Handle) {
                                bootstrapInstructions.add(MethodHandleUtils.generateMethodHandleLdcInsn((Handle)object));
                            } else {
                                throw new RuntimeException("Wrong argument type: " + object.getClass());
                            }
                            bootstrapInstructions.add(new InsnNode(83));
                            ++index2;
                        }
                        continue;
                    }
                    throw new RuntimeException("Wrong argument type: " + bsmArgument.getClass());
                }
                bootstrapInstructions.add(new MethodInsnNode(184, invokeDynamicInsnNode.bsm.getOwner(), invokeDynamicInsnNode.bsm.getName(), invokeDynamicInsnNode.bsm.getDesc()));
                bootstrapInstructions.add(new TypeInsnNode(192, "java/lang/invoke/CallSite"));
                bootstrapInstructions.add(new MethodInsnNode(185, "java/lang/invoke/CallSite", "getTarget", "()Ljava/lang/invoke/MethodHandle;"));
                bootstrapInstructions.add(new JumpInsnNode(167, invokeStart));
                break;
            }
            case HOTSPOT: {
                bootstrapInstructions.add(new InsnNode(4));
                bootstrapInstructions.add(new TypeInsnNode(189, "java/lang/Object"));
                bootstrapInstructions.add(new InsnNode(89));
                bootstrapInstructions.add(new LdcInsnNode(Type.getObjectType(classNode.name)));
                bootstrapInstructions.add(new InsnNode(95));
                bootstrapInstructions.add(MethodHandleUtils.generateMethodHandleLdcInsn(invokeDynamicInsnNode.bsm));
                bootstrapInstructions.add(new InsnNode(95));
                bootstrapInstructions.add(new LdcInsnNode(invokeDynamicInsnNode.name));
                bootstrapInstructions.add(new InsnNode(95));
                bootstrapInstructions.add(MethodHandleUtils.generateMethodTypeLdcInsn(Type.getMethodType(invokeDynamicInsnNode.desc)));
                bootstrapInstructions.add(new InsnNode(95));
                bootstrapInstructions.add(new LdcInsnNode((Object)invokeDynamicInsnNode.bsmArgs.length));
                bootstrapInstructions.add(new TypeInsnNode(189, "java/lang/Object"));
                int index = 0;
                for (Object bsmArgument : invokeDynamicInsnNode.bsmArgs) {
                    bootstrapInstructions.add(new InsnNode(89));
                    bootstrapInstructions.add(new LdcInsnNode((Object)index));
                    if (bsmArgument instanceof String) {
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                    } else if (bsmArgument instanceof Type) {
                        if (((Type)bsmArgument).getSort() == 11) {
                            bootstrapInstructions.add(MethodHandleUtils.generateMethodTypeLdcInsn((Type)bsmArgument));
                        } else {
                            bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                        }
                    } else if (bsmArgument instanceof Integer) {
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                        bootstrapInstructions.add(IndyPreprocessor.getBoxingInsnNode(Type.INT_TYPE));
                    } else if (bsmArgument instanceof Long) {
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                        bootstrapInstructions.add(IndyPreprocessor.getBoxingInsnNode(Type.LONG_TYPE));
                    } else if (bsmArgument instanceof Float) {
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                        bootstrapInstructions.add(IndyPreprocessor.getBoxingInsnNode(Type.FLOAT_TYPE));
                    } else if (bsmArgument instanceof Double) {
                        bootstrapInstructions.add(new LdcInsnNode(bsmArgument));
                        bootstrapInstructions.add(IndyPreprocessor.getBoxingInsnNode(Type.DOUBLE_TYPE));
                    } else if (bsmArgument instanceof Handle) {
                        bootstrapInstructions.add(MethodHandleUtils.generateMethodHandleLdcInsn((Handle)bsmArgument));
                    } else {
                        throw new RuntimeException("Wrong argument type: " + bsmArgument.getClass());
                    }
                    bootstrapInstructions.add(new InsnNode(83));
                    ++index;
                }
                bootstrapInstructions.add(new TypeInsnNode(192, "java/lang/Object"));
                bootstrapInstructions.add(new InsnNode(95));
                bootstrapInstructions.add(PreprocessorUtils.LINK_CALL_SITE_METHOD.get());
                bootstrapInstructions.add(new InsnNode(87));
                bootstrapInstructions.add(new InsnNode(3));
                bootstrapInstructions.add(new InsnNode(50));
                bootstrapInstructions.add(new InsnNode(89));
                bootstrapInstructions.add(new TypeInsnNode(193, "java/lang/invoke/CallSite"));
                LabelNode methodHandleReady = new LabelNode(new Label());
                bootstrapInstructions.add(new JumpInsnNode(153, methodHandleReady));
                bootstrapInstructions.add(new MethodInsnNode(185, "java/lang/invoke/CallSite", "getTarget", "()Ljava/lang/invoke/MethodHandle;"));
                bootstrapInstructions.add(methodHandleReady);
                bootstrapInstructions.add(new TypeInsnNode(192, "java/lang/invoke/MethodHandle"));
                bootstrapInstructions.add(new JumpInsnNode(167, invokeStart));
            }
        }
        bootstrapInstructions.add(bootstrapEnd);
        InsnList invokeInstructions = new InsnList();
        invokeInstructions.add(invokeStart);
        switch (typesVMP) {
            case HOTSPOT: {
                invokeInstructions.add(PreprocessorUtils.INVOKE_REVERSE.apply(invokeDynamicInsnNode.desc));
                Type returnType = Type.getReturnType(invokeDynamicInsnNode.desc);
                if (returnType.getSort() == 10) {
                    invokeInstructions.add(new TypeInsnNode(192, returnType.getInternalName()));
                    break;
                }
                if (returnType.getSort() != 9) break;
                invokeInstructions.add(new TypeInsnNode(192, returnType.getDescriptor()));
                break;
            }
            case DEFAULT_JAVA: {
                invokeInstructions.add(new InsnNode(95));
                invokeInstructions.add(new MethodInsnNode(182, "java/lang/invoke/MethodHandle", "invokeWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;"));
                Type returnType = Type.getReturnType(invokeDynamicInsnNode.desc);
                if (returnType.getSort() == 10) {
                    invokeInstructions.add(new TypeInsnNode(192, returnType.getInternalName()));
                    break;
                }
                if (returnType.getSort() == 9) {
                    invokeInstructions.add(new TypeInsnNode(192, returnType.getDescriptor()));
                    break;
                }
                invokeInstructions.add(IndyPreprocessor.getUnboxingTypeInsn(returnType));
                break;
            }
        }
        InsnList bsmeInstructions = new InsnList();
        bsmeInstructions.add(bsmeStart);
        bsmeInstructions.add(new InsnNode(89));
        bsmeInstructions.add(new TypeInsnNode(193, "java/lang/BootstrapMethodError"));
        LabelNode throwLabel = new LabelNode(new Label());
        bsmeInstructions.add(new JumpInsnNode(154, throwLabel));
        bsmeInstructions.add(new TypeInsnNode(187, "java/lang/BootstrapMethodError"));
        bsmeInstructions.add(new InsnNode(89));
        bsmeInstructions.add(new InsnNode(93));
        bsmeInstructions.add(new InsnNode(88));
        bsmeInstructions.add(new MethodInsnNode(183, "java/lang/BootstrapMethodError", "<init>", "(Ljava/lang/Throwable;)V"));
        bsmeInstructions.add(throwLabel);
        bsmeInstructions.add(new InsnNode(191));
        InsnList resultInstructions = new InsnList();
        resultInstructions.add(bootstrapInstructions);
        resultInstructions.add(bsmeInstructions);
        resultInstructions.add(invokeInstructions);
        methodNode.instructions.insert((AbstractInsnNode)invokeDynamicInsnNode, resultInstructions);
        methodNode.instructions.remove(invokeDynamicInsnNode);
        methodNode.tryCatchBlocks.add(0, new TryCatchBlockNode(bootstrapStart, bootstrapEnd, bsmeStart, "java/lang/Throwable"));
    }

    private static AbstractInsnNode getBoxingInsnNode(Type argument) {
        switch (argument.getSort()) {
            case 1: {
                return new MethodInsnNode(184, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            }
            case 3: {
                return new MethodInsnNode(184, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            }
            case 2: {
                return new MethodInsnNode(184, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            }
            case 8: {
                return new MethodInsnNode(184, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            }
            case 6: {
                return new MethodInsnNode(184, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            }
            case 5: {
                return new MethodInsnNode(184, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            }
            case 7: {
                return new MethodInsnNode(184, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            }
            case 4: {
                return new MethodInsnNode(184, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            }
        }
        throw new RuntimeException(String.format("Failed to box %s", argument));
    }

    private static InsnList getUnboxingTypeInsn(Type argument) {
        InsnList result = new InsnList();
        switch (argument.getSort()) {
            case 1: {
                result.add(new TypeInsnNode(192, "java/lang/Boolean"));
                result.add(new MethodInsnNode(182, "java/lang/Boolean", "booleanValue", "()Z"));
                break;
            }
            case 3: {
                result.add(new TypeInsnNode(192, "java/lang/Byte"));
                result.add(new MethodInsnNode(182, "java/lang/Byte", "byteValue", "()B"));
                break;
            }
            case 2: {
                result.add(new TypeInsnNode(192, "java/lang/Character"));
                result.add(new MethodInsnNode(182, "java/lang/Character", "charValue", "()C"));
                break;
            }
            case 8: {
                result.add(new TypeInsnNode(192, "java/lang/Double"));
                result.add(new MethodInsnNode(182, "java/lang/Double", "doubleValue", "()D"));
                break;
            }
            case 6: {
                result.add(new TypeInsnNode(192, "java/lang/Float"));
                result.add(new MethodInsnNode(182, "java/lang/Float", "floatValue", "()F"));
                break;
            }
            case 5: {
                result.add(new TypeInsnNode(192, "java/lang/Integer"));
                result.add(new MethodInsnNode(182, "java/lang/Integer", "intValue", "()I"));
                break;
            }
            case 7: {
                result.add(new TypeInsnNode(192, "java/lang/Long"));
                result.add(new MethodInsnNode(182, "java/lang/Long", "longValue", "()J"));
                break;
            }
            case 4: {
                result.add(new TypeInsnNode(192, "java/lang/Short"));
                result.add(new MethodInsnNode(182, "java/lang/Short", "shortValue", "()S"));
                break;
            }
            case 0: {
                result.add(new InsnNode(87));
                break;
            }
            default: {
                throw new RuntimeException(String.format("Failed to unbox %s", argument));
            }
        }
        return result;
    }

    @Override
    public void process(ClassNode classNode, MethodNode methodNode, TypesVMP typesVMP) {
        for (int i = 0; i < methodNode.instructions.size(); ++i) {
            AbstractInsnNode insnNode = methodNode.instructions.get(i);
            if (!(insnNode instanceof InvokeDynamicInsnNode)) continue;
            IndyPreprocessor.processIndy(classNode, methodNode, (InvokeDynamicInsnNode)insnNode, typesVMP);
        }
    }
}

