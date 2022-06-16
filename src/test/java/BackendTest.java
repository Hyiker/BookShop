import backend.ProductSpecification;
import backend.Sale;
import backend.SaleLineItem;
import org.junit.Test;

import static org.junit.Assert.*;

public class BackendTest {
    @Test
    public void test() {
        Sale sale = new Sale();
        sale.addItem(new SaleLineItem(2, new ProductSpecification("9787111186823", 18, "UML与模式应用", ProductSpecification.TEACHING)));
        sale.addItem(new SaleLineItem(2, new ProductSpecification("9787505380004", 34, "Java与模式", ProductSpecification.COMPUTER)));
        sale.addItem(new SaleLineItem(1, new ProductSpecification("9787508353937", 58, "HeadFirst 设计模式", ProductSpecification.COMPUTER)));
        sale.addItem(new SaleLineItem(3, new ProductSpecification("9787115426901", 30, "爱丽丝历险记", ProductSpecification.COMICS)));
        sale.addItem(new SaleLineItem(1, new ProductSpecification("9787504849649", 20, "煲汤大全", ProductSpecification.OTHER)));
        double total = sale.getTotal();
        assertEquals(259.92, total, 0.001);
    }
}
