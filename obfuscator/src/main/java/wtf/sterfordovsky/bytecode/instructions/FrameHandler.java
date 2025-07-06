/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;
import wtf.sterfordovsky.runtime.cached.MethodProcessor;
import wtf.sterfordovsky.utils.Util;

import java.util.Arrays;
import java.util.function.Consumer;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LabelNode;

public class FrameHandler
implements InstructionTypeHandler<FrameNode> {
    @Override
    public void accept(MethodContext context, FrameNode node) {
        Consumer<Object> appendLocal = local -> {
            if (local instanceof String) {
                context.locals.add(MethodProcessor.TYPE_TO_STACK[10]);
            } else if (local instanceof LabelNode) {
                context.locals.add(MethodProcessor.TYPE_TO_STACK[10]);
            } else {
                context.locals.add(MethodProcessor.STACK_TO_STACK[(Integer)local]);
            }
        };
        Consumer<Object> appendStack = stack -> {
            if (stack instanceof String) {
                context.stack.add(MethodProcessor.TYPE_TO_STACK[10]);
            } else if (stack instanceof LabelNode) {
                context.stack.add(MethodProcessor.TYPE_TO_STACK[10]);
            } else {
                context.stack.add(MethodProcessor.STACK_TO_STACK[(Integer)stack]);
            }
        };
        switch (node.type) {
            case 1: {
                node.local.forEach(appendLocal);
                context.stack.clear();
                break;
            }
            case 2: {
                node.local.forEach(item -> context.locals.remove(context.locals.size() - 1));
                context.stack.clear();
                break;
            }
            case -1: 
            case 0: {
                context.locals.clear();
                context.stack.clear();
                node.local.forEach(appendLocal);
                node.stack.forEach(appendStack);
                break;
            }
            case 3: {
                context.stack.clear();
                break;
            }
            case 4: {
                context.stack.clear();
                appendStack.accept(node.stack.get(0));
            }
        }
        if (context.stack.stream().anyMatch(x -> x == 0)) {
            int currentSp = 0;
            context.output.append("    ");
            for (int type : context.stack) {
                if (type == 0) {
                    context.output.append("refs.erase(cstack").append(currentSp).append(".l); ");
                }
                currentSp += Math.max(1, type);
            }
            context.output.append("\n");
        }
        if (context.locals.stream().anyMatch(x -> x == 0)) {
            int currentLp = 0;
            context.output.append("    ");
            for (int type : context.locals) {
                if (type == 0) {
                    context.output.append("refs.erase(clocal").append(currentLp).append(".l); ");
                }
                currentLp += Math.max(1, type);
            }
            context.output.append("\n");
        }
        context.output.append("    utils::clear_refs(env, refs);\n");
    }

    @Override
    public String insnToString(MethodContext context, FrameNode node) {
        return String.format("FRAME %s L: %s S: %s", Util.getOpcodesString(node.type, "F_"), node.local == null ? "null" : Arrays.toString(node.local.toArray(new Object[0])), node.stack == null ? "null" : Arrays.toString(node.stack.toArray(new Object[0])));
    }

    @Override
    public int getNewStackPointer(FrameNode node, int currentStackPointer) {
        switch (node.type) {
            case 1: 
            case 2: 
            case 3: {
                return 0;
            }
            case -1: 
            case 0: {
                return node.stack.stream().mapToInt(argument -> Math.max(1, argument instanceof Integer ? MethodProcessor.STACK_TO_STACK[(Integer)argument] : MethodProcessor.TYPE_TO_STACK[10])).sum();
            }
            case 4: {
                return node.stack.stream().limit(1L).mapToInt(argument -> Math.max(1, argument instanceof Integer ? MethodProcessor.STACK_TO_STACK[(Integer)argument] : MethodProcessor.TYPE_TO_STACK[10])).sum();
            }
        }
        throw new RuntimeException();
    }
}

