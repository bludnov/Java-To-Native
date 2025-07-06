/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.runtime.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class HiddenMethodsPool {
    private final String baseName;
    private final HashMap<String, Integer> namePool = new HashMap();
    private final HashMap<String, HashMap<String, HiddenMethod>> methods = new HashMap();
    private final List<ClassNode> classes = new ArrayList<ClassNode>();

    public HiddenMethodsPool(String baseName) {
        this.baseName = baseName;
    }

    public HiddenMethod getMethod(String name, String desc, Consumer<MethodNode> creator) {
        HashMap<String, HiddenMethod> methodMap = (HashMap)this.methods.computeIfAbsent(name, (k) -> new HashMap());
        HiddenMethod existingMethod = (HiddenMethod)methodMap.get(desc);
        if (existingMethod != null) {
            return existingMethod;
        } else {
            String newName = name + this.namePool.compute(name, (k, v) -> v == null ? 0 : v + 1);
            MethodNode newMethod = new MethodNode(4169, newName, desc, (String)null, new String[0]);
            creator.accept(newMethod);
            ClassNode classNode = !this.classes.isEmpty() && ((ClassNode)this.classes.get(this.classes.size() - 1)).methods.size() <= 10000 ? (ClassNode)this.classes.get(this.classes.size() - 1) : this.createNewClassNode();
            classNode.methods.add(newMethod);
            HiddenMethod hiddenMethod = new HiddenMethod(classNode, newMethod);
            methodMap.put(desc, hiddenMethod);
            return hiddenMethod;
        }
    }

    private ClassNode createNewClassNode() {
        ClassNode classNode = new ClassNode(458752);
        classNode.access = 1;
        classNode.version = 52;
        classNode.name = this.baseName + "/Launcher" + this.classes.size();
        classNode.superName = Type.getInternalName(Object.class);
        this.classes.add(classNode);
        return classNode;
    }
    public List<ClassNode> getClasses() {
        return this.classes;
    }

    public static class HiddenMethod {
        private final ClassNode classNode;
        private final MethodNode methodNode;

        private HiddenMethod(ClassNode classNode, MethodNode methodNode) {
            this.classNode = classNode;
            this.methodNode = methodNode;
        }

        public ClassNode getClassNode() {
            return this.classNode;
        }

        public MethodNode getMethodNode() {
            return this.methodNode;
        }
    }
}

