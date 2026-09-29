package com.interview.test.model;

import java.time.LocalDate;

public record InventoryLocationEntity(
		String sku,
		String locationId,
		LocalDate availableFrom,
		LocalDate availableUntil) {
}