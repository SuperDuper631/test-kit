package com.interview.test.controller;

import com.interview.test.model.CollectionCheckItemRequest;
import com.interview.test.model.CollectionCheckItemResponse;
import com.interview.test.model.CollectionCheckRequest;
import com.interview.test.model.CollectionCheckResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CollectionControllerTest {
	@Autowired
	private ObjectMapper objectMapper;

	@Value("${local.server.port}")
	private int port;

	@Test
	void returnsCollectableForAvailabilityEndDateRegardlessOfTime() throws Exception {
		CollectionCheckRequest request = new CollectionCheckRequest(
				OffsetDateTime.parse("2026-08-31T23:59:00Z"),
				List.of(new CollectionCheckItemRequest("SKU-1001", "LOC-42")));

		CollectionCheckResponse body = post(request);

		assertEquals(request.requestedAt(), body.requestedAt());
		assertEquals(1, body.items().size());
		CollectionCheckItemResponse item = body.items().get(0);
		assertTrue(item.collectable());
		assertEquals("SKU-1001", item.sku());
		assertEquals("LOC-42", item.locationId());
		assertEquals(1, item.availability().size());
		assertEquals("2026-08-01", item.availability().get(0).availableFrom().toString());
		assertEquals("2026-08-31", item.availability().get(0).availableUntil().toString());
	}

	@Test
	void returnsAvailabilityButNotCollectableWhenRequestedDateIsOutsidePeriod() throws Exception {
		CollectionCheckRequest request = new CollectionCheckRequest(
				OffsetDateTime.parse("2026-09-01T00:00:00Z"),
				List.of(new CollectionCheckItemRequest("SKU-1001", "LOC-42")));

		CollectionCheckItemResponse item = post(request).items().get(0);
		assertFalse(item.collectable());
		assertEquals(1, item.availability().size());
	}

	@Test
	void returnsNotCollectableWithEmptyAvailabilityForUnknownItem() throws Exception {
		CollectionCheckRequest request = new CollectionCheckRequest(
				OffsetDateTime.parse("2026-08-15T14:30:00Z"),
				List.of(new CollectionCheckItemRequest("SKU-UNKNOWN", "LOC-42")));

		CollectionCheckItemResponse item = post(request).items().get(0);
		assertFalse(item.collectable());
		assertTrue(item.availability().isEmpty());
	}

	@Test
	void treatsAvailabilityStartAndEndDatesAsInclusive() throws Exception {
		CollectionCheckRequest request = new CollectionCheckRequest(
				OffsetDateTime.parse("2026-08-15T14:30:00Z"),
				List.of(
						new CollectionCheckItemRequest("SKU-1004", "LOC-30"),
						new CollectionCheckItemRequest("SKU-1005", "LOC-40")));

		List<CollectionCheckItemResponse> items = post(request).items();
		assertEquals(List.of(true, true), items.stream()
				.map(CollectionCheckItemResponse::collectable).toList());
	}

	@Test
	void returnsAllWindowsWhenNoneOrOnlyOneMatches() throws Exception {
		CollectionCheckRequest request = new CollectionCheckRequest(
				OffsetDateTime.parse("2026-08-15T14:30:00Z"),
				List.of(
						new CollectionCheckItemRequest("SKU-1002", "LOC-10"),
						new CollectionCheckItemRequest("SKU-1006", "LOC-50")));

		List<CollectionCheckItemResponse> items = post(request).items();	
		assertFalse(items.get(0).collectable());
		assertEquals(2, items.get(0).availability().size());
		assertTrue(items.get(1).collectable());
		assertEquals(2, items.get(1).availability().size());
	}

	@Test
	void handlesExampleRequestAndPreservesItemOrder() throws Exception {
		CollectionCheckRequest request = new CollectionCheckRequest(
				OffsetDateTime.parse("2026-08-15T14:30:00Z"),
				List.of(
						new CollectionCheckItemRequest("SKU-1001", "LOC-42"),
						new CollectionCheckItemRequest("SKU-1002", "LOC-10"),
						new CollectionCheckItemRequest("SKU-1003", "LOC-20"),
						new CollectionCheckItemRequest("SKU-1004", "LOC-30"),
						new CollectionCheckItemRequest("SKU-1005", "LOC-40"),
						new CollectionCheckItemRequest("SKU-1006", "LOC-50"),
						new CollectionCheckItemRequest("SKU-9999", "LOC-99")));

		CollectionCheckResponse response = post(request);
		List<CollectionCheckItemResponse> items = response.items();
		assertEquals(request.requestedAt(), response.requestedAt());
		assertEquals(List.of("SKU-1001", "SKU-1002", "SKU-1003", "SKU-1004", "SKU-1005", "SKU-1006", "SKU-9999"),
				items.stream().map(CollectionCheckItemResponse::sku).toList());
		assertEquals(List.of(true, false, false, true, true, true, false), items.stream()
				.map(CollectionCheckItemResponse::collectable).toList());
		assertEquals(List.of(1, 2, 1, 1, 1, 2, 0), items.stream()
				.map(item -> item.availability().size()).toList());
	}

	private CollectionCheckResponse post(CollectionCheckRequest request) throws Exception {
		HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + "/collection/check"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(request)))
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient()
				.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());
		return objectMapper.readValue(response.body(), CollectionCheckResponse.class);
	}
}
