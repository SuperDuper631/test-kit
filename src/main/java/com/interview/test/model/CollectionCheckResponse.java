package com.interview.test.model;

import java.time.OffsetDateTime;
import java.util.List;

public record CollectionCheckResponse(
		OffsetDateTime requestedAt,
		List<CollectionCheckItemResponse> items) {
}