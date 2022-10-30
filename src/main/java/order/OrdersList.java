package order;

import java.util.List;

public class OrdersList {
    private boolean success;
    private List<Order> orders;
    private int total;
    private int totalToday;
    public OrdersList() {
        }

    public OrdersList(boolean success, List<Order> orders, int total, int totalToday) {
        this.success = success;
        this.orders = orders;
        this.total = total;
        this.totalToday = totalToday;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalToday() {
        return totalToday;
    }

    public void setTotalToday(int totalToday) {
        this.totalToday = totalToday;
    }
}
