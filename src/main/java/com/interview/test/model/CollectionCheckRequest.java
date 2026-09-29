package com.interview.test.model;

import java.time.OffsetDateTime;
import java.util.List;

public record CollectionCheckRequest(
		OffsetDateTime requestedAt,
		List<CollectionCheckItemRequest> items) {
}