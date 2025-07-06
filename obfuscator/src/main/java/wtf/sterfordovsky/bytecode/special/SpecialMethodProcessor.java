/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.bytecode.special;

import wtf.sterfordovsky.runtime.cached.MethodContext;

public interface SpecialMethodProcessor {
    public String preProcess(MethodContext var1);

    public void postProcess(MethodContext var1);
}

