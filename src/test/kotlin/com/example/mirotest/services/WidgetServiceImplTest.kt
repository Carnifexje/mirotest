package com.example.mirotest.services

import com.example.mirotest.domains.widgets.Coordinate
import com.example.mirotest.domains.widgets.Dimension
import com.example.mirotest.domains.widgets.Widget
import com.example.mirotest.domains.widgets.WidgetRepository
import com.example.mirotest.dtos.IntersectionFilter
import com.example.mirotest.exceptions.WidgetNotFoundException
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

internal class WidgetServiceImplTest {

    @InjectMockKs
    private lateinit var service: WidgetServiceImpl

    @MockK
    private lateinit var repository: WidgetRepository

    @MockK
    private lateinit var planeAlignmentService: PlaneAlignmentService

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun findExistingById() {
        val givenId = UUID.randomUUID()
        val widget = Widget(
                null,
                Coordinate(1, 2),
                0,
                Dimension(10, 20),
                LocalDateTime.now()
        )
        every { repository.findById(givenId) } returns Optional.of(widget)

        val actual = service.findExistingById(givenId)

        Assertions.assertThat(actual).isEqualTo(widget)
        verify { repository.findById(givenId) }
    }

    @Test
    fun exceptionIsThrownWhenWidgetDoesNotExist() {
        val givenId = UUID.randomUUID()

        every { repository.findById(givenId) } returns Optional.empty()

        assertThrows(WidgetNotFoundException::class.java) { service.findExistingById(givenId) }
    }

    @Test
    fun updateExisting() {
        val givenId = UUID.randomUUID()
        val widget = Widget(
                null,
                Coordinate(1, 2),
                0,
                Dimension(10, 20),
                LocalDateTime.of(1970, 1, 1, 0, 0, 0)
        )
        every { repository.findById(givenId) } returns Optional.of(widget)
        every { repository.save<Widget>(any()) } returns Widget(coordinate = Coordinate(0, 0), dimension = Dimension(0, 0), zIndex = 0)

        service.updateExisting(givenId, 0, 0, 0, 0, 0)

        val slot = slot<Widget>()
        verify { repository.save(capture(slot)) }

        val (_, coordinate, zIndex, dimension, lastModifiedAt) = slot.captured
        assertThat(zIndex).isEqualTo(0)
        assertThat(dimension.height).isEqualTo(0)
        assertThat(dimension.width).isEqualTo(0)
        assertThat(coordinate.x).isEqualTo(0)
        assertThat(coordinate.y).isEqualTo(0)
        assertThat(lastModifiedAt).isCloseTo(LocalDateTime.now(), Assertions.within(1, ChronoUnit.SECONDS))
    }

    @Test
    fun updatingExistingWidgetShiftsPlaneWhenZIndexChanges() {
        val givenId = UUID.randomUUID()
        val givenWidget = Widget(
                null,
                Coordinate(1, 2),
                0,
                Dimension(10, 20),
                LocalDateTime.now()
        )
        every { repository.findById(givenId) } returns Optional.of(givenWidget)
        every { repository.existsByzIndex(any()) } returns true
        val givenWidgetsToShift = listOf(givenWidget)
        every { repository.findAllByzIndexIsGreaterThanEqual(any()) } returns givenWidgetsToShift
        every { planeAlignmentService.shiftUp(givenWidgetsToShift) } returns givenWidgetsToShift
        every { repository.save<Widget>(any()) } returns Widget(coordinate = Coordinate(0, 0), dimension = Dimension(0, 0), zIndex = 0)
        every { repository.saveAndFlush<Widget>(any()) } returns Widget(coordinate = Coordinate(0, 0), dimension = Dimension(0, 0), zIndex = 0)

        service.updateExisting(givenId, 0, 0, 0, 0, 1)

        verify { repository.findById(givenId) }
        verify { repository.existsByzIndex(1) }
        verify { repository.findAllByzIndexIsGreaterThanEqual(1) }
        verify { planeAlignmentService.shiftUp(givenWidgetsToShift) }
        verify { repository.saveAndFlush<Widget>(any()) }
        verify { repository.save<Widget>(any()) }
    }

