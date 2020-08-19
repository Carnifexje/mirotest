package com.example.mirotest.exceptions

import java.util.UUID


class WidgetNotFoundException(givenId: UUID) : RuntimeException("Widget with given id $givenId does not exist")