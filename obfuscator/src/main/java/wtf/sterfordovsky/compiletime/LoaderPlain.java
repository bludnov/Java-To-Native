package wtf.sterfordovsky.compiletime;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class LoaderPlain {

    private static MethodHandle externalGuard;

    static {
        System.loadLibrary("%LIB_NAME%");
        try {
            MethodHandles.Lookup externalLookup = MethodHandles.lookup();
            externalGuard = externalLookup.findStatic(LoaderPlain.class, "registerExternalNative", MethodType.methodType(void.class, int.class, Class.class));
        } catch (NoSuchMethodException | IllegalAccessException aB) {
            throw new RuntimeException(aB);
        }
    }

    public static void registerExternalNative(int index, Class<?> clazz) {
        try {
            externalGuard.invokeExact(index, clazz);
        } catch (Throwable b) {
            throw new RuntimeException(b);
        }
    }

    public static native void registerNativesForClass(int index, Class<?> clazz);
}
