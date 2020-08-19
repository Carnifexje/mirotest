package com.example.mirotest.controllers

import com.example.mirotest.domains.widgets.Coordinate
import com.example.mirotest.domains.widgets.Dimension
import com.example.mirotest.domains.widgets.Widget
import com.example.mirotest.dtos.WidgetCreationRequest
import com.example.mirotest.dtos.WidgetUpdateRequest
import com.example.mirotest.services.WidgetService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Answer
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.LinkedMultiValueMap
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [WidgetController::class])
internal class WidgetControllerTest @Autowired constructor(private val mockMvc: MockMvc, private val objectMapper: ObjectMapper) {

    @MockkBean
    private lateinit var widgetService: WidgetService

    @Test
    fun getWidget() {
        val givenId = UUID.fromString("b52ae8f9-c7b3-49bb-8dca-f4374e1e50b4")
        val widget = Widget(
                givenId,
                Coordinate(1, 2),
                0,
                Dimension(10, 20),
                LocalDateTime.of(2020, 8, 22, 0, 0, 0)
        )
        every { widgetService.findExistingById(givenId) } returns widget

        mockMvc.perform(MockMvcRequestBuilders.get("/widgets/{widgetId}", givenId))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json("{\"id\":\"b52ae8f9-c7b3-49bb-8dca-f4374e1e50b4\",\"x\":1,\"y\":2,\"z\":0,\"width\":10,\"height\":20,\"last_modified_at\":\"2020-08-22T00:00:00\"}"))
    }

    @Test
    fun getAllWidgets() {
        val widget1 = Widget(
                UUID.fromString("b52ae8f9-c7b3-49bb-8dca-f4374e1e50b4"),
                Coordinate(1, 2),
                0,
                Dimension(10, 20),
                LocalDateTime.of(2020, 8, 22, 0, 0, 0)
        )
        val widget2 = widget1.copy(zIndex = 1)

        val givenWidgets = listOf(widget1, widget2)

        every { widgetService.findAllPaged(any(), any()) } returns PageImpl(givenWidgets)

        mockMvc.perform(MockMvcRequestBuilders.get("/widgets"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json("{\"content\":[{\"id\":\"b52ae8f9-c7b3-49bb-8dca-f4374e1e50b4\",\"x\":1,\"y\":2,\"z\":0,\"width\":10,\"height\":20,\"last_modified_at\":\"2020-08-22T00:00:00\"},{\"id\":\"b52ae8f9-c7b3-49bb-8dca-f4374e1e50b4\",\"x\":1,\"y\":2,\"z\":1,\"width\":10,\"height\":20,\"last_modified_at\":\"2020-08-22T00:00:00\"}],\"pageable\":\"INSTANCE\",\"totalPages\":1,\"totalElements\":2,\"last\":true,\"number\":0,\"size\":2,\"sort\":{\"unsorted\":true,\"sorted\":false,\"empty\":true},\"numberOfElements\":2,\"first\":true,\"empty\":false}"))
    }

    @Test
    fun getAllWidgetsWithIntersectionFilter() {
        every { widgetService.findAllPaged(any(), any()) } returns Page.empty()

        val requestParams = LinkedMultiValueMap<String, String>()
        requestParams.add("x", "0")
        requestParams.add("y", "0")
        requestParams.add("width", "10")
        requestParams.add("height", "10")

        mockMvc.perform(MockMvcRequestBuilders.get("/widgets").params(requestParams))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json("{\"content\":[],\"pageable\":\"INSTANCE\",\"totalPages\":1,\"last\":true,\"totalElements\":0,\"number\":0,\"size\":0,\"sort\":{\"unsorted\":true,\"sorted\":false,\"empty\":true},\"numberOfElements\":0,\"first\":true,\"empty\":true}"))
    }

    @Test
    fun updateExisting() {
        val givenId = UUID.fromString("b52ae8f9-c7b3-49bb-8dca-f4374e1e50b4")
        val request = WidgetUpdateRequest(0, 1, 2, 3, 4)
        val widget = Widget(
                givenId,
                Coordinate(request.x, request.y),
                request.z,
                Dimension(request.width, request.height),
                LocalDateTime.of(2020, 8, 22, 0, 0, 0)
        )
        every { widgetService.updateExisting(
                givenId,
                request.x,
                request.y,
                request.width,
                request.height,
                request.z
        ) } returns widget

        mockMvc.perform(
                MockMvcRequestBuilders.put("/widgets/{widgetId}", givenId)
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json("{\"id\":\"b52ae8f9-c7b3-49bb-8dca-f4374e1e50b4\",\"x\":0,\"y\":1,\"z\":2,\"width\":3,\"height\":4,\"last_modified_at\":\"2020-08-22T00:00:00\"}"))
    }

    @Test
    fun createNew() {
        val request = WidgetCreationRequest(0, 1, 2, 3, 4)
        val widget = Widget(
                UUID.fromString("b52ae8f9-c7b3-49bb-8dca-f4374e1e50b4"),
                Coordinate(request.x, request.y),
                request.z,
                Dimension(request.width, request.height),
                LocalDateTime.of(2020, 8, 22, 0, 0, 0)
        )
        every { widgetService.constructNew(
                request.x,
                request.y,
                request.width,
                request.height,
                request.z
        ) } returns widget

        mockMvc.perform(
                MockMvcRequestBuilders.post("/widgets")
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.content().json("{\"id\":\"b52ae8f9-c7b3-49bb-8dca-f4374e1e50b4\",\"x\":0,\"y\":1,\"z\":2,\"width\":3,\"height\":4,\"last_modified_at\":\"2020-08-22T00:00:00\"}"))
    }

    @Test
    fun delete() {
        val givenId = UUID.randomUUID()

        every { widgetService.delete(givenId) } answers { }

        mockMvc.perform(MockMvcRequestBuilders.delete("/widgets/{widgetId}", givenId))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
    }
}