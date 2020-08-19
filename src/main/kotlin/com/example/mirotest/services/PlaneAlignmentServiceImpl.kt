package com.example.mirotest.services

import com.example.mirotest.domains.widgets.Widget
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PlaneAlignmentServiceImpl : PlaneAlignmentService {

    override fun shiftUp(widgetsToShift: List<Widget>): List<Widget> {
        // TODO: do not shift gaps but rather fill them
        return widgetsToShift
                .map {
                    val shiftedZ = ++it.zIndex
                    log.debug("shifting widget {} up to z-index {}", it, shiftedZ)
                    it.zIndex = shiftedZ
                    it
                }
                .toList()
    }

    companion object {
        private val log = LoggerFactory.getLogger(PlaneAlignmentServiceImpl::class.java)
    }
}