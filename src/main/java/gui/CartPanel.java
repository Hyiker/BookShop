package gui;

import backend.Sale;
import backend.SaleLineItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CartPanel extends JPanel {
    private final Sale sale = new Sale();

    public CartPanel(int width) {
        super(new BorderLayout());

        JLabel goodListTitle = new JLabel("购物车");
        goodListTitle.setFont(new Font("微软雅黑", Font.BOLD, 20));
        goodListTitle.setVerticalAlignment(JLabel.CENTER);
        goodListTitle.setHorizontalAlignment(JLabel.CENTER);
        add(goodListTitle, BorderLayout.NORTH);

        String[] header = {"ISBN", "标题", "数量"};
        DefaultTableModel tableModel = new UnEditableTableModel(header, 0);
        JTable goodListTable = new JTable(tableModel);
        goodListTable.setCellSelectionEnabled(false);
        goodListTable.setRowSelectionAllowed(true);
        goodListTable.setRowHeight(20);
        goodListTable.setShowGrid(false);
        goodListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane goodListScrollPanel = new JScrollPane(goodListTable);
        add(goodListScrollPanel, BorderLayout.CENTER);

        JPanel leftBottomPanel = new JPanel(new FlowLayout());
        leftBottomPanel.setPreferredSize(new Dimension(width, 40));
        JLabel totalPrice = new JLabel("0$");
        totalPrice.setFont(new Font("微软雅黑", Font.BOLD, 30));
        JButton payButton = new JButton("结算");
        JButton clearButton = new JButton("清空");
        leftBottomPanel.add(totalPrice);
        leftBottomPanel.add(payButton);
        leftBottomPanel.add(clearButton);
        add(leftBottomPanel, BorderLayout.SOUTH);
    }
}
