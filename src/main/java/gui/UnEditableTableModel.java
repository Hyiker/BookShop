package gui;

import javax.swing.table.DefaultTableModel;

public class UnEditableTableModel extends DefaultTableModel {
    public UnEditableTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
