/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.builders.other;

import java.util.WeakHashMap;
import org.objectweb.asm.Label;

public class LabelPool {
    private final WeakHashMap<Label, Long> labels = new WeakHashMap();
    private long currentIndex = 0L;

    public String getName(Label label) {
        return "L" + this.labels.computeIfAbsent(label, addedLabel -> ++this.currentIndex);
    }
}

