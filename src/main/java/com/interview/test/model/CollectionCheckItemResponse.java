package com.interview.test.model;

import java.util.List;

public record CollectionCheckItemResponse(
		String sku,
		String locationId,
		boolean collectable,
		List<AvailabilityPeriod> availability) {
}