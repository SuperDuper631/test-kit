package com.interview.test.service;

import com.interview.test.model.CollectionCheckItemRequest;
import com.interview.test.model.CollectionCheckRequest;
import com.interview.test.model.CollectionCheckResponse;
import com.interview.test.model.InventoryLocationEntity;
import com.interview.test.repo.InventoryLocationRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionServiceTest {
	@Test
	void returnsAllAvailabilityPeriodsAndPreservesRequestOrder() {
		InventoryLocationRepository repository = new InventoryLocationRepository();
		repository.add(new InventoryLocationEntity("SKU-TEST", "LOC-TEST",
				LocalDate.parse("2026-08-01"), LocalDate.parse("2026-08-31")));
		repository.add(new InventoryLocationEntity("SKU-TEST", "LOC-TEST",
				LocalDate.parse("2026-09-01"), LocalDate.parse("2026-09-30")));
		CollectionService service = new CollectionService(repository);

		CollectionCheckResponse response = service.check(new CollectionCheckRequest(
				OffsetDateTime.parse("2026-08-31T23:59:00Z"),
				List.of(
						new CollectionCheckItemRequest("SKU-TEST", "LOC-TEST"),
						new CollectionCheckItemRequest("SKU-2000", "LOC-TEST"))));

		assertEquals(2, response.items().size());
		assertTrue(response.items().get(0).collectable());
		assertEquals(2, response.items().get(0).availability().size());
		assertEquals("SKU-TEST", response.items().get(0).sku());
		assertFalse(response.items().get(1).collectable());
		assertTrue(response.items().get(1).availability().isEmpty());
	}

	@Test
	void initializesSampleInventoryRecord() {
		InventoryLocationRepository repository = new InventoryLocationRepository();

		assertEquals(1, repository.findBySkuAndLocationId("SKU-1001", "LOC-42").size());
	}
}