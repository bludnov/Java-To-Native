/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode;

import wtf.sterfordovsky.runtime.TypesVMP;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class PreprocessorRunner {
    private static final List<Preprocessor> PREPROCESSORS = new ArrayList<Preprocessor>();

    public static void preprocess(ClassNode classNode, MethodNode methodNode, TypesVMP typesVMP) {
        for (Preprocessor preprocessor : PREPROCESSORS) {
            preprocessor.process(classNode, methodNode, typesVMP);
        }
    }

    static {
        PREPROCESSORS.add(new IndyPreprocessor());
        PREPROCESSORS.add(new LdcPreprocessor());
    }
}

