package me.kinhiro.annotation

import java.lang.annotation.ElementType
import javax.annotation.Nonnull
import javax.annotation.meta.TypeQualifierDefault

@Nonnull
@TypeQualifierDefault(ElementType.METHOD)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class MethodsAreNonnullByDefault
