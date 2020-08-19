package com.example.mirotest.dtos

import com.example.mirotest.domains.widgets.Widget
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.*

data class WidgetResponse(val id: UUID, val x: Int, val y: Int, val z: Int, val width: Int, val height: Int, @field:JsonProperty("last_modified_at") val lastModifiedAt: LocalDateTime) {

    companion object {
        fun of(widget: Widget): WidgetResponse {
            return WidgetResponse(
                    widget.id!!,
                    widget.coordinate.x,
                    widget.coordinate.y,
                    widget.zIndex,
                    widget.dimension.width,
                    widget.dimension.height,
                    widget.lastModifiedAt!!
            )
        }
    }

}