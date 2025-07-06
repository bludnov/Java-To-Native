/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.runtime.cached;

import java.util.Objects;

public class CachedFieldInfo {
    private final String clazz;
    private final String name;
    private final String desc;
    private final boolean isStatic;

    public CachedFieldInfo(String clazz, String name, String desc, boolean isStatic) {
        this.clazz = clazz;
        this.name = name;
        this.desc = desc;
        this.isStatic = isStatic;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CachedFieldInfo that = (CachedFieldInfo)o;
        return this.isStatic == that.isStatic && this.clazz.equals(that.clazz) && this.name.equals(that.name) && this.desc.equals(that.desc);
    }

    public int hashCode() {
        return Objects.hash(this.clazz, this.name, this.desc, this.isStatic);
    }
}

