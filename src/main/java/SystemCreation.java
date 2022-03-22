import prolog.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SystemCreation extends JDialog {
    private JPanel mainPanel;
    private JButton saveButton;
    private JButton exitButton;

    private JTable condTable;
    private JTable questionTable;
    private JTable ruleTable;

    private JButton removeCondButton;
    private JButton removeQuestionButton;
    private JButton removeRuleButton;
    private JButton addRuleButton;
    private JButton addQuestionButton;
    private JButton addCondButton;

    public final Conditions conds;
    public final Questions questions;
    public final Rules rules;

    public final SystemCreationChecker checker;

    public SystemCreation(Frame owner, Conditions conds, Questions questions, Rules rules) {
        super(owner);
        this.conds = conds == null ? new Conditions() : conds.clone();
        this.questions = questions == null ? new Questions() : questions.clone();
        this.rules = rules == null ? new Rules() : rules.clone();

        checker = new SystemCreationChecker(this.conds, this.questions, this.rules);

        condTable.setModel(this.conds);
        condTable.setAutoCreateRowSorter(true);
        ((TableRowSorter) condTable.getRowSorter()).setSortsOnUpdates(true);
        condTable.getRowSorter().toggleSortOrder(0);
        condTable.getTableHeader().setReorderingAllowed(false);
        addCondButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SystemCreation.this.conds.addNew();
                condTable.getRowSorter().rowsInserted(condTable.getModel().getRowCount() - 1, condTable.getModel().getRowCount() - 1);
                condTable.revalidate();
            }
        });
        removeCondButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (condTable.getSelectedRow() > -1 && condTable.getSelectedRow() < condTable.getRowCount()) {
                    if (!checker.isThisDuplicateCondId(SystemCreation.this.conds.getValue(condTable.convertRowIndexToModel(condTable.getSelectedRow())))) {
                        checker.condIdMap.remove(SystemCreation.this.conds.getValue(condTable.convertRowIndexToModel(condTable.getSelectedRow())).id);
                    }
                    SystemCreation.this.conds.remove(condTable.convertRowIndexToModel(condTable.getSelectedRow()));
                    if (SystemCreation.this.conds.size() == 0) {
                        condTable.getRowSorter().allRowsChanged();
                    } else {
                        condTable.getRowSorter().rowsDeleted(condTable.getModel().getRowCount() - 1, condTable.getModel().getRowCount() - 1);
                    }
                    condTable.revalidate();
                    ruleTable.repaint();
                }
            }
        });
        this.conds.addTableModelListener(condTable);
        this.conds.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                ruleTable.repaint();
            }
        });
        this.conds.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getColumn() == 0) {
                    Condition localCond = SystemCreation.this.conds.getValue(e.getFirstRow());
                    if (checker.condIdMap.containsValue(localCond)) {
                        Integer id = checker.condIdMap.entrySet().stream().filter(c -> c.getValue() == localCond).toList().get(0).getKey();
                        checker.condIdMap.remove(id);
                    }
                }
            }
        });
        condTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                Conditions tableModel = (Conditions) table.getModel();


                if (checker.isThisDuplicateCondId(tableModel.getValue(table.getRowSorter().convertRowIndexToModel(row)))) {
                    l.setBackground(Color.pink);
                } else {
                    l.setBackground(Color.white);
                }

                return l;
            }
        });

        questionTable.setModel(this.questions);
        questionTable.setAutoCreateRowSorter(true);
        ((TableRowSorter) questionTable.getRowSorter()).setSortsOnUpdates(true);
        questionTable.getRowSorter().toggleSortOrder(0);
        questionTable.getTableHeader().setReorderingAllowed(false);
        addQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SystemCreation.this.questions.addNew();
                questionTable.getRowSorter().rowsInserted(questionTable.getModel().getRowCount() - 1, questionTable.getModel().getRowCount() - 1);
                questionTable.revalidate();
            }
        });
        removeQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (questionTable.getSelectedRow() > -1 && questionTable.getSelectedRow() < questionTable.getRowCount()) {
                    if (!checker.isThisDuplicateQuestionId(SystemCreation.this.questions.getValue(questionTable.convertRowIndexToModel(condTable.getSelectedRow())))) {
                        checker.queIdMap.remove(SystemCreation.this.questions.getValue(questionTable.convertRowIndexToModel(questionTable.getSelectedRow())).id);
                    }
                    SystemCreation.this.questions.remove(questionTable.convertRowIndexToModel(questionTable.getSelectedRow()));
                    if (SystemCreation.this.questions.size() == 0) {
                        questionTable.getRowSorter().allRowsChanged();
                    } else {
                        questionTable.getRowSorter().rowsDeleted(questionTable.getModel().getRowCount() - 1, questionTable.getModel().getRowCount() - 1);
                    }
                    questionTable.revalidate();
                }
            }
        });
        this.questions.addTableModelListener(questionTable);
        questionTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                Questions tableModel = (Questions) table.getModel();

                if (checker.isThisDuplicateQuestionId(tableModel.getValue(table.getRowSorter().convertRowIndexToModel(row)))) {
                    l.setBackground(Color.pink);
                } else {
                    l.setBackground(Color.white);
                }

                return l;
            }
        });

        this.rules.addTableModelListener(ruleTable);
        ruleTable.setModel(this.rules);
        ruleTable.setAutoCreateRowSorter(true);
        ((TableRowSorter) ruleTable.getRowSorter()).setSortsOnUpdates(true);
        ruleTable.getTableHeader().setReorderingAllowed(false);
        addRuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SystemCreation.this.rules.addNew();
                ruleTable.getRowSorter().rowsInserted(ruleTable.getModel().getRowCount() - 1, ruleTable.getModel().getRowCount() - 1);
                ruleTable.revalidate();
            }
        });
        removeRuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ruleTable.getSelectedRow() > -1 && ruleTable.getSelectedRow() < ruleTable.getRowCount()) {
                    SystemCreation.this.rules.remove(ruleTable.convertRowIndexToModel(ruleTable.getSelectedRow()));
                    if (SystemCreation.this.rules.size() == 0) {
                        ruleTable.getRowSorter().allRowsChanged();
                    } else {
                        ruleTable.getRowSorter().rowsDeleted(ruleTable.getModel().getRowCount() - 1, ruleTable.getModel().getRowCount() - 1);
                    }
                    ruleTable.revalidate();
                }
            }
        });
        ruleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int column = ruleTable.getSelectedColumn();
                    if (column == 1) {
                        int row = ruleTable.getSelectedRow();
                        List<Integer> data = (List<Integer>) SystemCreation.this.rules.getValueAt(row, column);
                        ChangeConditionList listChanger = new ChangeConditionList(SystemCreation.this, data, SystemCreation.this.conds);
                        listChanger.run();
                    }
                }
            }
        });
        ruleTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                Rules tableModel = (Rules) table.getModel();

                if (checker.doAllCondsExists(SystemCreation.this.rules.list.get(ruleTable.getRowSorter().convertRowIndexToModel(row)).conditions)) {
                    l.setBackground(Color.white);
                } else {
                    l.setBackground(Color.pink);
                }

                return l;
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SystemCreation.this.dispose();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Сохранить как...");
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().contains(".expsys");
                    }

                    @Override
                    public String getDescription() {
                        return ".expsys";
                    }
                });
                int userSelection = fileChooser.showSaveDialog(SystemCreation.this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    File withExt;
                    if (!file.getAbsolutePath().contains(".expsys")) {
                        withExt = new File(file.getAbsolutePath() + ".expsys");
                    } else {
                        withExt = file;
                    }
                    ((WelcomeWindow) owner).file = withExt;
                    ((WelcomeWindow) owner).loadSysLabel.setText(withExt.getName());
                    //FileWriter writer;
                    try (OutputStreamWriter writer =
                                 new OutputStreamWriter(new FileOutputStream(withExt), StandardCharsets.UTF_8)) {
                        writer.write(":- encoding(utf8).\n");
                        for (Condition cond : SystemCreation.this.conds.list) {
                            writer.write("cond(" + cond.id + ",\"" +
                                    cond.desc + "\"," +
                                    cond.ass_question + ",\"" +
                                    cond.expl + "\").\n");
                        }
                        for (Question que : SystemCreation.this.questions.list) {
                            writer.write("question(" + que.id + ",\"" + que.value + "\").\n");
                        }
                        for (Rule rule : SystemCreation.this.rules.list) {
                            writer.write("rule(" + rule.result + "," + rule.conditions + ").\n");
                        }
                    } catch (IOException ignored) {
                    }
                    SystemCreation.this.dispose();
                }
            }
        });
    }

    public void run() {
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setModal(true);

        //this.setMinimumSize(new Dimension(this.getPreferredSize().width, this.getPreferredSize().height));
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        mainPanel.add(panel1, BorderLayout.SOUTH);
        saveButton = new JButton();
        saveButton.setText("Сохранить");
        panel1.add(saveButton);
        exitButton = new JButton();
        exitButton.setText("Выйти");
        panel1.add(exitButton);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        mainPanel.add(panel2, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(scrollPane1, gbc);
        condTable = new JTable();
        condTable.setAutoCreateRowSorter(true);
        condTable.setEditingColumn(0);
        condTable.setEditingRow(0);
        condTable.setFocusTraversalPolicyProvider(false);
        condTable.setRowSelectionAllowed(true);
        condTable.putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);
        scrollPane1.setViewportView(condTable);
        final JScrollPane scrollPane2 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(scrollPane2, gbc);
        questionTable = new JTable();
        questionTable.setAutoCreateRowSorter(true);
        questionTable.setEditingColumn(0);
        questionTable.setEditingRow(0);
        scrollPane2.setViewportView(questionTable);
        final JScrollPane scrollPane3 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(scrollPane3, gbc);
        ruleTable = new JTable();
        ruleTable.setAutoCreateRowSorter(true);
        scrollPane3.setViewportView(ruleTable);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel3, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Условия");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label1, gbc);
        removeCondButton = new JButton();
        removeCondButton.setText("-");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(removeCondButton, gbc);
        addCondButton = new JButton();
        addCondButton.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(addCondButton, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel4, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Вопросы");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(label2, gbc);
        removeQuestionButton = new JButton();
        removeQuestionButton.setText("-");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(removeQuestionButton, gbc);
        addQuestionButton = new JButton();
        addQuestionButton.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(addQuestionButton, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel5, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Правила");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(label3, gbc);
        removeRuleButton = new JButton();
        removeRuleButton.setText("-");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(removeRuleButton, gbc);
        addRuleButton = new JButton();
        addRuleButton.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(addRuleButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
