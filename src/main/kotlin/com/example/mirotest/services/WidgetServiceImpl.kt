package com.example.mirotest.services

import com.example.mirotest.domains.widgets.Coordinate
import com.example.mirotest.domains.widgets.Dimension
import com.example.mirotest.domains.widgets.Widget
import com.example.mirotest.domains.widgets.WidgetRepository
import com.example.mirotest.dtos.IntersectionFilter
import com.example.mirotest.exceptions.WidgetNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class WidgetServiceImpl(
        private val widgetRepository: WidgetRepository,
        private val planeAlignmentService: PlaneAlignmentService
) : WidgetService {

    override fun findExistingById(id: UUID): Widget {
        return widgetRepository.findById(id).orElseThrow { WidgetNotFoundException(id) }
    }

    override fun updateExisting(
            widgetId: UUID,
            newX: Int,
            newY: Int,
            newWidth: Int,
            newHeight: Int,
            newZ: Int
    ): Widget {
        val widgetToUpdate = findExistingById(widgetId)

        // we only need to shift the z plane if the consumer requests to change the z-index, and if another widget
        // already exists on the requested z-index
        if (newZ != widgetToUpdate.zIndex && widgetRepository.existsByzIndex(newZ)) {
            shiftWidgetsUpFromZIndex(newZ)
        }

        return save(widgetToUpdate.copy(
                dimension = Dimension(newWidth, newHeight),
                coordinate = Coordinate(newX, newY),
                zIndex = newZ
        ))
    }

    override fun findAllPaged(pageable: Pageable, intersectionFilter: IntersectionFilter): Page<Widget> {
        if (!intersectionFilter.isInitialized()) {
            log.debug("Finding paged widget results without intersection filter")
            return widgetRepository.findAll(pageable)
        }
        val intersectionBounds = intersectionFilter.toRectangle()
        log.debug("Finding paged widget results with intersection filter {}", intersectionBounds)
        val widgets = widgetRepository
                .findAll(pageable)
                .filter { intersectionBounds.contains(it.toRectangle()) }
                .toList()
        return PageImpl(widgets, pageable, widgets.size.toLong())
    }

    override fun constructNew(
            newX: Int,
            newY: Int,
            newWidth: Int,
            newHeight: Int,
            newZ: Int
    ): Widget {
        if (widgetRepository.existsByzIndex(newZ)) {
            shiftWidgetsUpFromZIndex(newZ)
        }
        return save(Widget(
                coordinate = Coordinate(newX, newY),
                dimension = Dimension(newWidth, newHeight),
                zIndex = newZ
        ))
    }

    override fun delete(widgetId: UUID) {
        widgetRepository.delete(findExistingById(widgetId))
    }

    private fun save(widget: Widget): Widget {
        return widgetRepository.save(widget.copy(lastModifiedAt = LocalDateTime.now()))
    }

    private fun shiftWidgetsUpFromZIndex(z: Int) {
        log.debug("Shifting z plane from z index {}", z)
        // we shift each widget above the requested z-index up, then store each shifted widget
        // starting with the highest z-index.
        // We do this because otherwise we would get unique constraint violations during our transaction.
        planeAlignmentService.shiftUp(widgetRepository.findAllByzIndexIsGreaterThanEqual(z))
                .sortedByDescending { it.zIndex }
                .forEach { widgetRepository.saveAndFlush(it) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(WidgetServiceImpl::class.java)
    }

}