package de.tech26.robotfactory.application.domain

sealed class DomainError

// Robot Errors
sealed class RobotError
data class TooManyOptionsForPart(val category: PartCategory): RobotError()
data class MissingMandatoryPart(val category: PartCategory): RobotError()
data class InvalidRobotState(val errors: List<RobotError>): DomainError()

// Manufacture Errors
data class UnableToProceedOrder(val reason: String): DomainError()

// Stock Errors
sealed class StockError
data class PartNotAvailable(val code: Code): StockError()
data class PartNotFound(val code: Code): StockError()
data class InvalidStockState(val errors: List<StockError>): DomainError()