package com.logreposit.renogyrover.services.logreposit.mappers

import com.logreposit.renogyrover.communication.renogy.RenogyRamData
import com.logreposit.renogyrover.services.logreposit.dtos.ingress.IngressData

object LogrepositIngressDataMapper {
    fun toLogrepositIngressDto(data: RenogyRamData) = IngressData(
            readings = listOf() // TODO!
    )
}
