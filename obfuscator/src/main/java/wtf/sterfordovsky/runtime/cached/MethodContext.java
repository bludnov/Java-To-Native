/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.runtime.cached;

import wtf.sterfordovsky.builders.other.CatchesBlock;
import wtf.sterfordovsky.builders.other.LabelPool;
import wtf.sterfordovsky.runtime.methods.HiddenMethodsPool;
import wtf.sterfordovsky.builders.StringPool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import wtf.sterfordovsky.utils.NativeObfuscator;
import wtf.sterfordovsky.utils.Snippets;

public class MethodContext {
    public NativeObfuscator obfuscator;
    public final MethodNode method;
    public final ClassNode clazz;
    public final int methodIndex;
    public final int classIndex;
    public final StringBuilder output;
    public final StringBuilder nativeMethods;
    public Type ret;
    public ArrayList<Type> argTypes;
    public int line;
    public List<Integer> stack;
    public List<Integer> locals;
    public Set<TryCatchBlockNode> tryCatches;
    public Map<CatchesBlock, String> catches;
    public HiddenMethodsPool.HiddenMethod proxyMethod;
    public MethodNode nativeMethod;
    public int stackPointer;
    private final LabelPool labelPool = new LabelPool();
    public String cppNativeMethodName;

    public MethodContext(NativeObfuscator obfuscator, MethodNode method, int methodIndex, ClassNode clazz, int classIndex) {
        this.obfuscator = obfuscator;
        this.method = method;
        this.methodIndex = methodIndex;
        this.clazz = clazz;
        this.classIndex = classIndex;
        this.output = new StringBuilder();
        this.nativeMethods = new StringBuilder();
        this.line = -1;
        this.stack = new ArrayList<Integer>();
        this.locals = new ArrayList<Integer>();
        this.tryCatches = new HashSet<TryCatchBlockNode>();
        this.catches = new HashMap<CatchesBlock, String>();
    }

    public NodeCache<String> getCachedStrings() {
        return this.obfuscator.getCachedStrings();
    }

    public NodeCache<String> getCachedClasses() {
        return this.obfuscator.getCachedClasses();
    }

    public NodeCache<CachedMethodInfo> getCachedMethods() {
        return this.obfuscator.getCachedMethods();
    }

    public NodeCache<CachedFieldInfo> getCachedFields() {
        return this.obfuscator.getCachedFields();
    }

    public Snippets getSnippets() {
        return this.obfuscator.getSnippets();
    }

    public StringPool getStringPool() {
        return this.obfuscator.getStringPool();
    }

    public LabelPool getLabelPool() {
        return this.labelPool;
    }
}

