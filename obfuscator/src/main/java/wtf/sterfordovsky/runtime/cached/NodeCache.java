/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.runtime.cached;

import java.util.HashMap;
import java.util.Map;

public class NodeCache<T> {
    private final String pointerPattern;
    private final Map<T, Integer> cache;

    public NodeCache(String pointerPattern) {
        this.pointerPattern = pointerPattern;
        this.cache = new HashMap<T, Integer>();
    }

    public String getPointer(T key) {
        return String.format(this.pointerPattern, this.getId(key));
    }

    public int getId(T key) {
        if (!this.cache.containsKey(key)) {
            this.cache.put(key, this.cache.size());
        }
        return this.cache.get(key);
    }

    public int size() {
        return this.cache.size();
    }

    public boolean isEmpty() {
        return this.cache.isEmpty();
    }

    public Map<T, Integer> getCache() {
        return this.cache;
    }

    public void clear() {
        this.cache.clear();
    }
}

