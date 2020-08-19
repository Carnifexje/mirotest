package com.example.mirotest.services

import com.example.mirotest.domains.widgets.Coordinate
import com.example.mirotest.domains.widgets.Dimension
import com.example.mirotest.domains.widgets.Widget
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class PlaneAlignmentServiceImplTest {

    @InjectMockKs
    private lateinit var planeAlignmentService: PlaneAlignmentServiceImpl

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun shiftUp() {
        val widget = Widget(
                null,
                Coordinate(1, 2),
                0,
                Dimension(10, 20),
                LocalDateTime.now()
        )
        val widgets = listOf(widget, widget.copy(zIndex = 1))

        val actual = planeAlignmentService.shiftUp(widgets)

        Assertions.assertThat(actual[0].zIndex).isEqualTo(1)
        Assertions.assertThat(actual[1].zIndex).isEqualTo(2)
    }
}