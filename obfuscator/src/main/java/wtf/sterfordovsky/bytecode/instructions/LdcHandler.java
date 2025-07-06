/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import wtf.sterfordovsky.runtime.cached.MethodProcessor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LdcInsnNode;

public class LdcHandler
extends GenericInstructionHandler<LdcInsnNode> {
    public static String getIntString(int value) {
        return value == Integer.MIN_VALUE ? "(jint) 2147483648U" : String.valueOf(value);
    }

    public static String getLongValue(long value) {
        return value == Long.MIN_VALUE ? "(jlong) 9223372036854775808ULL" : String.valueOf(value) + "LL";
    }

    public static String getFloatValue(float value) {
        if (Float.isNaN(value)) {
            return "NAN";
        }
        if (value == Float.POSITIVE_INFINITY) {
            return "HUGE_VALF";
        }
        if (value == Float.NEGATIVE_INFINITY) {
            return "-HUGE_VALF";
        }
        return value + "f";
    }

    public static String getDoubleValue(double value) {
        if (Double.isNaN(value)) {
            return "NAN";
        }
        if (value == Double.POSITIVE_INFINITY) {
            return "HUGE_VAL";
        }
        if (value == Double.NEGATIVE_INFINITY) {
            return "-HUGE_VAL";
        }
        return String.valueOf(value);
    }

    @Override
    protected void process(MethodContext context, LdcInsnNode node) {
        Object cst = node.cst;
        if (cst instanceof String) {
            this.instructionName = this.instructionName + "_STRING";
            this.props.put("cst_ptr", context.getCachedStrings().getPointer(node.cst.toString()));
        } else if (cst instanceof Integer) {
            this.instructionName = this.instructionName + "_INT";
            this.props.put("cst", LdcHandler.getIntString((Integer)cst));
        } else if (cst instanceof Long) {
            this.instructionName = this.instructionName + "_LONG";
            this.props.put("cst", LdcHandler.getLongValue((Long)cst));
        } else if (cst instanceof Float) {
            this.instructionName = this.instructionName + "_FLOAT";
            this.props.put("cst", LdcHandler.getFloatValue(((Float)node.cst).floatValue()));
        } else if (cst instanceof Double) {
            this.instructionName = this.instructionName + "_DOUBLE";
            this.props.put("cst", LdcHandler.getDoubleValue((Double)node.cst));
        } else if (cst instanceof Type) {
            this.instructionName = this.instructionName + "_CLASS";
            int classId = context.getCachedClasses().getId(node.cst.toString());
            context.output.append(String.format("if (!cclasses[%d] || env->IsSameObject(cclasses[%d], NULL)) { cclasses_mtx[%d].lock(); if (!cclasses[%d] || env->IsSameObject(cclasses[%d], NULL)) { if (jclass clazz = %s) { cclasses[%d] = (jclass) env->NewWeakGlobalRef(clazz); env->DeleteLocalRef(clazz); } } cclasses_mtx[%d].unlock(); %s } ", classId, classId, classId, classId, classId, MethodProcessor.getClassGetter(context, node.cst.toString()), classId, classId, this.trimmedTryCatchBlock));
            this.props.put("cst_ptr", context.getCachedClasses().getPointer(node.cst.toString()));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String insnToString(MethodContext context, LdcInsnNode node) {
        return String.format("LDC %s", node.cst);
    }

    @Override
    public int getNewStackPointer(LdcInsnNode node, int currentStackPointer) {
        if (node.cst instanceof Double || node.cst instanceof Long) {
            return currentStackPointer + 2;
        }
        return currentStackPointer + 1;
    }
}

