/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.builders.other.CatchesBlock;
import wtf.sterfordovsky.runtime.cached.MethodContext;
import wtf.sterfordovsky.runtime.cached.MethodProcessor;
import wtf.sterfordovsky.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

public abstract class GenericInstructionHandler<T extends AbstractInsnNode>
implements InstructionTypeHandler<T> {
    protected Map<String, String> props;
    protected String instructionName;
    protected String trimmedTryCatchBlock;

    @Override
    public void accept(MethodContext context, T node) {
        this.props = new HashMap<String, String>();
        ArrayList<TryCatchBlockNode> tryCatchBlockNodeList = new ArrayList<TryCatchBlockNode>();
        for (TryCatchBlockNode tryCatchBlock : context.method.tryCatchBlocks) {
            if (!context.tryCatches.contains(tryCatchBlock) || !tryCatchBlockNodeList.stream().noneMatch(tryCatchBlockNode -> Objects.equals(tryCatchBlockNode.type, tryCatchBlock.type))) continue;
            tryCatchBlockNodeList.add(tryCatchBlock);
        }
        this.instructionName = MethodProcessor.INSTRUCTIONS.getOrDefault(((AbstractInsnNode)node).getOpcode(), "NOTFOUND");
        this.props.put("line", String.valueOf(context.line));
        StringBuilder tryCatch = new StringBuilder("\n");
        tryCatch.append("    ");
        if (tryCatchBlockNodeList.size() > 0) {
            String tryCatchLabelName = context.catches.computeIfAbsent(new CatchesBlock(tryCatchBlockNodeList.stream().map(item -> new CatchesBlock.CatchBlock(item.type, item.handler)).collect(Collectors.toList())), key -> String.format("L_CATCH_%d", context.catches.size()));
            tryCatch.append(context.getSnippets().getSnippet("TRYCATCH_START"));
            tryCatch.append(" goto ").append(tryCatchLabelName).append("; }");
        } else {
            tryCatch.append(context.getSnippets().getSnippet("TRYCATCH_EMPTY", Util.createMap("rettype", MethodProcessor.CPP_TYPES[context.ret.getSort()])));
        }
        this.props.put("trycatchhandler", tryCatch.toString());
        this.props.put("rettype", MethodProcessor.CPP_TYPES[context.ret.getSort()]);
        this.trimmedTryCatchBlock = tryCatch.toString().trim().replace('\n', ' ');
        for (int i = -5; i <= 5; ++i) {
            this.props.put("stackindex" + (i >= 0 ? Integer.valueOf(i) : "m" + -i), String.valueOf(context.stackPointer + i));
        }
        context.output.append("    ");
        this.process(context, node);
        if (this.instructionName != null) {
            context.output.append(context.obfuscator.getSnippets().getSnippet(this.instructionName, this.props));
        }
        context.output.append("\n");
    }

    protected abstract void process(MethodContext var1, T var2);
}

