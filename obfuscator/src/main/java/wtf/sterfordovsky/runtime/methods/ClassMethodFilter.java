/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.runtime.methods;

import org.externalguard.launcher.enums.NativeExclude;
import org.externalguard.launcher.enums.NativeInclude;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import wtf.sterfordovsky.runtime.cached.MethodProcessor;

public class ClassMethodFilter {
    private static final String NATIVE_ANNOTATION_DESC = Type.getDescriptor(NativeInclude.class);
    private static final String NOT_NATIVE_ANNOTATION_DESC = Type.getDescriptor(NativeExclude.class);
    private final ClassMethodList blackList;
    private final ClassMethodList whiteList;
    private final boolean useAnnotations;

    public ClassMethodFilter(ClassMethodList blackList, ClassMethodList whiteList, boolean useAnnotations) {
        this.blackList = blackList;
        this.whiteList = whiteList;
        this.useAnnotations = useAnnotations;
    }

    private boolean hasInList(ClassMethodList list, String name) {
        if (list == null) {
            return false;
        }
        return list.contains(name);
    }

        public boolean shouldProcess(ClassNode classNode) {
            if (this.hasInList(this.blackList, classNode.name)) {
                return false;
            }
            if (this.whiteList != null && !this.hasInList(this.whiteList, classNode.name)) {
                return false;
            }
            if (!this.useAnnotations) {
                return true;
            }
            if (classNode.invisibleAnnotations != null && classNode.invisibleAnnotations.stream().anyMatch(annotationNode -> annotationNode.desc.equals(NATIVE_ANNOTATION_DESC))) {
                return true;
            }
            return classNode.methods.stream().anyMatch(methodNode -> this.shouldProcess(classNode, (MethodNode)methodNode));
        }

    public boolean shouldProcess(ClassNode classNode, MethodNode methodNode) {
        boolean classIsMarked;
        if (this.hasInList(this.blackList, MethodProcessor.nameFromNode(methodNode, classNode))) {
            return false;
        }
        if (this.whiteList != null && !this.hasInList(this.whiteList, MethodProcessor.nameFromNode(methodNode, classNode))) {
            return false;
        }
        if (!this.useAnnotations) {
            return true;
        }
        boolean bl = classIsMarked = classNode.invisibleAnnotations != null && classNode.invisibleAnnotations.stream().anyMatch(annotationNode -> annotationNode.desc.equals(NATIVE_ANNOTATION_DESC));
        if (methodNode.invisibleAnnotations != null && methodNode.invisibleAnnotations.stream().anyMatch(annotationNode -> annotationNode.desc.equals(NATIVE_ANNOTATION_DESC))) {
            return true;
        }
        return classIsMarked && (methodNode.invisibleAnnotations == null || methodNode.invisibleAnnotations.stream().noneMatch(annotationNode -> annotationNode.desc.equals(NOT_NATIVE_ANNOTATION_DESC)));
    }

    public static void cleanAnnotations(ClassNode classNode) {
        if (classNode.invisibleAnnotations != null) {
            classNode.invisibleAnnotations.removeIf(annotationNode -> annotationNode.desc.equals(NATIVE_ANNOTATION_DESC));
        }
        classNode.methods.stream().filter(methodNode -> methodNode.invisibleAnnotations != null).forEach(methodNode -> methodNode.invisibleAnnotations.removeIf(annotationNode -> annotationNode.desc.equals(NATIVE_ANNOTATION_DESC) || annotationNode.desc.equals(NOT_NATIVE_ANNOTATION_DESC)));
    }
}

