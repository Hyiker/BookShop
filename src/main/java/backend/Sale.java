package backend;

import java.util.ArrayList;
import java.util.TreeMap;

public class Sale {
    private final TreeMap<String, SaleLineItem> items = new TreeMap<>();

    public SaleLineItem getItem(String isbn) {
        return items.get(isbn);
    }

    public void addItem(SaleLineItem item) {
        items.put(item.getProdSpec().getIsbn(), item);
    }

    public double getTotal() {
        double total = 0.0;
        for (SaleLineItem item : items.values()) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void clear() {
        items.clear();
    }

    public void removeItem(String isbn) {
        items.remove(isbn);
    }
}
