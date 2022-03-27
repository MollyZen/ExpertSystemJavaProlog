import prolog.Condition;
import prolog.Conditions;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

public class ChangeConditionList extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable table1;

    private final Model model;
    private final List<Integer> included;

    public ChangeConditionList(Dialog owner, List<Integer> included, Conditions conditions) {
        super(owner);
        this.included = included;
        model = new Model(included, conditions.clone());
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                ChangeConditionList.this.revalidate();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        table1.setModel(model);
        ((TableRowSorter) table1.getRowSorter()).setSortsOnUpdates(true);
        table1.getTableHeader().setReorderingAllowed(false);
        table1.getRowSorter().toggleSortOrder(1);

        table1.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                Model tableModel = (Model) table.getModel();

                if ((Boolean) tableModel.getValueAt(row, 0) == Boolean.FALSE && tableModel.includedQuestions.contains(tableModel.getValueAt(table.convertRowIndexToModel(row), 3))) {
                    l.setBackground(Color.yellow);
                } else {
                    l.setBackground(Color.white);
                }
                return l;
            }
        });
    }

    private void onOK() {
        included.clear();
        for (Integer key : model.selected.keySet()) {
            if (model.selected.get(key).equals(Boolean.TRUE)) {
                included.add(key);
            }
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private static class Model implements TableModel {
        private static final String[] columnNames = {"Выбрано", "Номер", "Условие", "Ассоциируемый вопрос"};
        Conditions conditions;
        Map<Integer, Boolean> selected;
        private final List<Integer> includedQuestions = new ArrayList<>();
        private final List<TableModelListener> listeners = new ArrayList<>();

        public Model(List<Integer> included, Conditions conditions) {
            selected = new HashMap<>();
            this.conditions = new Conditions();
            Map<Integer, Condition> tmpMap = new HashMap<>();
            for (Condition cond : conditions.list) {
                if (!tmpMap.containsKey(cond.id)) {
                    tmpMap.put(cond.id, cond);
                }
            }
            this.conditions.list = tmpMap.values().stream().toList();
            for (Condition cond : this.conditions.list) {
                if (included.contains(cond.id)) {
                    selected.put(cond.id, Boolean.TRUE);
                }
            }
            for (Condition cond : conditions.list) {
                if (selected.containsKey(cond.id) && !includedQuestions.contains(cond.ass_question)) {
                    includedQuestions.add(cond.ass_question);
                }
            }
        }

        @Override
        public int getRowCount() {
            return conditions.size();
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
            return switch (columnIndex) {
                case 0 -> Boolean.class;
                case 1 -> Integer.class;
                case 2 -> String.class;
                case 3 -> Integer.class;
                default -> Object.class;
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return switch (columnIndex) {
                case 0 -> Objects.requireNonNullElse(selected.get(conditions.getValue(rowIndex).id), Boolean.FALSE);
                case 1 -> conditions.getValue(rowIndex).id;
                case 2 -> conditions.getValue(rowIndex).desc;
                case 3 -> conditions.getValue(rowIndex).ass_question;
                default -> null;
            };
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                if (includedQuestions.contains(conditions.getValue(rowIndex).ass_question)) {
                    for (Condition id : conditions.list) {
                        if (id.ass_question != -1 && id.ass_question.equals(conditions.getValue(rowIndex).ass_question)) {
                            selected.put(id.id, Boolean.FALSE);
                            for (int i = 0; i < this.getRowCount(); ++i) {
                                if (this.getValueAt(i, 1) == id.id) {
                                    this.fireDataChanged(i, 1);
                                    break;
                                }
                            }
                        }
                    }
                }
                selected.put(conditions.getValue(rowIndex).id, (Boolean) aValue);
                if ((Boolean) aValue) {
                    includedQuestions.add(conditions.getValue(rowIndex).ass_question);
                } else {
                    includedQuestions.remove(conditions.getValue(rowIndex).ass_question);
                }
                this.fireDataChanged(rowIndex, columnIndex);
            }
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {

        }

        public void fireDataChanged(int row, int col) {
            for (var lister : listeners) {
                lister.tableChanged(new TableModelEvent(this, row, row, col));
            }
        }
    }

    public void run() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        buttonOK = new JButton();
        buttonOK.setText("ОК");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(buttonOK, gbc);
        buttonCancel = new JButton();
        buttonCancel.setText("Отмена");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(buttonCancel, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel3, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(scrollPane1, gbc);
        table1 = new JTable();
        table1.setAutoCreateRowSorter(true);
        scrollPane1.setViewportView(table1);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
