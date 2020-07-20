package com.github.mar9000.graphloader.test.resources;

import com.github.mar9000.graphloader.loader.ExecutionContext;

import java.util.Locale;

/**
 * @author ML
 * @since 1.0.0
 */
public class LocaleExecutionContext implements ExecutionContext {
    public final Locale locale;
    public LocaleExecutionContext(Locale locale) {
        this.locale = locale;
    }
}
