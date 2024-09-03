package com.phodal.shirecore.search.tokenizer

import com.phodal.shirecore.search.tokenizer.TermSplitter.splitTerms
import junit.framework.TestCase.assertEquals
import org.junit.Test

class TermSplitterTest {

    @Test
    fun should_splitTerms_when_inputIsCamelCase() {
        // Given
        val input = "HelloWorld_helloWorld123"

        // When
        val result = splitTerms(input).toList()

        // Then
        val expected = listOf(
            "helloworld_helloworld123",
            "hello",
            "world_hello",
            "world123",
            "helloworld",
            "helloworld123",
            "helloworld_helloworld"
        )
        assertEquals(expected, result)
    }

    @Test
    fun should_splitTerms_when_inputIsUnderscoreCase() {
        // Given
        val input = "underscore_case_example"

        // When
        val result = splitTerms(input).toList()

        // Then
        val expected = listOf("underscore_case_example", "underscore", "case", "example")
        assertEquals(expected, result)
    }

    @Test
    fun should_splitTerms_when_inputContainsNumericSuffix() {
        // Given
        val input = "example123"

        // When
        val result = splitTerms(input).toList()

        // Then
        val expected = listOf("example123", "example")
        assertEquals(expected, result)
    }

    @Test
    fun should_splitTerms_when_inputIsMixedNamingStyles() {
        // Given
        val input = "CamelCase_with123Numbers"

        // When
        val result = splitTerms(input).toList()

        // Then
        val expected = listOf("camelcase_with123numbers", "camel", "case_with123numbers", "camelcase", "with123numbers")
        assertEquals(expected, result)
    }

    @Test
    fun should_syncSplitTerms_when_inputIsCamelCase() {
        // Given
        val input = "CamelCaseExample"

        // When
        val result = splitTerms(input).toList()

        // Then
        val expected = listOf("camelcaseexample", "camel", "case", "example")
        assertEquals(expected, result)
    }

    @Test
    fun should_syncSplitTerms_when_inputIsUnderscoreCase() {
        // Given
        val input = "underscore_case_example"

        // When
        val result = splitTerms(input).toList()

        // Then
        val expected = listOf("underscore_case_example", "underscore", "case", "example")
        assertEquals(expected, result)
    }

    @Test
    fun should_syncSplitTerms_when_inputContainsNumericSuffix() {
        // Given
        val input = "example123"

        // When
        val result = splitTerms(input).toList()

        // Then
        val expected = listOf("example123", "example")
        assertEquals(expected, result)
    }

    @Test
    fun should_handle_java_controller_in_real_world() {
        // Given
        val input = """
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public String postTicket(@RequestBody TicketCreateRequest ticketCreateRequest){
        Ticket ticket = ticketMapper.toEntity(ticketCreateRequest);
        List<Food> foods = ticketCreateRequest.getFood().stream()
                .map((foodId) -> foodService.findById(foodId))
                .collect(Collectors.toList());
        ticket.setFoods(foods);
        return ticketService.postTicket(ticket);
    }            
    """.trimMargin()

        // When
        val result = splitTerms(input).toList()

        // Then
        val expected = listOf(
            "postmapping", "post", "mapping", "responsestatus", "response", "status", "code", "httpstatus", "http", "created",
            "public", "string", "postticket", "ticket", "requestbody", "request", "body", "ticketcreaterequest", "create",
            "ticketmapper", "mapper", "toentity", "entity", "list", "food", "foods", "getfood", "get", "stream", "map",
            "foodid", "foodservice", "service", "findbyid", "find", "collect", "collectors", "tolist", "setfoods", "set",
            "return", "ticketservice"
        )
        assertEquals(expected, result)
    }
}
