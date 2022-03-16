package prolog;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class Conditions implements TableModel, Cloneable {
    private final List<TableModelListener> listeners = new ArrayList<>();
    private static final String[] columnNames = {"Номер", "Описание","Ассоциирумый вопрос", "Объяснение"};
    public List<Condition> list;

    public Conditions() {
        this.list = new ArrayList<>();
    }

    public Conditions(List<Condition> list) {
        this.list = list == null ? new ArrayList<>() : list;
    }

    //model stuff
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
            case 0, 2 -> Integer.class;
            case 1, 3 -> String.class;
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
            case 0 -> list.get(rowIndex).id;
            case 1 -> list.get(rowIndex).desc;
            case 2 -> list.get(rowIndex).ass_question;
            case 3 -> list.get(rowIndex).expl;
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 0 -> list.get(rowIndex).id = (Integer)aValue;
            case 1 -> list.get(rowIndex).desc = (String)aValue;
            case 2 -> list.get(rowIndex).ass_question = (Integer)aValue;
            case 3 -> list.get(rowIndex).expl = (String)aValue;
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
    public Conditions clone() {
        try {
            Conditions clone = (Conditions) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    public void addNew(){
        list.add(new Condition());
    }
    public void remove(int id){
        list.remove(id);
    }
    public void setValue(int id, Condition condition){
        list.set(id, condition);
    }
    public Condition getValue(int id){
        return list.get(id);
    }
    public int size(){
        return list.size();
    }
}
