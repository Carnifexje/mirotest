package com.example.mirotest.domains.widgets

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime

@DataJpaTest
class WidgetRepositoryTest @Autowired constructor(val widgetRepository: WidgetRepository) {

    @BeforeEach
    fun setUp() {
        widgetRepository.deleteAll()
    }

    @Test
    fun existsByzIndex() {
        val givenWidget = Widget(
                null,
                Coordinate(1, 2),
                0,
                Dimension(10, 20),
                LocalDateTime.now()
        )
        val (_, _, zIndex) = widgetRepository.save(givenWidget)

        val actual = widgetRepository.existsByzIndex(zIndex)

        Assertions.assertThat(actual).isTrue()
    }

    @Test
    fun returnsFalseWhenWidgetDoesNotExistByGivenZIndex() {
        val actual = widgetRepository.existsByzIndex(0)

        Assertions.assertThat(actual).isFalse()
    }

    @Test
    fun findAllByzIndexGreaterThanEqual() {
        val givenWidget1 = Widget(
                null,
                Coordinate(1, 2),
                0,
                Dimension(10, 20),
                LocalDateTime.now()
        )
        val givenWidget2 = givenWidget1.copy(zIndex = 1)
        val givenWidget3 = givenWidget1.copy(zIndex = 2)

        widgetRepository.saveAll(listOf(givenWidget1, givenWidget2, givenWidget3))

        val actual = widgetRepository.findAllByzIndexIsGreaterThanEqual(1)

        Assertions.assertThat(actual).hasSize(2)
        Assertions.assertThat(actual).containsExactlyElementsOf(listOf(givenWidget2, givenWidget3))
    }
}