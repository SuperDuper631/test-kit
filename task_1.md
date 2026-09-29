
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

| Layer      | Class                         | What's provided                                                                    |
| ---------- | ----------------------------- | ---------------------------------------------------------------------------------- |
| Model      | `InventoryLocationEntity`     | Holds the SKU, location ID, and availability dates as `LocalDate` values.          |
| DTOs       | Collection check model classes | Request and response types are provided in the `model` package.                    |
| Repository | `InventoryLocationRepository` | An in-memory `Map<String, List<InventoryLocationEntity>>` and seed fixtures exist. |
| Controller | `CollectionController`        | The `POST /collection/check` route is declared; its handler is incomplete.         |
| Service    | `CollectionService`           | The `check` method is provided as a stub.                                          |

## Your task

Complete the TODOs so that the endpoint follows the business rules above. Use the in-memory map in `InventoryLocationRepository`; do not add a database or ORM.

Consider:

* How to store and return multiple availability records for the same SKU and location.
* How to handle items for which no matching records are found.
* How to evaluate the requested timestamp using its calendar date.
* How to preserve the request order in the response.
* Keeping map access in the repository and business decisions in the service.

You may complete or modify the existing repository methods as needed. The request and response DTOs are already provided.

## Seed fixtures

The repository's `seedData()` method loads these records into the in-memory map. Rows with the same SKU and location represent separate availability periods.

| SKU        | Location ID | Available from | Available until |
| ---------- | ----------- | -------------- | --------------- |
| `SKU-1001` | `LOC-42`    | 2026-08-01     | 2026-08-31      |
| `SKU-1002` | `LOC-10`    | 2026-08-01     | 2026-08-05      |
| `SKU-1002` | `LOC-10`    | 2026-08-20     | 2026-08-31      |
| `SKU-1003` | `LOC-20`    | 2026-07-01     | 2026-07-31      |
| `SKU-1004` | `LOC-30`    | 2026-08-15     | 2026-08-20      |
| `SKU-1005` | `LOC-40`    | 2026-08-01     | 2026-08-15      |
| `SKU-1006` | `LOC-50`    | 2026-07-01     | 2026-07-10      |
| `SKU-1006` | `LOC-50`    | 2026-08-10     | 2026-08-25      |