/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sterfordovsky.compiletime;

import wtf.sterfordovsky.runtime.methods.HiddenMethodsPool;

public class HiddenCppMethod {

    private final HiddenMethodsPool.HiddenMethod hiddenMethod;

    private final String cppName;

    public HiddenCppMethod(HiddenMethodsPool.HiddenMethod hiddenMethod, String cppName) {
        this.hiddenMethod = hiddenMethod;
        this.cppName = cppName;
    }

    public HiddenMethodsPool.HiddenMethod getHiddenMethod() {
        return hiddenMethod;
    }

    public String getCppName() {
        return cppName;
    }
}


