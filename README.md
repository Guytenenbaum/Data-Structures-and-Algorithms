# Stock Manager – Real-Time Stock Tracking (2–3 Trees)

A data-structures project that implements a stock tracking system with real-time updates and efficient queries over **current stock prices** and **price ranges**.

The system supports:
- Adding/removing stocks
- Updating stock prices over time
- Canceling (invalidating) a specific update by timestamp
- Querying the current price of a stock
- Querying how many stocks are in a price range
- Returning the list of stock IDs in a price range, sorted by price (tie-break by stockId)

---

## Files

- `StockManager.java` – main API / “system” object that exposes the required operations.
- `TTStocks.java` – 2–3 tree keyed by `stockId` (stores each stock and its current price).
- `TTPrice.java` – 2–3 tree keyed by `(price, stockId)` for ordering + range queries (also maintains `nextLeaf` links).
- `TTTimeStamp.java` – 2–3 tree keyed by `timestamp` for per-stock update history.
- `Node.java` – stock node (stockId, current price, timestamp history tree).
- `PNode.java` – price-tree node (stockId, current price, size, `nextLeaf` pointer).
- `LightNode.java` – timestamp-tree node (timestamp + price diff).
- `Main.java` – simple local test/demo.

---

## Data Structure Design (High Level)

### 1) `TTStocks` (by stockId)
A 2–3 tree where each **leaf** is a `Node` representing a stock:
- `stockId`
- `current price`
- `TTTimeStamp ttTimeStamp` (history of updates for this stock) - which is a 2-3 tree of its own

Used for:
- `addStock`, `removeStock`, `getStockPrice`, finding stock for updates

### 2) `TTPrice` (by current price, tie by stockId)
A 2–3 tree where each **leaf** is a `PNode` representing the stock’s **current** price.
Comparison is by:
1. price ascending
2. stockId lexicographic (tie-break)

Also maintains `nextLeaf` pointers between leaves to support fast in-order traversal for range output.

Used for:
- `getAmountStocksInPriceRange`
- `getStocksInPriceRange`

### 3) `TTTimeStamp` (per stock, by timestamp)
Each stock has its own 2–3 tree containing `LightNode(timestamp, diff)` entries.
This allows removing an update event by timestamp and adjusting the current price accordingly.

Used for:
- `removeStockTimestamp(stockId, timestamp)`

---

## Public API (StockManager)

### `initStocks()`
Initializes the system (creates the internal trees).

### `addStock(String stockId, long timestamp, float price)`
Adds a new stock with an initial price.

### `removeStock(String stockId)`
Removes the stock and all its history.

### `updateStock(String stockId, long timestamp, Float priceDifference)`
Adds a price update event (diff can be positive or negative, but not 0).

### `Float getStockPrice(String stockId)`
Returns current price.

### `removeStockTimestamp(String stockId, long timestamp)`
Invalidates/removes a specific update event by timestamp and updates the current stock price accordingly.

### `int getAmountStocksInPriceRange(Float price1, Float price2)`
Counts how many stocks have current price `p` such that `price1 <= p <= price2`.

### `String[] getStocksInPriceRange(Float price1, Float price2)`
Returns **exact-sized array** of stock IDs in `[price1, price2]`, sorted by price, and by `stockId` on ties.

---

## Input Validation / Exceptions

The implementation throws `IllegalArgumentException` for invalid actions such as:
- Adding an existing stockId
- Removing a missing stock
- Removing a missing timestamp update
- `priceDifference == 0`
- invalid range: `price1 > price2`
- non-positive initial price (price must be > 0)

---

## How to Run

### IntelliJ
Open the project → run `Main.java`.

### Command line
From the folder containing the `.java` files:
```bash
javac *.java
java Main
