/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.instructions;

import wtf.sterfordovsky.runtime.cached.MethodContext;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;

public class MultiANewArrayHandler
extends GenericInstructionHandler<MultiANewArrayInsnNode> {
    @Override
    protected void process(MethodContext context, MultiANewArrayInsnNode node) {
        Type elementType = Type.getType(node.desc).getElementType();
        this.props.put("required_count", String.valueOf(node.dims));
        int dimensions = node.dims;
        this.props.put("count", String.valueOf(Type.getType(node.desc).getDimensions()));
        this.props.put("desc", elementType.getInternalName());
        if (elementType.getSort() != 10) {
            this.props.put("sort", String.valueOf(elementType.getSort()));
            this.instructionName = "MULTIANEWARRAY_VALUE";
        }
        this.props.put("dims", String.format("{ %s }", IntStream.range(context.stackPointer - dimensions, context.stackPointer).mapToObj(i -> String.format("cstack%d.i", i)).collect(Collectors.joining(", "))));
        this.props.put("returnstackindex", String.valueOf(context.stackPointer - dimensions));
    }

    @Override
    public String insnToString(MethodContext context, MultiANewArrayInsnNode node) {
        return String.format("MULTIANEWARRAY %d %s", node.dims, node.desc);
    }

    @Override
    public int getNewStackPointer(MultiANewArrayInsnNode node, int currentStackPointer) {
        return currentStackPointer - node.dims + 1;
    }
}

