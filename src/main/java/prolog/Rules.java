package prolog;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class Rules implements TableModel, Cloneable {
    private final List<TableModelListener> listeners = new ArrayList<>();
    private static final String[] columnNames = {"Результат", "Условия"};
    public List<Rule> list;

    public Rules(List<Rule> list) {
        this.list = list == null ? new ArrayList<>() : list;
    }

    public Rules() {
        list = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex){
            case 0 -> Boolean.class;
            case 1 -> Integer[].class;
            default -> Object.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return switch (columnIndex){
            case 0 -> list.get(rowIndex).result;
            case 1 -> list.get(rowIndex).conditions;
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 0 -> list.get(rowIndex).result = (Boolean) aValue;
            case 1 -> list.get(rowIndex).conditions = (List<Integer>) aValue;
        }
        fireDataChanged(rowIndex, columnIndex);
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }
    public void fireDataChanged(int row, int col){
        for (var lister : listeners){
            lister.tableChanged(new TableModelEvent(this, row, row, col));
        }
    }

    @Override
    public Rules clone() {
        try {
            Rules clone = (Rules) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    public void addNew(){
        list.add(new Rule());
    }
    public void remove(int id){
        list.remove(id);
    }
    public void setValue(int id, Rule rule){
        list.set(id, rule);
    }
    public Rule getValue(int id){
        return list.get(id);
    }
    public int size(){
        return list.size();
    }
}
