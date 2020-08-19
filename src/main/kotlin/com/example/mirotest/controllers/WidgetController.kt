package com.example.mirotest.controllers

import com.example.mirotest.dtos.IntersectionFilter
import com.example.mirotest.dtos.WidgetCreationRequest
import com.example.mirotest.dtos.WidgetResponse
import com.example.mirotest.dtos.WidgetUpdateRequest
import com.example.mirotest.services.WidgetService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/widgets")
class WidgetController(private val widgetService: WidgetService) {

    @GetMapping("/{widgetId}")
    fun getWidget(@PathVariable("widgetId") widgetId: UUID): WidgetResponse {
        return WidgetResponse.of(widgetService.findExistingById(widgetId))
    }

    @GetMapping
    fun getAllWidgets(
            @PageableDefault
            @SortDefault(sort = ["zIndex"], direction = Sort.Direction.ASC)
            pageable: @NotNull Pageable,
            intersectionFilter: @Valid IntersectionFilter
    ): Page<WidgetResponse> {
        return widgetService
                .findAllPaged(pageable, intersectionFilter)
                .map {
                    WidgetResponse.of(it)
                }
    }

    @PutMapping("/{widgetId}")
    fun updateExisting(
            @PathVariable("widgetId") widgetId: UUID,
            @RequestBody request: @Valid WidgetUpdateRequest
    ): WidgetResponse {
        return WidgetResponse.of(widgetService.updateExisting(
                widgetId,
                request.x,
                request.y,
                request.width,
                request.height,
                request.z
        ))
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createNew(@RequestBody request: @Valid WidgetCreationRequest): WidgetResponse {
        return WidgetResponse.of(
                widgetService.constructNew(
                        request.x,
                        request.y,
                        request.width,
                        request.height,
                        request.z
                )
        )
    }

    @DeleteMapping("/{widgetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable("widgetId") widgetId: UUID) {
        widgetService.delete(widgetId)
    }

}