package gui;

import backend.ProductSpecification;
import backend.Sale;
import backend.SaleLineItem;

import javax.swing.*;
import java.awt.*;

public class CartPanel extends JPanel {
    private final Sale sale = new Sale();
    private final BindTable<SaleLineItem> goodListTable;
    private final JLabel totalPriceLabel;
    private double totalPrice = 0.0;

    public void addProduct(ProductSpecification prodSpec, int copies) {
        SaleLineItem item = sale.getItem(prodSpec.getIsbn());
        if (item == null) {
            item = new SaleLineItem(copies, prodSpec);
        } else {
            item.setCopies(item.getCopies() + copies);
        }

        sale.addItem(item);
        goodListTable.addItem(item, true);
        goodListTable.adjustColumnWidth();
        updateTotalPriceLabel();
    }

    void setTableLoading(boolean loading) {
        goodListTable.setLoading(loading);
    }

    void clear() {
        sale.clear();
        goodListTable.clear();
        updateTotalPriceLabel();
    }


    private void updateTotalPriceLabel() {
        // TODO: update price label with animated effect
        totalPrice = sale.getTotal();
        double totalPrev = totalPrice;
        totalPriceLabel.setText(String.format("$%.1f", totalPrice));
    }

    public CartPanel(int width) {
        super(new BorderLayout());

        JLabel goodListTitle = new JLabel("购物车");
        goodListTitle.setFont(new Font("微软雅黑", Font.BOLD, 20));
        goodListTitle.setVerticalAlignment(JLabel.CENTER);
        goodListTitle.setHorizontalAlignment(JLabel.CENTER);
        add(goodListTitle, BorderLayout.NORTH);

        String[] header = {"ISBN", "标题", "数量"};
        goodListTable = new BindTable<>(header, new RowConverter<SaleLineItem>() {
            @Override
            public Object[] getRow(SaleLineItem obj) {
                return new Object[]{obj.getProdSpec().getIsbn(), obj.getProdSpec().getTitle(), obj.getCopies()};
            }

            @Override
            public String getKey(SaleLineItem obj) {
                return obj.getProdSpec().getIsbn();
            }
        });
        JScrollPane goodListScrollPanel = new JScrollPane(goodListTable);
        add(goodListScrollPanel, BorderLayout.CENTER);

        JPanel leftBottomPanel = new JPanel(new FlowLayout());
        leftBottomPanel.setPreferredSize(new Dimension(width, 40));
        totalPriceLabel = new JLabel();
        totalPriceLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        JButton payButton = new JButton("结算");
        payButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, String.format("总价: $%.1f，是否结算", totalPrice),
                    "结算", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(this, "结算成功");
                sale.clear();
                goodListTable.clear();
                updateTotalPriceLabel();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                JOptionPane.showMessageDialog(this, "取消结算");
            }
        });
        JButton removeButton = new JButton("删除");
        removeButton.addActionListener(e -> {
            SaleLineItem item = goodListTable.removeSelection();
            if (item != null) {
                sale.removeItem(item.getProdSpec().getIsbn());
            }
            updateTotalPriceLabel();
        });
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> {
            goodListTable.clear();
            sale.clear();
            updateTotalPriceLabel();
        });
        updateTotalPriceLabel();
        leftBottomPanel.add(totalPriceLabel);
        leftBottomPanel.add(payButton);
        leftBottomPanel.add(removeButton);
        leftBottomPanel.add(clearButton);
        add(leftBottomPanel, BorderLayout.SOUTH);
    }
}
