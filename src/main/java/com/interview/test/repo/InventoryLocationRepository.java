package com.interview.test.repo;

import com.interview.test.model.InventoryLocationEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InventoryLocationRepository {
	private final Map<String, List<InventoryLocationEntity>> records = new ConcurrentHashMap<>();

	public InventoryLocationRepository() {
		seedData();
	}

    public void add(InventoryLocationEntity record) {
		records.computeIfAbsent(key(record.sku(), record.locationId()), ignored -> new CopyOnWriteArrayList<>())
				.add(record);
	}

	public List<InventoryLocationEntity> findBySkuAndLocationId(String sku, String locationId) {
		return List.copyOf(records.getOrDefault(key(sku, locationId), List.of()));
	}

	private String key(String sku, String locationId) {
		return sku.length() + ":" + sku + locationId;
	}

    private void seedData() {
       add(new InventoryLocationEntity("SKU-1001", "LOC-42",
				LocalDate.parse("2026-08-01"), LocalDate.parse("2026-08-31")));
	       add(new InventoryLocationEntity("SKU-1002", "LOC-10",
		       LocalDate.parse("2026-08-01"), LocalDate.parse("2026-08-05")));
	       add(new InventoryLocationEntity("SKU-1002", "LOC-10",
		       LocalDate.parse("2026-08-20"), LocalDate.parse("2026-08-31")));
	       add(new InventoryLocationEntity("SKU-1003", "LOC-20",
		       LocalDate.parse("2026-07-01"), LocalDate.parse("2026-07-31")));
	       add(new InventoryLocationEntity("SKU-1004", "LOC-30",
		       LocalDate.parse("2026-08-15"), LocalDate.parse("2026-08-20")));
	       add(new InventoryLocationEntity("SKU-1005", "LOC-40",
		       LocalDate.parse("2026-08-01"), LocalDate.parse("2026-08-15")));
	       add(new InventoryLocationEntity("SKU-1006", "LOC-50",
		       LocalDate.parse("2026-07-01"), LocalDate.parse("2026-07-10")));
	       add(new InventoryLocationEntity("SKU-1006", "LOC-50",
		       LocalDate.parse("2026-08-10"), LocalDate.parse("2026-08-25")));
    }
}