/*
 * This software is released under the BSD license. Full license available at http://xmappr.googlecode.com
 *
 * Copyright (c) 2008, 2009, Peter Knego & Xmappr contributors
 * All rights reserved.
 */
package org.xmappr;

import org.xmappr.converters.ValueConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to map a field to XML text elements.
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Text {

    /**
     * Custom converter assigned to convert this field.
     *
     * @return
     */
    Class<? extends ValueConverter> converter() default ValueConverter.class;

    /**
     * Optional formatting string. Used by converter.
     *
     * @return
     */
    String format() default "";
}