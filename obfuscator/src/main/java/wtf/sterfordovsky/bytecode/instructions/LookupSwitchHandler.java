/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import wtf.sterfordovsky.utils.Util;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LookupSwitchInsnNode;

public class LookupSwitchHandler
extends GenericInstructionHandler<LookupSwitchInsnNode> {
    @Override
    protected void process(MethodContext context, LookupSwitchInsnNode node) {
        StringBuilder output = context.output;
        output.append(LookupSwitchHandler.getStart(context)).append("\n    ");
        for (int i = 0; i < node.labels.size(); ++i) {
            output.append(String.format("    %s\n    ", LookupSwitchHandler.getPart(context, node.keys.get(i), node.labels.get(i).getLabel())));
        }
        output.append(String.format("    %s\n    ", LookupSwitchHandler.getDefault(context, node.dflt.getLabel())));
        this.instructionName = "LOOKUPSWITCH_END";
    }

    private static String getStart(MethodContext context) {
        return context.getSnippets().getSnippet("LOOKUPSWITCH_START", Util.createMap("stackindexm1", String.valueOf(context.stackPointer - 1)));
    }

    private static String getPart(MethodContext context, int key, Label label) {
        return context.getSnippets().getSnippet("LOOKUPSWITCH_PART", Util.createMap("key", key, "label", context.getLabelPool().getName(label)));
    }

    private static String getDefault(MethodContext context, Label label) {
        return context.getSnippets().getSnippet("LOOKUPSWITCH_DEFAULT", Util.createMap("label", context.getLabelPool().getName(label)));
    }

    @Override
    public String insnToString(MethodContext context, LookupSwitchInsnNode node) {
        return "LOOKUPSWITCH";
    }

    @Override
    public int getNewStackPointer(LookupSwitchInsnNode node, int currentStackPointer) {
        return currentStackPointer - 1;
    }
}