    @Test
    fun findAllPaged() {
        val givenWidget = Widget(
                null,
                Coordinate(1, 2),
                0,
                Dimension(10, 20),
                LocalDateTime.now()
        )
        val widgets = listOf(givenWidget, givenWidget)
        val givenPage = PageImpl(widgets)
        every { repository.findAll(ofType(Pageable::class)) } returns givenPage

        val actual = service.findAllPaged(Pageable.unpaged(), IntersectionFilter(null, null, null, null))

        assertThat(actual.toList()).containsExactlyElementsOf(givenPage.toList())
    }

    @Test
    fun findAllPagedWithIntersectionFilter() {
        val givenWidget1 = Widget(
                null,
                Coordinate(0, 0),
                0,
                Dimension(10, 10),
                LocalDateTime.now()
        )
        val givenWidget2 = givenWidget1.copy(coordinate = Coordinate(100, 100))
        val widgets = listOf(givenWidget1, givenWidget2)
        val givenPage = PageImpl(widgets)
        every { repository.findAll(ofType(Pageable::class)) } returns givenPage
        val givenFilter = IntersectionFilter(0, 0, 20, 20)

        val actual = service.findAllPaged(Pageable.unpaged(), givenFilter)

        assertThat(actual.toList()).containsExactlyElementsOf(listOf(givenWidget1))
    }

    @Test
    fun constructNew() {
        every { repository.existsByzIndex(any()) } returns false
        every { repository.save<Widget>(any()) } returns Widget(coordinate = Coordinate(0, 0), dimension = Dimension(0, 0), zIndex = 0)

        service.constructNew(0, 0, 0, 0, 0)

        val slot = slot<Widget>()
        verify { repository.save(capture(slot)) }
        val (_, coordinate, zIndex, dimension, lastModifiedAt) = slot.captured
        assertThat(zIndex).isEqualTo(0)
        assertThat(dimension.height).isEqualTo(0)
        assertThat(dimension.width).isEqualTo(0)
        assertThat(coordinate.x).isEqualTo(0)
        assertThat(coordinate.y).isEqualTo(0)
        assertThat(lastModifiedAt).isCloseTo(LocalDateTime.now(), Assertions.within(1, ChronoUnit.SECONDS))
    }

    @Test
    fun constructingNewWidgetShiftsZPlaneWhenWidgetExistsOnZIndex() {
        val givenWidget = Widget(
                null,
                Coordinate(0, 0),
                0,
                Dimension(10, 10),
                LocalDateTime.now()
        )
        val givenWidgetsToShift = listOf(givenWidget)
        every { repository.existsByzIndex(any()) } returns true
        every { repository.findAllByzIndexIsGreaterThanEqual(any()) } returns givenWidgetsToShift
        every { planeAlignmentService.shiftUp(givenWidgetsToShift) } returns givenWidgetsToShift
        every { repository.save<Widget>(any()) } returns Widget(coordinate = Coordinate(0, 0), dimension = Dimension(0, 0), zIndex = 0)
        every { repository.saveAndFlush<Widget>(any()) } returns Widget(coordinate = Coordinate(0, 0), dimension = Dimension(0, 0), zIndex = 0)

        service.constructNew(0, 0, 0, 0, 0)

        verify { repository.findAllByzIndexIsGreaterThanEqual(0) }
        verify { planeAlignmentService.shiftUp(givenWidgetsToShift) }
        verify { repository.saveAndFlush<Widget>(any()) }
        verify { repository.save<Widget>(any()) }
    }

    @Test
    fun delete() {
        val givenId = UUID.randomUUID()
        val givenWidget = Widget(
                givenId,
                Coordinate(0, 0),
                0,
                Dimension(10, 10),
                LocalDateTime.now()
        )

        every { repository.findById(givenId) } returns Optional.of(givenWidget)
        every { repository.delete(givenWidget) } answers { }

        service.delete(givenId)

        verify { repository.delete(givenWidget) }
    }
}