package org.externalguard.launcher.vmprotect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VMProtect {
    VMProtectType type();
}
