package de.tech26.robotfactory.adapter.out.repository

import de.tech26.functional.Either
import de.tech26.functional.Left
import de.tech26.functional.Right
import de.tech26.robotfactory.application.domain.*
import de.tech26.robotfactory.application.port.out.StockRepository

class StockInMemoryRepository(inputComponents: List<Component>) : StockRepository {

    private var components = inputComponents.map { it.copy() }
    private val componentCodes = inputComponents.map { it.part.code }.toSet()

    override fun displayComponents(codes: List<Code>): Either<InvalidStockState, List<Component>> =
        components
            .filter { it.part.code in codes }
            .let { matchedComponents ->
                return if (matchedComponents.size == codes.toSet().size) {
                    Right(matchedComponents)
                } else {
                    val matchedCodes = matchedComponents.map { it.part.code }
                    val errors = codes
                        .filter { it !in matchedCodes }
                        .map { PartNotFound(it) }
                    Left(InvalidStockState(errors = errors))
                }
            }

    @Synchronized
    override fun subtractComponents(codes: List<Code>): Either<InvalidStockState, List<Code>> {
        val codeQuantities = codes.groupingBy { it }.eachCount()
        val allComponentMap = components.associate { it.part.code to it.available }

        val errors = codes
            .toSet()
            .mapNotNull {
                when {
                    it !in componentCodes -> PartNotFound(it)
                    codeQuantities.getOrDefault(it, 0) > allComponentMap.getOrDefault(it, 0) -> PartNotAvailable(it)
                    else -> null
                }
            }

        return if (errors.isEmpty()) {
            updateComponentsState(codeQuantities)
            Right(codes)
        } else {
            Left(InvalidStockState(errors))
        }
    }

    private fun updateComponentsState(codeQuantities: Map<Code, Int>) {
        components = components
            .map { it.copy(available = it.available - codeQuantities.getOrDefault(it.part.code, 0)) }
    }

}