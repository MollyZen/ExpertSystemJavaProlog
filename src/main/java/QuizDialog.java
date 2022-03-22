import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jpl7.Compound;
import org.jpl7.Query;
import org.jpl7.Term;
import org.jpl7.Variable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea questionArea;

    private JPanel radioPanel;
    private JScrollPane radioScrollPane;
    private final ButtonGroup choiceButtonsGroup = new ButtonGroup();
    private final List<JRadioButton> choiceButtons = new ArrayList<JRadioButton>();

    private int question;
    private final List<java.lang.Integer> conditions = new ArrayList<>();

    Thread thread;

    public QuizDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
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
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        String val = "";
        Term goal;
        int id = 0;
        for (JRadioButton butt : choiceButtons) {
            if (butt.isSelected()) {
                val = butt.getText();
                break;
            }
            ++id;
        }
        if (question == -1) {
            if (val.equals("Да")) {
                goal = Term.textToTerm("assertz(fact(" + conditions.get(0).toString() + ",-1,1))");
            } else {
                goal = Term.textToTerm("assertz(fact(" + conditions.get(0).toString() + ",-1,0))");
            }
        } else {
            goal = Term.textToTerm("assertz(fact(" + conditions.get(id) + "," + question + ",1))");
        }
        new Query(goal).hasSolution();
        new Query(Term.textToTerm("retractall(updated(_)), assertz(updated(1))")).hasSolution();
        new Query(Term.textToTerm("retractall(updated(_)), assertz(updates(0))")).hasSolution();
        conditions.clear();
        clearButtons();
        radioPanel.revalidate();
    }

    private void onCancel() {
        thread.interrupt();
        new Query("retractall(updated(_)), assertz(interrupted(1)), assertz(updated(1))").hasSolution();
        dispose();
    }

    public void saveCond(int cond) {
        conditions.add(cond);
    }

    public void saveConds(int[] conds) {
        for (int cond : conds) {
            conditions.add(cond);
        }
    }

    public void setQuestion(String Question) {
        questionArea.setText(Question);
        questionArea.revalidate();
        questionArea.repaint();
    }

    public void setButtons(int qNum) {
        question = qNum;
        String[] buttons;
        if (qNum != -1) {
            buttons = getConds(qNum);
        } else {
            buttons = new String[]{"Да", "Нет"};
        }
        addButtons(buttons);
        radioPanel.revalidate();
        radioPanel.repaint();
    }

    public String[] getConds(int qNum) {
        Term goal = Term.textToTerm("cond(Id,Desc," + qNum + ",_)");
        Map<String, Term>[] terms = new Query(goal).allSolutions();
        List<String> buttons = new ArrayList<>();
        int[] drip = new int[terms.length];
        for (Map<String, Term> term : terms) {
            buttons.add(term.get("Desc").toString());
        }
        for (int i = 0; i < terms.length; ++i) {
            drip[i] = terms[i].get("Id").intValue();
        }
        String[] strings = new String[buttons.size()];
        buttons.toArray(strings);
        saveConds(drip);
        return strings;
    }

    public void addButtons(String... buttons) {
        radioPanel.setLayout(new GridLayoutManager(buttons.length, 1));
        for (int i = 0; i < buttons.length; ++i) {
            addButton(buttons[i], i);
        }
        radioPanel.setPreferredSize(new Dimension(100, (choiceButtons.size() + 1) * 30));
    }

    public void addButton(String button, int row) {
        JRadioButton drip = new JRadioButton(button);
        choiceButtons.add(drip);
        choiceButtonsGroup.add(drip);
        radioPanel.add(drip, new GridConstraints(row, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.ALIGN_CENTER, GridConstraints.ALIGN_CENTER,
                new Dimension(100, 30), new Dimension(100, 30), new Dimension(100, 30)));
    }

    public void clearButtons() {
        for (JRadioButton button : choiceButtons) {
            choiceButtonsGroup.remove(button);
            radioPanel.remove(button);
            this.remove(button);
        }
        choiceButtons.clear();
    }

    public static void debug(String val) {
        System.out.println(val);
    }

    public void run() {
        setContentPane(contentPane);
        this.thread = new Thread() {
            @Override
            public void run() {
                var res = new Query(new Compound("work", new Term[]{new Variable("Answ")})).oneSolution();
                if (!this.isInterrupted()) {
                    QuizDialog.this.dispose();
                    ResultDialog resultDialog = new ResultDialog();
                    if (res != null && res.get("Answ").toString().equals("true")) {
                        resultDialog.resField.setText("Результат: следует покупать");
                    } else {
                        resultDialog.resField.setText("Результат: покупать НЕ следует");
                    }
                    resultDialog.run();
                }
            }
        };
        this.thread.start();
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setMinimumSize(new Dimension(this.getPreferredSize().width, this.getPreferredSize().height));
        this.pack();
        this.setTitle("Экспертная система");
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
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel1.add(panel2, gbc);
        buttonOK = new JButton();
        buttonOK.setText("Сделать выбор");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(buttonOK, gbc);
        buttonCancel = new JButton();
        buttonCancel.setText("Выход");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(buttonCancel, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel3, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel4, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, BorderLayout.CENTER);
        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setMinimumSize(new Dimension(360, 80));
        questionArea.setPreferredSize(new Dimension(360, 80));
        questionArea.setRows(0);
        questionArea.setText("Загрузка...");
        scrollPane1.setViewportView(questionArea);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel5, gbc);
        radioScrollPane = new JScrollPane();
        radioScrollPane.setVerticalScrollBarPolicy(20);
        panel5.add(radioScrollPane, BorderLayout.CENTER);
        radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        radioPanel.setPreferredSize(new Dimension(360, 80));
        radioScrollPane.setViewportView(radioPanel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
