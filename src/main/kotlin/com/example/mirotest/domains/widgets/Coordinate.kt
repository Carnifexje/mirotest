package com.example.mirotest.domains.widgets

import javax.persistence.Embeddable

@Embeddable
data class Coordinate(var x: Int, var y: Int)