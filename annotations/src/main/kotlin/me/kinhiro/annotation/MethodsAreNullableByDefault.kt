package me.kinhiro.annotation

import java.lang.annotation.ElementType
import javax.annotation.Nullable
import javax.annotation.meta.TypeQualifierDefault

@Nullable
@TypeQualifierDefault(ElementType.METHOD)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class MethodsAreNullableByDefault
