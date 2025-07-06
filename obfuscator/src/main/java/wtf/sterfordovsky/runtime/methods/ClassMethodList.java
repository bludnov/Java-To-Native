/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.runtime.methods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class ClassMethodList {
    private static final String STAR_CHARACTER = ClassMethodList.quote("*");
    private final HashSet<String> staticEntries = new HashSet();
    private final List<Pattern> patterns = new ArrayList<Pattern>();

    private static String quote(String entry) {
        StringBuilder result = new StringBuilder();
        for (char c : entry.toCharArray()) {
            result.append(String.format("\\u%04x", c & 0xFFFF));
        }
        return result.toString();
    }

    private static Pattern parseAsPattern(String entry) {
        return Pattern.compile("^" + ClassMethodList.quote(entry).replaceAll(Pattern.quote(STAR_CHARACTER + STAR_CHARACTER), "(.*?)").replaceAll(Pattern.quote(STAR_CHARACTER), "([^/]*?)") + "$");
    }

    public static ClassMethodList parse(List<String> list) {
        if (list == null) {
            return null;
        }
        ClassMethodList resultList = new ClassMethodList();
        for (String entry : list) {
            if (entry.contains("*")) {
                resultList.patterns.add(ClassMethodList.parseAsPattern(entry));
                continue;
            }
            resultList.staticEntries.add(entry);
        }
        return resultList;
    }

    public boolean contains(String item) {
        if (this.staticEntries.contains(item)) {
            return true;
        }
        for (Pattern pattern : this.patterns) {
            if (!pattern.matcher(item).matches()) continue;
            return true;
        }
        return false;
    }
}

