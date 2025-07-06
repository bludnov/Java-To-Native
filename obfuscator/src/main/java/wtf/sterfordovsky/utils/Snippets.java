/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.utils;

import wtf.sterfordovsky.builders.StringPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class Snippets {
    private final Properties snippets;
    private final StringPool stringPool;

    public Snippets(StringPool stringPool) {
        this.stringPool = stringPool;
        this.snippets = new Properties();
        try {
            this.snippets.load(NativeObfuscator.class.getClassLoader().getResourceAsStream("sources/cppsnippets.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Can't load cpp snippets", e);
        }
    }

    private String[] getVars(String key) {
        String result = this.snippets.getProperty(key = key + "_S_VARS");
        if (result == null || result.isEmpty()) {
            return new String[0];
        }
        return result.split(",");
    }

    public String getSnippet(String key) {
        return this.getSnippet(key, Util.createMap(new Object[0]));
    }

    public String getSnippet(String key, Map<String, String> tokens) {
        String value = this.snippets.getProperty(key);
        Objects.requireNonNull(value, key);
        String[] stringVars = this.getVars(key);
        HashMap<String, String> result = new HashMap<String, String>();
        for (String var2 : stringVars) {
            if (var2.startsWith("#")) {
                result.put(var2, this.snippets.getProperty(key + "_S_CONST_" + var2.substring(1)));
                continue;
            }
            if (var2.startsWith("$")) {
                result.put(var2, tokens.get(var2.substring(1)));
                continue;
            }
            throw new RuntimeException("Unknown format modifier: " + var2);
        }
        result.entrySet().stream().filter(var -> var.getValue() == null).findAny().ifPresent(entry -> {
            throw new RuntimeException(key + " - token value can't be null");
        });
        result.entrySet().forEach(entry -> entry.setValue(this.stringPool.get((String)entry.getValue())));
        tokens.forEach((k, v) -> result.putIfAbsent("$" + k, (String)v));
        return Util.dynamicRawFormat(value, result);
    }
}

