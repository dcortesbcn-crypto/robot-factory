package de.tech26.robotfactory.application.port.`in`

import de.tech26.functional.Either
import de.tech26.robotfactory.application.domain.Order
import de.tech26.robotfactory.application.domain.DomainError
import de.tech26.robotfactory.application.domain.OrderRequest
import de.tech26.robotfactory.application.domain.RobotError

interface OrderPort {
    fun create(orderRequest: OrderRequest): Either<RobotError, Order>
}