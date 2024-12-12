package TextClassification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Класс для создания графического интерфейса
 *
 * @author Котелин Пётр "petya.kotelin@mail.ru"
 * @see Main_Class_for_TextClassification
 */

public class JFrame_Windows extends JFrame {


    public static JTextField textfield1, textfield2;
    public static JLabel label_1, label_2, label_3, label_4, label_5;
    public static JButton button_1, button_2, button_3;
    public static DefaultTableModel tableModel;
    public static JTable table;
    public String Link_to_file;
    public String Link_to_save_files;
    public JComboBox<String> comboBox;
    public static final String[] array_of_topics = new String[]{"medical_topics", "historical_topics", "network_topics", "cryptography_topics", "finance_topics", "programming_topics"};

    public JFrame_Windows() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(580, 235);

        JPanel container = new JPanel(new FlowLayout());
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JPanel buttonsPanel_2 = new JPanel(new FlowLayout());

        label_1 = new JLabel("<html>Приложение для определения тематики текста</html>");
        label_2 = new JLabel("<html>Приложение способно классифицировать текст по следующим тематикам:</html>");
        label_3 = new JLabel("<html>медицинским, историческим, программирование, сети, криптография, финансы</html>");
        label_4 = new JLabel("Укажите ссылку на текст:");
        textfield1 = new JTextField("", 20);
        label_5 = new JLabel("Укажите путь для сохранения файлов:");
        textfield2 = new JTextField("", 20);
        button_1 = new JButton("Определить тематику текста");
        button_2 = new JButton("Гистограмма");
        button_3 = new JButton("Таблица");
        comboBox = new JComboBox<>(array_of_topics);
        table = new JTable();
        tableModel = new DefaultTableModel(new String[]{"Topic", "Percentage of occurrence of words"}, 0);
        table.setModel(tableModel);

        label_1.setHorizontalAlignment(JLabel.CENTER);
        label_1.setFont(new Font("Arial", Font.PLAIN, 20));
        label_2.setHorizontalAlignment(JLabel.CENTER);
        label_3.setHorizontalAlignment(JLabel.CENTER);
        button_1.setHorizontalAlignment(JLabel.CENTER);
        button_3.setVisible(false);
        button_2.setVisible(false);
        comboBox.setVisible(false);
        buttonsPanel.add(label_1);
        buttonsPanel.add(new JLabel(" "));
        buttonsPanel.add(label_2);
        buttonsPanel.add(label_3);
        buttonsPanel.add(new JLabel(" "));
        buttonsPanel.add(label_4);
        buttonsPanel.add(textfield1);
        buttonsPanel.add(label_5);
        buttonsPanel.add(textfield2);
        buttonsPanel_2.add(button_1);
        buttonsPanel_2.add(button_3);
        buttonsPanel_2.add(comboBox);
        buttonsPanel_2.add(button_2);

        container.add(buttonsPanel);
        container.add(buttonsPanel_2);
        add(container);
        initListeners();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    public void initListeners() {}


    public static void main(String[] args) {
        JFrame_Windows app = new JFrame_Windows();
        app.setVisible(true);
    }
}