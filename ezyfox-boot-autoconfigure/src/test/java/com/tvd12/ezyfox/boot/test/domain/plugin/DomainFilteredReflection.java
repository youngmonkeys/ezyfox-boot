package com.tvd12.ezyfox.boot.test.domain.plugin;

import com.tvd12.ezyfox.io.EzySets;
import com.tvd12.ezyfox.reflect.EzyReflection;
import lombok.AllArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;

@AllArgsConstructor
public class DomainFilteredReflection implements EzyReflection {

    private final EzyReflection reflection;
    private final Predicate<Class<?>> filter;

    @Override
    public Set<Class<?>> getExtendsClasses(Class<?> parentClass) {
        return EzySets.filter(reflection.getExtendsClasses(parentClass), filter);
    }

    @Override
    public Set<Class<?>> getAnnotatedClasses(
        Class<? extends Annotation> annotationClass
    ) {
        return EzySets.filter(
            reflection.getAnnotatedClasses(annotationClass),
            filter
        );
    }
}
