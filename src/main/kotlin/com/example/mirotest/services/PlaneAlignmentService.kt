package com.example.mirotest.services

import com.example.mirotest.domains.widgets.Widget

interface PlaneAlignmentService {
    fun shiftUp(widgetsToShift: List<Widget>): List<Widget>
}