/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import wtf.sterfordovsky.runtime.TypesVMP;
import wtf.sterfordovsky.utils.NativeObfuscator;

public class Main {
    private static final String VERSION = "3.5.4r";

    public static void main(String[] args) throws IOException {
        System.exit(new CommandLine(new NativeObfuscatorRunner()).setCaseInsensitiveEnumValuesAllowed(true).execute(args));
    }

    @CommandLine.Command(name="native-obfuscator", mixinStandardHelpOptions=true, version={"native-obfuscator 3.5.4r"}, description={"Transpiles .jar file into .cpp files and generates output .jar file"})
    private static class NativeObfuscatorRunner
    implements Callable<Integer> {
        @CommandLine.Parameters(index="0", description={"Jar file to transpile"})
        private File jarFile;
        @CommandLine.Parameters(index="1", description={"Output directory"})
        private String outputDirectory;
        @CommandLine.Option(names={"-l", "--libraries"}, description={"Directory for dependent libraries"})
        private File librariesDirectory;
        @CommandLine.Option(names={"-b", "--black-list"}, description={"File with a list of blacklist classes/methods for transpilation"})
        private File blackListFile;
        @CommandLine.Option(names={"-w", "--white-list"}, description={"File with a list of whitelist classes/methods for transpilation"})
        private File whiteListFile;
        @CommandLine.Option(names={"--plain-lib-name"}, description={"Plain library name for LoaderPlain"})
        private String libraryName;
        @CommandLine.Option(names={"--custom-lib-dir"}, description={"Custom library directory for LoaderUnpack"})
        private String customLibraryDirectory;
        @CommandLine.Option(names={"-p", "--platform"}, defaultValue="hotspot", description={"Target platform: hotspot - standard standalone HotSpot JRE, std_java - java standard, android - for Android builds (w/o DefineClass)"})
        private TypesVMP typesVMP;
        @CommandLine.Option(names={"-a", "--annotations"}, description={"Use annotations to ignore/include native obfuscation"})
        private boolean useAnnotations;
        @CommandLine.Option(names={"--debug"}, description={"Enable generation of debug .jar file (non-executable)"})
        private boolean generateDebugJar;

        private NativeObfuscatorRunner() {
        }

        @Override
        public Integer call() throws Exception {
            ArrayList<Path> libs = new ArrayList<Path>();
            if (this.librariesDirectory != null) {
                Files.walk(this.librariesDirectory.toPath(), FileVisitOption.FOLLOW_LINKS).filter(f -> f.toString().endsWith(".jar") || f.toString().endsWith(".zip")).forEach(libs::add);
            }
            ArrayList<String> blackList = new ArrayList();
            if (this.blackListFile != null) {
                blackList = (ArrayList<String>) Files.readAllLines(this.blackListFile.toPath(), StandardCharsets.UTF_8);
            }
            List<String> whiteList = null;
            if (this.whiteListFile != null) {
                whiteList = Files.readAllLines(this.whiteListFile.toPath(), StandardCharsets.UTF_8);
            }
            new NativeObfuscator().process(this.jarFile.toPath(), Paths.get(this.outputDirectory, new String[0]), libs, blackList, whiteList, this.libraryName, this.customLibraryDirectory, this.typesVMP, this.useAnnotations, this.generateDebugJar);
            return 0;
        }
    }
}

