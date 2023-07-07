public class TransactionsInfo {
    private String data;
    private double price;
    private double quantity;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setCantity(double quantity) {
        this.quantity = quantity;
    }

    public TransactionsInfo(String data, double quantity, double price){
        this.data = data;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Data= " + data +
                ", quantity= " + quantity +
                ", price= " + price +
                ".";
    }
}
