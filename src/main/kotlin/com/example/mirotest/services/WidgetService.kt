package com.example.mirotest.services

import com.example.mirotest.domains.widgets.Widget
import com.example.mirotest.dtos.IntersectionFilter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface WidgetService {

    fun findExistingById(id: UUID): Widget

    fun findAllPaged(pageable: Pageable, intersectionFilter: IntersectionFilter): Page<Widget>

    fun updateExisting(
            widgetId: UUID,
            newX: Int,
            newY: Int,
            newWidth: Int,
            newHeight: Int,
            newZ: Int
    ): Widget

    fun constructNew(
            newX: Int,
            newY: Int,
            newWidth: Int,
            newHeight: Int,
            newZ: Int
    ): Widget

    fun delete(widgetId: UUID)
}