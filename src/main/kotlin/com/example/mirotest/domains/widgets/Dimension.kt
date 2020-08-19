package com.example.mirotest.domains.widgets

import javax.persistence.Embeddable

@Embeddable
data class Dimension(var width: Int, var height: Int)