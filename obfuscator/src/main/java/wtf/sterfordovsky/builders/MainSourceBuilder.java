/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.builders;

import wtf.sterfordovsky.utils.Util;

public class MainSourceBuilder {
    private final StringBuilder includes = new StringBuilder();
    private final StringBuilder registerMethods = new StringBuilder();

    public void addHeader(String hppFilename) {
        this.includes.append(String.format("#include \"output/%s\"\n", hppFilename));
    }

    public void registerClassMethods(int classId, String escapedClassName) {
        this.registerMethods.append(String.format("        reg_methods[%d] = &(native_jvm::classes::__ngen_%s::__ngen_register_methods);\n", classId, escapedClassName));
    }

    public void registerDefine(String stringPooledClassName, String classFileName) {
        this.registerMethods.append(String.format("        env->DeleteLocalRef(env->DefineClass(%s, nullptr, native_jvm::data::__ngen_%s::get_class_data(), native_jvm::data::__ngen_%s::get_class_data_length()));\n", stringPooledClassName, classFileName, classFileName));
    }

    public String build(String nativeDir, int classCount) {
        String template = Util.readResource("sources/native_jvm_output.cpp");
        return Util.dynamicFormat(template, Util.createMap("register_code", this.registerMethods, "includes", this.includes, "native_dir", nativeDir, "class_count", Math.max(1, classCount)));
    }
}

