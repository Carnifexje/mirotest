package com.example.mirotest.dtos

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class WidgetCreationRequest(
        val x: @NotNull Int,
        val y: @NotNull Int,
        val z: @NotNull Int,
        val width: @Min(value = 0) Int,
        val height: @Min(value = 0) Int
)