public class StockManager {
    private TTStocks stocks;
    private TTPrice prices;
    private static int N = 1; // total number of stocks
    //private TTTimeStamp  tttimeStamp;

    public StockManager() {

    }

    // 1. Initialize the system
    public void initStocks() {
        stocks = new TTStocks();
        prices = new TTPrice();
    }

    // 2. Add a new stock
    public void addStock(String stockId, long timestamp, float price) {
        Node newLeaf = new Node(stockId, timestamp, price);
        if(timestamp <= 0){
            throw new IllegalArgumentException("Timestamp must be greater than zero");
        }
        if (this.stocks.search(this.stocks.getRoot(), newLeaf.stockId) != null){
            throw new IllegalArgumentException("Cannot add a stock with an already used name");
        }
        PNode priceLeaf = new PNode(stockId, timestamp , price, newLeaf.size);
        this.stocks.Insert(stocks,newLeaf);
        this.prices.Insert(priceLeaf);
    }

    // 3. Remove a stock
    public void removeStock(String stockId) {
        Node UnwantedStock = stocks.search(stocks.getRoot(), stockId);
        if (UnwantedStock == null) {
            throw new IllegalArgumentException("Cannot remove a null Stock");
        }
        PNode UnwantedPrice = prices.search(prices.getRoot(), UnwantedStock.stockId, UnwantedStock.price);
        this.stocks.Delete(UnwantedStock);
        this.prices.Delete(prices, UnwantedPrice);
    }

    // 4. Update a stock price
    public void updateStock(String stockId, long timestamp, Float priceDifference) {
        this.prices.printTree(this.prices.getRoot(), "");
        if (priceDifference == 0){
            throw new IllegalArgumentException("Price difference must not be zero");
        }
        Node stock = stocks.search(stocks.getRoot(),stockId);//searching the leaf(stock) of the egiven stock
        PNode oldPrice = prices.search(this.prices.getRoot(), stockId, stock.getPrice());
        prices.Delete(this.prices, oldPrice );
        stock.updatePrice(priceDifference);// updating the info in stocks tree
        PNode stockPriceNode = new PNode(stockId, timestamp, stock.getPrice(), stock.size); //update the price in price tree
        prices.Insert(stockPriceNode); // updating TTprice
        LightNode lightLeaf = new LightNode(timestamp, priceDifference);
        stock.ttTimeStamp.Insert(stock.ttTimeStamp, lightLeaf);

    }

    // 5. Get the current price of a stock
    public Float getStockPrice(String stockId) {
        Node stock = stocks.search(stocks.getRoot(),stockId);
        if (stock == null) {
            throw new IllegalArgumentException();
        }
        return stock.getPrice();
    }

    // 6. Remove a specific timestamp from a stock's history
    public void removeStockTimestamp(String stockId, long timestamp) {
        Node stock = stocks.search(stocks.getRoot(),stockId);
        PNode stockPrice = prices.search(this.prices.getRoot(), stock.stockId, stock.price);
        LightNode lightLeaf = stock.ttTimeStamp.search(stock.ttTimeStamp.getRoot(), timestamp);
        if (lightLeaf == null) {
            throw new IllegalArgumentException("Cannot remove a unexisting Timestamp");
        }
        stock.updatePrice(-(lightLeaf.getDiff()));
        stock.ttTimeStamp.Delete(stock.ttTimeStamp, lightLeaf);
        stockPrice.updatePrice(-(lightLeaf.getDiff()));
    }

    // 7. Get the amount of stocks in a given price range
    public int getAmountStocksInPriceRange(Float price1, Float price2) {
        if(price1 > price2) {
            throw new IllegalArgumentException("Price1 cannot be greater than Price2");
        }
        this.prices.printTree(this.prices.getRoot(), "");
        PNode min = prices.Search_Min(prices.getRoot(), price1);
        PNode max = prices.Search_Max(prices.getRoot(), price2);
        if (min.size == 0 && max.size != 0){
            return(prices.Rank(prices.Search_Max(prices.getRoot(), price2)));
        }
        return prices.Rank(prices.Search_Max(prices.getRoot(), price2)) - prices.Rank(prices.Search_Min(prices.getRoot(), price1));
    }

    // 8. Get a list of stock IDs within a given price range
    public String[] getStocksInPriceRange(Float price1, Float price2) {
        if (price1 > price2) {
            throw new IllegalArgumentException("Price1 cannot be greater than Price2");
        }

        int count = getAmountStocksInPriceRange(price1, price2);

        if (count == 0) {
            return new String[0]; // Return empty array if no stocks are found
        }
        PNode x = prices.Search_Min(prices.getRoot(), price1);
        if (x == null) {
            return new String[0]; // Handle case where no matching node exists
        }
        String[] stocksInPriceRange = new String[count];
        for (int i = 0; i < count && x != null; i++) {
            if (x.getPrice() < 0){
                x = x.nextLeaf;
            }
            stocksInPriceRange[i] = x.stockId;
            x = x.nextLeaf;
        }

        return stocksInPriceRange;
    }
}
