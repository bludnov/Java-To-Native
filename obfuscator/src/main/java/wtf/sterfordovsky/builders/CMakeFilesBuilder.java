/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.builders;

import wtf.sterfordovsky.utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CMakeFilesBuilder {
    private final String projectName;
    private final List<String> classFiles;
    private final List<String> mainFiles;
    private final List<String> flags;

    public CMakeFilesBuilder(String projectName) {
        this.projectName = projectName;
        this.classFiles = new ArrayList<String>();
        this.mainFiles = new ArrayList<String>();
        this.flags = new ArrayList<String>();
    }

    public void addClassFile(String classFile) {
        this.classFiles.add(classFile);
    }

    public void addMainFile(String mainFile) {
        this.mainFiles.add(mainFile);
    }

    public void addFlag(String flag) {
        this.flags.add(flag);
    }

    public String build() {
        String template = Util.readResource("sources/CMakeLists.txt");
        return Util.dynamicFormat(template, Util.createMap("classfiles", String.join((CharSequence)" ", this.classFiles), "mainfiles", String.join((CharSequence)" ", this.mainFiles), "projectname", this.projectName, "definitions", this.flags.stream().map(flag -> String.format("-D%s=1", flag)).collect(Collectors.joining(" "))));
    }
}

