
# Scenario

A customer wants to know whether the items in their basket can be collected from a selected pickup location at a requested time.

## Endpoint

`POST /collection/check`

## Request body

```json
{
  "requestedAt": "2026-08-15T14:30:00Z",
  "items": [
    {
      "sku": "SKU-1001",
      "locationId": "LOC-42"
    }
  ]
}
```

## Expected response

```json
{
  "requestedAt": "2026-08-15T14:30:00Z",
  "items": [
    {
      "sku": "SKU-1001",
      "locationId": "LOC-42",
      "collectable": true,
      "availability": [
        {
          "availableFrom": "2026-08-01",
          "availableUntil": "2026-08-31"
        }
      ]
    }
  ]
}
```

## Business rules

* For every item, find all `inventory_location` records matching the supplied `sku` and `locationId`.
* An item is collectable when the requested date falls inside at least one availability period:
  `available_from <= requested_date <= available_until`.
* The response must include every availability period found for the item/location combination, even when none of those periods contain the requested date.
* When no matching `inventory_location` record exists, return `collectable: false` and an empty `availability` array.
* The requested timestamp should be evaluated using its calendar date; the time component does not affect the availability check.
* The order of the items in the response should match the order supplied in the request.

## What's already there

| Layer      | Class                         | Notes                                                                                             |
| ---------- | ----------------------------- | ------------------------------------------------------------------------------------------------- |
| Entity     | `InventoryLocationEntity`     | Maps the `inventory_location` table; contains `availableFrom` and `availableUntil` as `LocalDate` |
| Repository | `InventoryLocationRepository` | Currently exposes `findBySkuAndLocationId`, which returns only one matching record                |
| Controller | `CollectionController`        | Contains a placeholder `GET /collection/hello`; replace or extend it                              |
| Service    | `CollectionService`           | Empty; implement the required business logic here                                                 |

## Your task

Implement the endpoint and service logic so that the API follows the business rules above.

Consider:

* How the repository should behave when multiple availability records exist.
* How to handle items for which no matching database records are found.
* How to convert the requested timestamp into the date used by the business rules.
* How the response DTOs should be structured.
* Keeping database access in the repository layer and business decisions in the service layer.

You may modify existing repository methods or add new ones where necessary.

INSERT INTO inventory_location (
    sku,
    location_id,
    available_from,
    available_until
) VALUES
-- 1. Fully available for the requested date
('SKU-1001', 'LOC-42', '2026-08-01', '2026-08-31'),

-- 2. Multiple availability windows for the same SKU/location
('SKU-1002', 'LOC-10', '2026-08-01', '2026-08-05'),
('SKU-1002', 'LOC-10', '2026-08-20', '2026-08-31'),

-- 3. Window exists, but requested date is outside it
('SKU-1003', 'LOC-20', '2026-07-01', '2026-07-31'),

-- 4. Requested date is exactly the start date
('SKU-1004', 'LOC-30', '2026-08-15', '2026-08-20'),

-- 5. Requested date is exactly the end date
('SKU-1005', 'LOC-40', '2026-08-01', '2026-08-15'),

-- 6. Multiple windows, one matches
('SKU-1006', 'LOC-50', '2026-07-01', '2026-07-10'),
('SKU-1006', 'LOC-50', '2026-08-10', '2026-08-25');