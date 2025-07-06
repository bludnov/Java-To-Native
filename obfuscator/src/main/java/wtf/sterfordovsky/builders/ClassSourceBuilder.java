/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.builders;

import wtf.sterfordovsky.compiletime.HiddenCppMethod;
import wtf.sterfordovsky.runtime.cached.NodeCache;
import wtf.sterfordovsky.utils.Util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.tree.ClassNode;

public class ClassSourceBuilder
implements AutoCloseable {
    private final Path cppFile;
    private final Path hppFile;
    private final BufferedWriter cppWriter;
    private final BufferedWriter hppWriter;
    private final String className;
    private final String filename;
    private final StringPool stringPool;

    public ClassSourceBuilder(Path cppOutputDir, String className, int classIndex, StringPool stringPool) throws IOException {
        this.className = className;
        this.stringPool = stringPool;
        this.filename = String.format("%s_%d", Util.escapeCppNameString(className.replace('/', '_')), classIndex);
        this.cppFile = cppOutputDir.resolve(this.filename.concat(".cpp"));
        this.hppFile = cppOutputDir.resolve(this.filename.concat(".hpp"));
        this.cppWriter = Files.newBufferedWriter(this.cppFile, StandardCharsets.UTF_8, new OpenOption[0]);
        this.hppWriter = Files.newBufferedWriter(this.hppFile, StandardCharsets.UTF_8, new OpenOption[0]);
    }

    public void addHeader(int strings, int classes, int methods, int fields) throws IOException {
        this.cppWriter.append("#include \"../native_jvm.hpp\"\n");
        this.cppWriter.append("#include \"../string_pool.hpp\"\n");
        this.cppWriter.append("#include \"").append(this.getHppFilename()).append("\"\n");
        this.cppWriter.append("\n");
        this.cppWriter.append("// ").append(Util.escapeCommentString(this.className)).append("\n");
        this.cppWriter.append("namespace native_jvm::classes::__ngen_").append(this.filename).append(" {\n\n");
        this.cppWriter.append("    char *string_pool;\n\n");
        if (strings > 0) {
            this.cppWriter.append(String.format("    jstring cstrings[%d];\n", strings));
        }
        if (classes > 0) {
            this.cppWriter.append(String.format("    std::mutex cclasses_mtx[%d];\n", classes));
            this.cppWriter.append(String.format("    jclass cclasses[%d];\n", classes));
        }
        if (methods > 0) {
            this.cppWriter.append(String.format("    jmethodID cmethods[%d];\n", methods));
        }
        if (fields > 0) {
            this.cppWriter.append(String.format("    jfieldID cfields[%d];\n", fields));
        }
        this.cppWriter.append("\n");
        this.cppWriter.append("    ");
        this.hppWriter.append("#include \"../native_jvm.hpp\"\n");
        this.hppWriter.append("\n");
        this.hppWriter.append("#ifndef ").append(this.filename.concat("_hpp").toUpperCase()).append("_GUARD\n");
        this.hppWriter.append("\n");
        this.hppWriter.append("#define ").append(this.filename.concat("_hpp").toUpperCase()).append("_GUARD\n");
        this.hppWriter.append("\n");
        this.hppWriter.append("// ").append(Util.escapeCommentString(this.className)).append("\n");
        this.hppWriter.append("namespace native_jvm::classes::__ngen_").append(this.filename).append(" {\n\n");
    }

    public void addInstructions(String instructions) throws IOException {
        this.cppWriter.append(instructions);
        this.cppWriter.append("\n");
    }

    public void registerMethods(NodeCache<String> strings, NodeCache<String> classes, String nativeMethods, List<HiddenCppMethod> hiddenMethods) throws IOException {
        this.cppWriter.append("    void __ngen_register_methods(JNIEnv *env, jclass clazz) {\n");
        this.cppWriter.append("        string_pool = string_pool::get_pool();\n\n");
        for (Map.Entry<String, Integer> string : strings.getCache().entrySet()) {
            this.cppWriter.append("        if (jstring str = env->NewStringUTF(").append(this.stringPool.get(string.getKey())).append(")) { if (jstring int_str = utils::get_interned(env, str)) { ").append(String.format("cstrings[%d] = ", string.getValue())).append("(jstring) env->NewGlobalRef(int_str); env->DeleteLocalRef(str); env->DeleteLocalRef(int_str); } }\n");
        }
        if (!classes.isEmpty()) {
            this.cppWriter.append("\n");
        }
        if (!nativeMethods.isEmpty()) {
            this.cppWriter.append("        JNINativeMethod __ngen_methods[] = {\n");
            this.cppWriter.append(nativeMethods);
            this.cppWriter.append("        };\n\n");
            this.cppWriter.append("        if (clazz) env->RegisterNatives(clazz, __ngen_methods, sizeof(__ngen_methods) / sizeof(__ngen_methods[0]));\n");
            this.cppWriter.append("        if (env->ExceptionCheck()) { fprintf(stderr, \"Exception occured while registering native_jvm for %s\\n\", ").append(this.stringPool.get(this.className.replace('/', '.'))).append("); fflush(stderr); env->ExceptionDescribe(); env->ExceptionClear(); }\n");
            this.cppWriter.append("\n");
        }
        if (!hiddenMethods.isEmpty()) {
            HashMap<ClassNode, List<HiddenCppMethod>> sortedHiddenMethods = new HashMap<>();
            for (HiddenCppMethod method : hiddenMethods) {
                sortedHiddenMethods.computeIfAbsent(method.getHiddenMethod().getClassNode(), unused -> new ArrayList<>()).add(method);
            }

            for (ClassNode hiddenClazz : sortedHiddenMethods.keySet()) {
                cppWriter.append("        {\n");
                cppWriter.append("            jclass hidden_class = env->FindClass(").append(stringPool.get(hiddenClazz.name)).append(");\n");
                cppWriter.append("            JNINativeMethod __ngen_hidden_methods[] = {\n");
                for (HiddenCppMethod method : sortedHiddenMethods.get(hiddenClazz)) {
                    cppWriter.append(String.format("                { %s, %s, (void *)&%s },\n",
                            stringPool.get(method.getHiddenMethod().getMethodNode().name),
                            stringPool.get(method.getHiddenMethod().getMethodNode().desc),
                            method.getCppName()));
                }
                cppWriter.append("            };\n");
                cppWriter.append("            if (hidden_class) env->RegisterNatives(hidden_class, __ngen_hidden_methods, sizeof(__ngen_hidden_methods) / sizeof(__ngen_hidden_methods[0]));\n");
                cppWriter.append("            if (env->ExceptionCheck()) { fprintf(stderr, \"Exception occured while registering native_jvm for %s\\n\", ")
                        .append(stringPool.get(hiddenClazz.name.replace('/', '.')))
                        .append("); fflush(stderr); env->ExceptionDescribe(); env->ExceptionClear(); }\n");
                cppWriter.append("            env->DeleteLocalRef(hidden_class);\n");
                cppWriter.append("        }\n");

            }
        }
        this.cppWriter.append("    }\n");
        this.cppWriter.append("}");
        this.hppWriter.append("    void __ngen_register_methods(JNIEnv *env, jclass clazz);\n");
        this.hppWriter.append("}\n\n#endif");
    }

    public String getFilename() {
        return this.filename;
    }

    public String getHppFilename() {
        return this.hppFile.getFileName().toString();
    }

    public String getCppFilename() {
        return this.cppFile.getFileName().toString();
    }

    @Override
    public void close() throws IOException {
        try {
            this.cppWriter.close();
        } finally {
            this.hppWriter.close();
        }
    }
}

