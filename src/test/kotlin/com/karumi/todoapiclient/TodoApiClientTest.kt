package com.karumi.todoapiclient

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import todoapiclient.TodoApiClient
import todoapiclient.dto.TaskDto
import todoapiclient.exception.UnknownApiError

class TodoApiClientTest : MockWebServerTest() {

    private lateinit var apiClient: TodoApiClient

    @Before
    override fun setUp() {
        super.setUp()
        val mockWebServerEndpoint = baseEndpoint
        apiClient = TodoApiClient(mockWebServerEndpoint)
    }

    @Test
    fun sendsAcceptAndContentTypeHeaders() {
        enqueueMockResponse(200, "getTasksResponse.json")

        apiClient.allTasks

        assertRequestContainsHeader("Accept", "application/json")
    }

    @Test
    fun sendsGetAllTaskRequestToTheCorrectEndpoint() {
        enqueueMockResponse(200, "getTasksResponse.json")

        apiClient.allTasks

        assertGetRequestSentTo("/todos")
    }


    @Test
    fun sendsSuccessResponse() {
        enqueueMockResponse(200, "getTasksResponse.json")

        val response = apiClient.allTasks
        assertTrue(response.right!!.isNotEmpty())
        assertTrue(response.right!![0].id.isNotEmpty())
        assertTrue(response.right!![0].title.isNotEmpty())
        assertTrue(response.right!![0].userId.isNotEmpty())

    }


    @Test
    fun throwsUnknownErrorIfThereIsNoHandledError() {
        enqueueMockResponse(500)

        val error = apiClient.allTasks.left

        assertEquals(UnknownApiError(500), error)

    }

    @Test
    fun sendsGetTaskByIdContainsCorrectPath() {

        enqueueMockResponse(200)

        apiClient.getTaskById("1")

        assertGetRequestSentTo("/todos/1")

    }

    @Test
    fun sendsGetTaskByIdReturnsCorrectData() {

        enqueueMockResponse(200,"getTaskByIdResponse.json")


        val task = apiClient.getTaskById("1").right

        assertTaskContainsExpectedValues(task)

    }


    @Test
    fun sendsGetAllTaskRequestToExpectedPath() {
        enqueueMockResponse(200, "getTasksResponse.json")

        apiClient.allTasks

        assertRequestSentTo("/todos")
    }

    @Test
    fun parsesTasksProperlyGettingAllTheTasks() {
        enqueueMockResponse(200, "getTasksResponse.json")

        val tasks = apiClient.allTasks.right!!

        assertEquals(200, tasks.size.toLong())
        assertTaskContainsExpectedValues(tasks[0])
    }


    private fun assertTaskContainsExpectedValues(task: TaskDto?) {
        assertTrue(task != null)
        assertEquals(task?.id, "1")
        assertEquals(task?.userId, "1")
        assertEquals(task?.title, "delectus aut autem")
        assertFalse(task!!.isFinished)
    }
}
