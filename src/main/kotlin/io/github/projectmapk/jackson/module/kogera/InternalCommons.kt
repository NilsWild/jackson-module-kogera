package io.github.projectmapk.jackson.module.kogera

import com.fasterxml.jackson.annotation.JsonCreator
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.isNullable
import kotlinx.metadata.jvm.JvmMethodSignature
import kotlinx.metadata.jvm.KotlinClassMetadata
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Constructor
import java.lang.reflect.Method

internal typealias JavaDuration = java.time.Duration
internal typealias KotlinDuration = kotlin.time.Duration

internal fun Class<*>.toKmClass(): KmClass? = getAnnotation(Metadata::class.java)?.let {
    (KotlinClassMetadata.readStrict(it) as KotlinClassMetadata.Class).kmClass
}

internal fun Class<*>.isUnboxableValueClass() = this.isAnnotationPresent(JvmInline::class.java)

// JmClass must be value class.
internal fun JmClass.wrapsNullValueClass() = inlineClassUnderlyingType!!.isNullable

private val primitiveClassToDesc = mapOf(
    Byte::class.java to 'B',
    Char::class.java to 'C',
    Double::class.java to 'D',
    Float::class.java to 'F',
    Int::class.java to 'I',
    Long::class.java to 'J',
    Short::class.java to 'S',
    Boolean::class.java to 'Z',
    Void.TYPE to 'V'
)

// -> this.name.replace(".", "/")
private fun Class<*>.descName(): String {
    val replaced = name.toCharArray().apply {
        for (i in indices) {
            if (this[i] == '.') this[i] = '/'
        }
    }
    return String(replaced)
}

private fun StringBuilder.appendDescriptor(clazz: Class<*>): StringBuilder = when {
    clazz.isPrimitive -> append(primitiveClassToDesc.getValue(clazz))
    clazz.isArray -> append('[').appendDescriptor(clazz.componentType)
    else -> append("L${clazz.descName()};")
}

// -> this.joinToString(separator = "", prefix = "(", postfix = ")") { it.descriptor }
internal fun Array<Class<*>>.toDescBuilder(): StringBuilder = this
    .fold(StringBuilder("(")) { acc, cur -> acc.appendDescriptor(cur) }
    .append(')')

internal fun Constructor<*>.toSignature(): JvmMethodSignature =
    JvmMethodSignature("<init>", parameterTypes.toDescBuilder().append('V').toString())

internal fun Method.toSignature(): JvmMethodSignature =
    JvmMethodSignature(this.name, parameterTypes.toDescBuilder().appendDescriptor(this.returnType).toString())

internal val defaultConstructorMarker: Class<*> by lazy {
    Class.forName("kotlin.jvm.internal.DefaultConstructorMarker")
}

// Kotlin-specific types such as kotlin.String will result in an error,
// but are ignored because they do not result in errors in internal use cases.
internal fun String.reconstructClass(): Class<*> {
    // -> this.replace(".", "$").replace("/", ".")
    val replaced = this.toCharArray().apply {
        for (i in indices) {
            val c = this[i]

            if (c == '.') {
                this[i] = '$'
            } else if (c == '/') {
                this[i] = '.'
            }
        }
    }

    return Class.forName(String(replaced))
}

internal fun KmType.reconstructClassOrNull(): Class<*>? = (classifier as? KmClassifier.Class)
    ?.let { kotlin.runCatching { it.name.reconstructClass() }.getOrNull() }

internal fun AnnotatedElement.hasCreatorAnnotation(): Boolean = getAnnotation(JsonCreator::class.java)
    ?.let { it.mode != JsonCreator.Mode.DISABLED }
    ?: false
