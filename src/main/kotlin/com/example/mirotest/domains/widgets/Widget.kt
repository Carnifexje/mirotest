package com.example.mirotest.domains.widgets

import java.awt.Rectangle
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
data class Widget(
        @Id @GeneratedValue val id: UUID? = null,
        @Embedded val coordinate: Coordinate,
        @Column(unique = true) var zIndex: Int,
        @Embedded val dimension: Dimension,
        val lastModifiedAt: LocalDateTime? = LocalDateTime.now()
) {

    fun toRectangle(): Rectangle {
        return Rectangle(
                this.coordinate.x,
                this.coordinate.y,
                this.dimension.width,
                this.dimension.height
        )
    }

}