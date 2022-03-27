import org.jpl7.*;
import prolog.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.Integer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WelcomeWindow extends JFrame {

    private static final Object lock = new Object();

    private JButton startSystemButton;
    private JButton newSystemButton;
    private JButton loadSystemButton;
    private JButton editSystemButton;
    private JPanel mainPanel;
    public JLabel loadSysLabel;

    public Conditions conds = new Conditions();
    public Questions questions = new Questions();
    public Rules rules = new Rules();

    public File file;

    public WelcomeWindow() {
        newSystemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SystemCreation creation = new SystemCreation(WelcomeWindow.this, null, null, null);
                creation.run();
            }
        });
        loadSystemButton.addActionListener(new ActionListener() {
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
                int userSelection = fileChooser.showOpenDialog(WelcomeWindow.this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    WelcomeWindow.this.file = fileChooser.getSelectedFile();
                    loadSysLabel.setText(WelcomeWindow.this.file.getName());
                }
            }
        });
        editSystemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WelcomeWindow.this.loadSystemFromFile();
                SystemCreation creation = new SystemCreation(WelcomeWindow.this, WelcomeWindow.this.conds,
                        WelcomeWindow.this.questions,
                        WelcomeWindow.this.rules);
                creation.run();
            }
        });
        startSystemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSystemFromFile();

                if (!file.getAbsolutePath().equals(new Query("file(File)").oneSolution().get("File").toString().replace("\\\\", "\\").replace("'", ""))) {
                    Term goal = Term.textToTerm("file(File), unload_file(File), retractall(file(_))");
                    new Query(goal).hasSolution();
                    goal = new Compound("assertz", new Term[]{new Compound("file", new Term[]{new Atom(WelcomeWindow.this.file.getAbsolutePath().replace("\\", "\\\\"), "string")})});
                    new Query(goal).hasSolution();
                    goal = Term.textToTerm("file(File), ensure_loaded(File)");
                    new Query(goal).hasSolution();
                } else {
                    new Query("make").hasSolution();
                }

                QuizDialog quiz = new QuizDialog();

                new Query("retractall(interrupted(_)), assertz(interrupted(0))").hasSolution();
                new Query("retractall(updated(_)), assertz(updated(0))").hasSolution();

                //передача указателя на диалог
                new Query(Term.textToTerm("retractall(ref(_))")).hasSolution();
                Term goal = new Compound("assertz", new Term[]{new Compound("ref", new Term[]{JPL.newJRef(quiz)})});
                new Query(goal).hasSolution();

                //очистка базы фактов
                new Query("retractall(fact(_,_,_)), assertz(fact(drip,drip,drip))").hasSolution();

                quiz.run();
            }
        });
    }

    public void loadSystemFromFile() {
        if (WelcomeWindow.this.file != null && WelcomeWindow.this.file.exists()) {
            WelcomeWindow.this.conds.list.clear();
            WelcomeWindow.this.questions.list.clear();
            WelcomeWindow.this.rules.list.clear();
            try {
                List<String> read = Files.readAllLines(Paths.get(WelcomeWindow.this.file.getAbsolutePath()));
                for (String line : read) {
                    if (line.contains("cond(")) {
                        String[] args = new String[4];
                        String lineWithoutDot = line.substring(0, line.length() - 1);
                        for (int i = 1; i < 5; ++i) {
                            args[i - 1] = new Query("arg(" + i + "," + lineWithoutDot + ", Val)").oneSolution().get("Val").toString();
                        }
                        WelcomeWindow.this.conds.list.add(new Condition(
                                Integer.valueOf(args[0]),
                                args[1].replaceFirst("'", "").substring(0, args[1].length() - 2),
                                Integer.valueOf(args[2]),
                                args[3].replaceFirst("'", "").substring(0, args[3].length() - 2)));
                    } else if (line.contains("question(")) {
                        String[] args = new String[2];
                        String lineWithoutDot = line.substring(0, line.length() - 1);
                        for (int i = 1; i < 3; ++i) {
                            args[i - 1] = new Query("arg(" + i + "," + lineWithoutDot + ", Val)").oneSolution().get("Val").toString();
                        }
                        WelcomeWindow.this.questions.list.add(new Question(
                                Integer.valueOf(args[0]),
                                args[1].replaceFirst("\'", "").substring(0, args[1].length() - 2)));
                    } else if (line.contains("rule(")) {
                        String[] args = line.replace("rule(", "").replace(").", "")
                                .replaceFirst("\\[", "")
                                .replaceFirst("]", "").split(",");
                        List<Integer> drip = new ArrayList<>();
                        for (int i = 1; i < args.length; ++i) {
                            drip.add(Integer.valueOf(args[i].trim()));
                        }
                        WelcomeWindow.this.rules.list.add(
                                new Rule(Boolean.valueOf(args[0]), drip));
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void run() {
        this.setContentPane(mainPanel);
        this.setMinimumSize(new Dimension(this.getPreferredSize().width, this.getPreferredSize().height));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(panel1, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Добро пожаловать в экспертную систему, предназначенную для принятия решения о покупке недвижимости!");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(panel2, gbc);
        newSystemButton = new JButton();
        newSystemButton.setText("Создать новую систему");
        panel2.add(newSystemButton);
        loadSystemButton = new JButton();
        loadSystemButton.setText("Загрузить систему");
        panel2.add(loadSystemButton);
        editSystemButton = new JButton();
        editSystemButton.setText("Редактировать систему");
        panel2.add(editSystemButton);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(panel3, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Загруженная система:");
        panel3.add(label2);
        loadSysLabel = new JLabel();
        loadSysLabel.setText("null");
        panel3.add(loadSysLabel);
        startSystemButton = new JButton();
        startSystemButton.setText("Запустить ЭС");
        panel3.add(startSystemButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
