/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode;

import wtf.sterfordovsky.runtime.TypesVMP;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public interface Preprocessor {
    public void process(ClassNode var1, MethodNode var2, TypesVMP var3);
}

