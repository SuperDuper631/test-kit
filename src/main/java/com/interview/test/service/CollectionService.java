package com.interview.test.service;

import com.interview.test.model.AvailabilityPeriod;
import com.interview.test.model.CollectionCheckItemRequest;
import com.interview.test.model.CollectionCheckItemResponse;
import com.interview.test.model.CollectionCheckRequest;
import com.interview.test.model.CollectionCheckResponse;
import com.interview.test.model.InventoryLocationEntity;
import com.interview.test.repo.InventoryLocationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CollectionService {
	private final InventoryLocationRepository inventoryLocationRepository;

	public CollectionService(InventoryLocationRepository inventoryLocationRepository) {
		this.inventoryLocationRepository = inventoryLocationRepository;
	}

	public CollectionCheckResponse check(CollectionCheckRequest request) {
		LocalDate requestedDate = request.requestedAt().toLocalDate();
		List<CollectionCheckItemResponse> items = request.items().stream()
				.map(item -> checkItem(item, requestedDate))
				.toList();
		return new CollectionCheckResponse(request.requestedAt(), items);
	}

	private CollectionCheckItemResponse checkItem(CollectionCheckItemRequest item, LocalDate requestedDate) {
		List<InventoryLocationEntity> records = inventoryLocationRepository
				.findBySkuAndLocationId(item.sku(), item.locationId());
		List<AvailabilityPeriod> availability = records.stream()
				.map(record -> new AvailabilityPeriod(record.availableFrom(), record.availableUntil()))
				.toList();
		boolean collectable = records.stream().anyMatch(record ->
				!requestedDate.isBefore(record.availableFrom())
						&& !requestedDate.isAfter(record.availableUntil()));
		return new CollectionCheckItemResponse(item.sku(), item.locationId(), collectable, availability);
	}
}