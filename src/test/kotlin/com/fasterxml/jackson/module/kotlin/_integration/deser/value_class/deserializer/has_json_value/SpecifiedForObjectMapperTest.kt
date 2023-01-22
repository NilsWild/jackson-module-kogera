package com.fasterxml.jackson.module.kotlin._integration.deser.value_class.deserializer.has_json_value

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin._integration.deser.value_class.NonNullObject
import com.fasterxml.jackson.module.kotlin._integration.deser.value_class.NullableObject
import com.fasterxml.jackson.module.kotlin._integration.deser.value_class.Primitive
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SpecifiedForObjectMapperTest {
    companion object {
        val mapper = jacksonObjectMapper().apply {
            val module = SimpleModule().apply {
                this.addDeserializer(Primitive::class.java, Primitive.Deserializer())
                this.addDeserializer(NonNullObject::class.java, NonNullObject.Deserializer())
                this.addDeserializer(NullableObject::class.java, NullableObject.Deserializer())
            }
            this.registerModule(module)
        }
    }

    data class Dst(
        val pNn: Primitive,
        val pN: Primitive?,
        val nnoNn: NonNullObject,
        val nnoN: NonNullObject?,
        val noNn: NullableObject,
        val noN: NullableObject?
    )

    @Test
    fun nonNull() {
        val expected = Dst(
            Primitive(1),
            Primitive(2),
            NonNullObject("foo"),
            NonNullObject("bar"),
            NullableObject("baz"),
            NullableObject("qux")
        )
        val src = mapper.writeValueAsString(expected)
        val result = mapper.readValue<Dst>(src)

        assertEquals(expected, result)
    }

    // region Kogera42
    // After https://github.com/ProjectMapK/jackson-module-kogera/issues/42 is resolved, modify the test.
    data class WithoutNoNn(
        val pNn: Primitive,
        val pN: Primitive?,
        val nnoNn: NonNullObject,
        val nnoN: NonNullObject?,
        // val noNn: NullableObject,
        val noN: NullableObject?
    )

    @Test
    fun withNull() {
        val expected = WithoutNoNn(
            Primitive(1),
            null,
            NonNullObject("foo"),
            null,
            // NullableObject(null),
            null
        )
        val src = mapper.writeValueAsString(expected)
        val result = mapper.readValue<WithoutNoNn>(src)

        assertEquals(expected, result)
    }

    data class Tmp(val noNn: NullableObject)

    @Test
    fun temp() {
        val expected = Tmp(NullableObject(null))
        val src = mapper.writeValueAsString(expected)

        assertThrows<MissingKotlinParameterException>("Kogera #42 is fixed") {
            val result = mapper.readValue<Tmp>(src)
            assertEquals(expected, result)
        }
    }
    // endregion
}
