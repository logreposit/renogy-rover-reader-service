package com.logreposit.renogyrover.services.logreposit.dtos.ingress

data class IngressDefinition(
    val measurements: List<MeasurementDefinition>
)

data class MeasurementDefinition(
    val name: String,
    val tags: Set<String>,
    val fields: List<FieldDefinition>
)

data class FieldDefinition(
    val name: String,
    val datatype: DataType,
    val description: String?
)
