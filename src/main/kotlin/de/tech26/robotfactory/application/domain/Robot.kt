package de.tech26.robotfactory.application.domain

import de.tech26.functional.Either
import de.tech26.functional.Left
import de.tech26.functional.Right
import de.tech26.robotfactory.application.domain.PartCategory.*


enum class PartCategory { FACE, MATERIAL, ARMS, MOBILITY }
typealias Code = String

sealed class Part(open val code: Code, val category: PartCategory)
data class FacePart(override val code: Code): Part(code, FACE)
data class MaterialPart(override val code: Code): Part(code, MATERIAL)
data class ArmsPart(override val code: Code): Part(code, ARMS)
data class MobilityPart(override val code: Code): Part(code, MOBILITY)


data class Robot(
    val face: FacePart,
    val material: MaterialPart,
    val arms: ArmsPart,
    val mobility: MobilityPart
) {

    companion object {

        private fun validateParts(parts: List<Part>): List<RobotError> {
            val partsMap = parts.groupingBy { it.category }.eachCount()
            return PartCategory
                .values()
                .asList()
                .map { Pair(it, partsMap.getOrDefault(it, 0)) }
                .mapNotNull {
                    when {
                        it.second < 1 -> MissingMandatoryPart(it.first)
                        it.second == 1 -> null
                        else -> TooManyOptionsForPart(it.first)
                    }
                }
        }

        fun fromParts(parts: List<Part>): Either<InvalidRobotState, Robot> =
            validateParts(parts).let { errors ->
                if (errors.isEmpty()) {
                    Right(Robot(
                        face = FacePart(parts.first { it.category == FACE }.code),
                        material = MaterialPart(parts.first { it.category == MATERIAL }.code),
                        arms = ArmsPart(parts.first { it.category == ARMS }.code),
                        mobility = MobilityPart(parts.first { it.category == MOBILITY }.code)
                    ))
                } else {
                    Left(InvalidRobotState(errors = errors))
                }
            }

    }

}