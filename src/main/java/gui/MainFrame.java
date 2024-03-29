package gui;

import backend.ProductSpecification;
import backend.Sale;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final Sale sale = new Sale();

    private void initComponents(String productFilePath) {
        Container contentPanel = getContentPane();

        final CartPanel rightPanel = new CartPanel((int) (getWidth() * 0.33));
        final MarketPanel leftPanel = new MarketPanel((int) (getWidth() * 0.66), productFilePath,
                rightPanel::addProduct, new ReloadProfileHandler() {
            @Override
            public void onReload() {
                rightPanel.setTableLoading(true);
                rightPanel.setEnabled(false);
            }

            @Override
            public void reloadDone() {
                rightPanel.setTableLoading(false);
                rightPanel.setEnabled(true);
                rightPanel.clear();
            }
        });
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation((int) (getWidth() * 0.66));
        splitPane.setEnabled(false);


        add(splitPane);
        setContentPane(contentPanel);
    }

    public MainFrame(String title, int width, int height, String productFilePath) {
        super(title);
        // centralize the frame
        setLocationRelativeTo(null);
        int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width, screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        setBounds(screenWidth / 2 - width / 2, screenHeight / 2 - height / 2, width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents(productFilePath);
    }

    public void display() {
        setVisible(true);
    }
}
