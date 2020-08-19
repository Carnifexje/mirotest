package com.example.mirotest.domains.widgets

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WidgetRepository : JpaRepository<Widget, UUID> {

    fun existsByzIndex(zIndex: Int): Boolean

    fun findAllByzIndexIsGreaterThanEqual(zIndex: Int): List<Widget>

}