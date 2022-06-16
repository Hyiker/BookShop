package gui;

import backend.PricingStrategyFactory;
import backend.ProductSpecification;

import javax.swing.*;
import java.awt.*;

interface AddToCartHandler {
    void addToCart(ProductSpecification prodSpec, int copies);
}

public class MarketPanel extends JPanel {
    ProductSpecification[] productSpecifications = new ProductSpecification[]{
            new ProductSpecification("9787111186823", 18, "UML与模式应用", ProductSpecification.TEACHING),
            new ProductSpecification("9787505380004", 34, "Java与模式", ProductSpecification.COMPUTER),
            new ProductSpecification("9787508353937", 58, "HeadFirst 设计模式", ProductSpecification.COMPUTER),
            new ProductSpecification("9787115426901", 30, "爱丽丝历险记", ProductSpecification.COMICS),
            new ProductSpecification("9787504849649", 20, "煲汤大全", ProductSpecification.OTHER)};


    public MarketPanel(int width, AddToCartHandler addToCartHandler) {
        super(new BorderLayout());

        JLabel goodListTitle = new JLabel("商品列表");
        goodListTitle.setFont(new Font("微软雅黑", Font.BOLD, 20));
        goodListTitle.setVerticalAlignment(JLabel.CENTER);
        goodListTitle.setHorizontalAlignment(JLabel.CENTER);
        add(goodListTitle, BorderLayout.NORTH);

        String[] header = {"ISBN", "标题", "种类", "单价", "优惠"};
        BindTable<ProductSpecification> goodListTable = new BindTable<>(header, new RowConverter<ProductSpecification>() {
            @Override
            public Object[] getRow(ProductSpecification obj) {
                return new Object[]{obj.getIsbn(), obj.getTitle(), obj.getType(), obj.getPrice(),
                        PricingStrategyFactory.getInstance().getPricingStrategy(obj.getType()).getDescription()};
            }

            @Override
            public String getKey(ProductSpecification obj) {
                return obj.getIsbn();
            }
        });
        JScrollPane goodListScrollPanel = new JScrollPane(goodListTable);
        for (ProductSpecification ps : productSpecifications) {
            goodListTable.addItem(ps);
        }
        goodListTable.adjustColumnWidth();
        add(goodListScrollPanel, BorderLayout.CENTER);

        JPanel leftBottomPanel = new JPanel(new FlowLayout());
        JLabel totalTitle = new JLabel("数量");
        NumberField totalField = new NumberField();
        totalField.setPreferredSize(new Dimension(200, 30));
        JButton addButton = new JButton("添加");
        addButton.addActionListener(e -> {
            ProductSpecification ps = goodListTable.getSelection();
            int num = totalField.getNumber();
            if (ps == null) {
                JOptionPane.showMessageDialog(this, "请选择商品");
                return;
            } else if (num < 1) {
                JOptionPane.showMessageDialog(this, "数量不能小于1或为空");
                return;
            }
            addToCartHandler.addToCart(ps, num);
        });
        leftBottomPanel.setPreferredSize(new Dimension(width, 40));
        leftBottomPanel.add(totalTitle);
        leftBottomPanel.add(totalField);
        leftBottomPanel.add(addButton);

        add(leftBottomPanel, BorderLayout.SOUTH);
    }
}
