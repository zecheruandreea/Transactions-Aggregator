public class TransactionsInfo {
    private String data;
    private String type;
    private double price;
    private double quantity;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public TransactionsInfo(String data, double quantity, double price){
        this.data = data;
        this.quantity = quantity;
        this.price = price;
    }

    public TransactionsInfo(String data, String type, double quantity, double price){
        this.data = data;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Data= " + getData() +
                ", quantity= " + getQuantity() +
                ", price= " + getPrice() +
                "\n";
    }
}
