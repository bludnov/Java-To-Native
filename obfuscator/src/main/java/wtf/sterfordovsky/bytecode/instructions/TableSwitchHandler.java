/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import wtf.sterfordovsky.utils.Util;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.TableSwitchInsnNode;

public class TableSwitchHandler
extends GenericInstructionHandler<TableSwitchInsnNode> {
    @Override
    protected void process(MethodContext context, TableSwitchInsnNode node) {
        StringBuilder output = context.output;
        output.append(TableSwitchHandler.getStart(context)).append("\n    ");
        for (int i = 0; i < node.labels.size(); ++i) {
            output.append(String.format("    %s\n    ", TableSwitchHandler.getPart(context, node.min + i, node.labels.get(i).getLabel())));
        }
        output.append(String.format("    %s\n    ", TableSwitchHandler.getDefault(context, node.dflt.getLabel())));
        this.instructionName = "TABLESWITCH_END";
    }

    private static String getStart(MethodContext context) {
        return context.getSnippets().getSnippet("TABLESWITCH_START", Util.createMap("stackindexm1", String.valueOf(context.stackPointer - 1)));
    }

    private static String getPart(MethodContext context, int index, Label label) {
        return context.getSnippets().getSnippet("TABLESWITCH_PART", Util.createMap("index", index, "label", context.getLabelPool().getName(label)));
    }

    private static String getDefault(MethodContext context, Label label) {
        return context.getSnippets().getSnippet("TABLESWITCH_DEFAULT", Util.createMap("label", context.getLabelPool().getName(label)));
    }

    @Override
    public String insnToString(MethodContext context, TableSwitchInsnNode node) {
        return "TABLESWITCH";
    }

    @Override
    public int getNewStackPointer(TableSwitchInsnNode node, int currentStackPointer) {
        return currentStackPointer - 1;
    }
}

