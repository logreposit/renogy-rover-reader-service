package com.logreposit.renogyrover.services.logreposit.dtos.ingress

import java.time.Instant

data class IngressData(
    val readings: List<Reading>
)

data class Reading(
    val date: Instant,
    val measurement: String,
    val tags: List<Tag>,
    val fields: List<Field>
)

data class Tag(
    val name: String,
    val value: String
)

sealed class Field(open val name: String, open val datatype: DataType)

data class FloatField(
    override val name: String,
    val value: Double
) : Field(name = name, datatype = DataType.FLOAT)

data class IntegerField(
    override val name: String,
    val value: Long
) : Field(name = name, datatype = DataType.INTEGER)

data class StringField(
    override val name: String,
    val value: String
) : Field(name = name, datatype = DataType.STRING)

enum class DataType {
    FLOAT,
    INTEGER,
    STRING
}
