package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass

import io.github.projectmapk.jackson.module.kogera.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InheritanceTest {

    companion object {
        val mapper = jacksonObjectMapper()
    }

    @Test
    fun `array polymorphism should be used with polymorphic value class`(){
        val value = ValueClass(1)

        val json = mapper.writeValueAsString(value)
        val result = mapper.readValue(json, IValue::class.java)

        assertEquals(value, result)
    }

    @Test
    fun `array polymorphism should be used with polymorphic value class attributes`(){
        val value = WrappedValueClass(ValueClass(1))

        val json = mapper.writeValueAsString(value)
        val result = mapper.readValue(json, WrappedValueClass::class.java)

        assertEquals(value, result)
    }
}

data class WrappedValueClass(val value: ValueClass)