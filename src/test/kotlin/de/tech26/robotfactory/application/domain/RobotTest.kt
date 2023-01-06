package de.tech26.robotfactory.application.domain

import de.tech26.robotfactory.application.domain.PartCategory.*
import de.tech26.robotfactory.utils.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.rmi.UnexpectedException

internal class RobotTest {

    @Test
    fun `given that the components has not all parts than it needs should return an error`() {
        val parts = emptyList<Part>()
        val listPartErrors = listOf(
            MissingMandatoryPart(FACE),
            MissingMandatoryPart(MOBILITY),
            MissingMandatoryPart(MATERIAL),
            MissingMandatoryPart(ARMS)
        )

        val result = Robot.fromParts(parts)

        assertThat(result.isRight()).isFalse
        assertThat(result.getLeftOrThrow { UnexpectedException("") }.errors).containsAll(listPartErrors)
    }

    @Test
    fun `given that the components has more parts than it needs should return an error`() {
        val parts = listOf(
            partFace,
            partFace,
            partMaterial,
            partMobility,
            partMobility,
            partArms
        )
        val expectedResult = listOf(
            TooManyOptionsForPart(FACE),
            TooManyOptionsForPart(MOBILITY),
        )

        val result = Robot.fromParts(parts)

        assertThat(result.isRight()).isFalse
        assertThat(result.getLeftOrThrow { UnexpectedException("") }.errors).containsAll(expectedResult)
    }

    @Test
    fun `given that the components has missing parts and duplicated parts than it needs should return an error`() {
        val parts = listOf(
            partFace,
            partFace,
            partMaterial,
            partMobility,
        )
        val listPartErrors = listOf(
            TooManyOptionsForPart(FACE),
            MissingMandatoryPart(ARMS)
        )

        val result = Robot.fromParts(parts)

        assertThat(result.isRight()).isFalse
        assertThat(result.getLeftOrThrow { UnexpectedException("") }.errors).containsAll(listPartErrors)
    }

    @Test
    fun `given that the components have exactly the parts it need should create a robot`() {
        val parts = listOf(
            partFace,
            partMaterial,
            partMobility,
            partArms
        )

        val result = Robot.fromParts(parts)

        assertThat(result.isRight()).isTrue
        val retrievedRobot = result.getOrThrow { UnexpectedException("") }
        assertThat(retrievedRobot.arms).isEqualTo(ArmsPart(partArms.code))
        assertThat(retrievedRobot.face).isEqualTo(FacePart(partFace.code))
        assertThat(retrievedRobot.material).isEqualTo(MaterialPart(partMaterial.code))
        assertThat(retrievedRobot.mobility).isEqualTo(MobilityPart(partMobility.code))
    }

}