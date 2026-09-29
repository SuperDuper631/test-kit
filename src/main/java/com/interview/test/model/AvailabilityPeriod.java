package com.interview.test.model;

import java.time.LocalDate;

public record AvailabilityPeriod(LocalDate availableFrom, LocalDate availableUntil) {
}