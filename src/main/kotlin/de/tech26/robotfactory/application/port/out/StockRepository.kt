package de.tech26.robotfactory.application.port.out

import de.tech26.functional.Either
import de.tech26.robotfactory.application.domain.*

interface StockRepository {
    fun displayComponents(codes: List<Code>): Either<DomainError, List<Component>>
    fun subtractComponents(codes: List<Code>): Either<DomainError, List<Code>>
}