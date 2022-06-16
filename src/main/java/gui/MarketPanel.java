package gui;

import backend.PricingStrategyFactory;
import backend.ProductSpecification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MarketPanel extends JPanel {
    public MarketPanel(int width) {
        super(new BorderLayout());

        JLabel goodListTitle = new JLabel("商品列表");
        goodListTitle.setFont(new Font("微软雅黑", Font.BOLD, 20));
        goodListTitle.setVerticalAlignment(JLabel.CENTER);
        goodListTitle.setHorizontalAlignment(JLabel.CENTER);
        add(goodListTitle, BorderLayout.NORTH);

        String[] header = {"ISBN", "标题", "种类", "单价", "优惠"};
        DefaultTableModel tableModel = new UnEditableTableModel(header, 0);
        JTable goodListTable = new JTable(tableModel);
        goodListTable.setCellSelectionEnabled(false);
        goodListTable.setRowSelectionAllowed(true);
        goodListTable.setRowHeight(20);
        goodListTable.setShowGrid(false);
        goodListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane goodListScrollPanel = new JScrollPane(goodListTable);

        for (int i = 0; i < 100; i++) {
            ProductSpecification ps = new ProductSpecification("" + i, 10.0, "UML", ProductSpecification.COMPUTER);
            String discount = PricingStrategyFactory.getInstance().getPricingStrategy(ps.getType()).getDescription();
            tableModel.addRow(new Object[]{ps.getIsbn(), ps.getTitle(), ProductSpecification.getTypeName(ps.getType()),
                    ps.getPrice(), discount});
        }
        add(goodListScrollPanel, BorderLayout.CENTER);

        JPanel leftBottomPanel = new JPanel(new FlowLayout());
        JLabel totalTitle = new JLabel("数量");
        JTextField totalField = new JTextField();
        totalField.setPreferredSize(new Dimension(200, 30));
        JButton addButton = new JButton("添加");
        leftBottomPanel.setPreferredSize(new Dimension(width, 40));
        leftBottomPanel.add(totalTitle);
        leftBottomPanel.add(totalField);
        leftBottomPanel.add(addButton);

        add(leftBottomPanel, BorderLayout.SOUTH);
    }
}
