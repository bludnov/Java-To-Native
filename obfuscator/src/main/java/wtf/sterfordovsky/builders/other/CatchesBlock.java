/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.builders.other;

import java.util.List;
import java.util.Objects;
import org.objectweb.asm.tree.LabelNode;

public class CatchesBlock {
    private final List<CatchBlock> catches;

    public CatchesBlock(List<CatchBlock> catches) {
        this.catches = catches;
    }

    public List<CatchBlock> getCatches() {
        return this.catches;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CatchesBlock that = (CatchesBlock)o;
        return Objects.equals(this.catches, that.catches);
    }

    public int hashCode() {
        return Objects.hash(this.catches);
    }

    public static class CatchBlock {
        private final String clazz;
        private final LabelNode handler;

        public CatchBlock(String clazz, LabelNode handler) {
            this.clazz = clazz;
            this.handler = handler;
        }

        public String getClazz() {
            return this.clazz;
        }

        public LabelNode getHandler() {
            return this.handler;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CatchBlock that = (CatchBlock)o;
            return Objects.equals(this.clazz, that.clazz) && Objects.equals(this.handler, that.handler);
        }

        public int hashCode() {
            return Objects.hash(this.clazz, this.handler);
        }
    }
}

