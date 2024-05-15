package io.github.projectmapk.jackson.module.kogera.zIntegration.deser.valueClass

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
sealed interface IValue

sealed interface IValue2: IValue

@JvmInline
value class ValueClass(val value: Int?) : IValue2

@JvmInline
value class AnotherValueClass( val value: Int?) : IValue2