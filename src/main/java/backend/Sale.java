package backend;

import java.util.ArrayList;

public class Sale {
    private final ArrayList<SaleLineItem> items = new ArrayList<>();

    public void addItem(SaleLineItem item) {
        items.add(item);
    }

    public double getTotal() {
        double total = 0.0;
        for (SaleLineItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
}
