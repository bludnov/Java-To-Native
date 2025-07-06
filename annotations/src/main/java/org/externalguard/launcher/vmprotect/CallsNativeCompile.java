package org.externalguard.launcher.vmprotect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CallsNativeCompile {
    VMProtectType vmp = VMProtectType.VIRTUALIZATION;
}