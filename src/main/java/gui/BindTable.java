package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.TreeMap;

interface RowConverter<T> {
    Object[] getRow(T obj);

    String getKey(T obj);
}

interface RowConflictResolver<T> {
    boolean resolve(T objOld, T objNew);
}

// T is bind class type
public class BindTable<T> extends JTable {
    private final TreeMap<String, T> bindMap = new TreeMap<>();
    private final RowConverter<T> converter;
    private final int keyColumnIndex;

    @Override
    public void repaint() {
        super.repaint();
    }

    private void addRow(Object[] rowData) {
        DefaultTableModel tableModel = (DefaultTableModel) getModel();
        tableModel.addRow(rowData);
    }

    private void updateRow(int index, Object[] rowData) {
        DefaultTableModel tableModel = (DefaultTableModel) getModel();
        for (int i = 0; i < rowData.length; i++) {
            tableModel.setValueAt(rowData[i], index, i);
        }
    }

    private void removeRow(int index) {
        DefaultTableModel tableModel = (DefaultTableModel) getModel();
        tableModel.removeRow(index);
    }

    public void addItem(T item) {
        addItem(item, false);
    }

    public void addItem(T obj, boolean updateIfExist) {
        String key = converter.getKey(obj);
        if (bindMap.containsKey(key) && updateIfExist) {
            bindMap.put(key, obj);
            // find corresponding row
            // TODO optimize this O(n) algorithm
            int rowIndex = -1;
            for (int i = 0; i < getRowCount(); i++) {
                if (getValueAt(i, keyColumnIndex).equals(key)) {
                    rowIndex = i;
                    break;
                }
            }
            if (rowIndex != -1) {
                updateRow(rowIndex, converter.getRow(obj));
            }
            return;

        }
        bindMap.put(converter.getKey(obj), obj);
        addRow(converter.getRow(obj));
    }

    public T getSelection() {
        int selectedRow = getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        String key = (String) getModel().getValueAt(selectedRow, keyColumnIndex);
        return bindMap.get(key);
    }

    public T removeSelection() {
        int selectedRow = getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        String key = (String) getModel().getValueAt(selectedRow, keyColumnIndex);
        T obj = bindMap.remove(key);
        bindMap.remove(key);
        removeRow(selectedRow);
        return obj;
    }

    public void adjustColumnWidth() {
        final TableColumnModel columnModel = getColumnModel();
        for (int column = 0; column < getColumnCount(); column++) {
            int width = 45; // Min width
            for (int row = 0; row < getRowCount(); row++) {
                TableCellRenderer renderer = getCellRenderer(row, column);
                Component comp = prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    public BindTable(Object[] header, RowConverter<T> converter) {
        this(header, converter, 0);
    }

    public BindTable(Object[] header, RowConverter<T> converter, int keyColumnIndex) {
        super(new UnEditableTableModel(header, 0));
        this.keyColumnIndex = keyColumnIndex;
        this.converter = converter;
        setCellSelectionEnabled(false);
        setRowSelectionAllowed(true);
        setRowHeight(20);
        setShowGrid(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void clear() {
        bindMap.clear();
        ((DefaultTableModel) getModel()).setRowCount(0);
    }
}
