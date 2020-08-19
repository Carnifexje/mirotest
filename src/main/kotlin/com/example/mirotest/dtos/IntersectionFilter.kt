package com.example.mirotest.dtos

import java.awt.Rectangle

data class IntersectionFilter(val x: Int?, val y: Int?, val width: Int?, val height: Int?) {

    fun toRectangle(): Rectangle {
        return Rectangle(x!!, y!!, width!!, height!!)
    }

    fun isInitialized(): Boolean {
        return x != null && y != null && width != null && height != null
    }

}