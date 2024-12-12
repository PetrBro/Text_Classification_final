package TextClassification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Основной класс приложения для классификации текстов по темам
 *
 * @author Котелин Пётр "petya.kotelin@mail.ru"
 * @version 1.0
 * @see FindWords
 * @see JFrame_Windows
 * @see HistogramVisualization
 */

public class Main_Class_for_TextClassification extends JFrame_Windows {

    /**
     * Поле ссылки на внутренние словари
     */
    public static String MAIN_LINK = "src/main/Files_with_words_for_topics/";

    /**
     * Поле для логирования
     */
    public static Logger logger;
    /**
     * Поле словаря для статистики по темам
     */
    public static Map<String, Map<String, Integer>> dictionary_for_statistic = new HashMap<>();

    /**
     * Поле класс, реализующий поиск слов в тексте
     */
    public static FindWords Find_Words_In_Topic;

    /**
     * Поле словаря для численной статистики
     */
    public static ArrayList<Float> Array_for_Statistic = new ArrayList<>();
    /**
     * Поле ссылки на директорию для сохранения логов
     */
    public static String sourcePath = "logs/logs.log";
    /**
     * Поле словаря, содержащего пути к тематическим словарям
     */
    public static final Map<String, String> dictionary = new HashMap<>();
    /**
     * Поле массива с названием тематических словарей на английском
     */
    public static final String[] array_of_topics = new String[]{"medical_topics", "historical_topics", " network_topics", "cryptography_topics", "finance_topics", "programming_topics"};
    /**
     * Поле массива с названием тематических словарей на русском
     */
    public static final String[] array_of_topics_russian = new String[]{"Медицина", "История", "Сети", "Криптография", "Финансы", "Программирование"};

    /**
     * Метод, заполняющий словарь ссылками на внутренние словари
     */
    public static void FillDictionary() {
        for (String topic : array_of_topics) {
            dictionary.put(topic, MAIN_LINK + topic + ".txt");
        }
    }

    /**
     * Метод, реализующий заполнение словаря для статистики по темам
     *
     * @param LINK_TO_TEXT_FILE ссылка на текст
     */
    public static void Find_Words(String LINK_TO_TEXT_FILE) throws IOException {
        FillDictionary();

        for (int i = 0; i < array_of_topics.length; i++) {
            Find_Words_In_Topic = new FindWords(dictionary.get(array_of_topics[i]), LINK_TO_TEXT_FILE);
            Map<String, Integer> array_for_find_topic_words = Find_Words_In_Topic.Find_Same_words();
            logger.info("Dictionary for topic{} successfully create!", array_of_topics[i]);
            Array_for_Statistic.add(Float.parseFloat(Find_Words_In_Topic.Return_Statistic()));
            tableModel.addRow(new String[]{array_of_topics_russian[i], Find_Words_In_Topic.Return_Statistic()});
            dictionary_for_statistic.put(array_of_topics[i], array_for_find_topic_words);
        }
    }

    /**
     * Метод, реализующий очистку файла для логирования
     */
    public static void clearFile(String fileLocation) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileLocation));
            bw.write("");
            bw.flush();
            bw.close();
        } catch (IOException ioe) {
            logger.warn(ioe.getMessage());
        }
    }

    /**
     * Метод, реализующий обработку нажатие кнопок
     */
    @Override
    public void initListeners() {
        button_1.addActionListener((ActionEvent _) -> {

            clearFile(sourcePath);

            logger = LogManager.getLogger(Main_Class_for_TextClassification.class);
            this.Link_to_file = textfield1.getText();
            this.Link_to_save_files = textfield2.getText();


            logger.info("Links have been added!");
            try {
                if (this.Link_to_file.endsWith(".txt") || this.Link_to_file.endsWith(".doc") || (this.Link_to_file.endsWith(".docx"))) {
                    new BufferedReader(new FileReader(Link_to_file));

                    Find_Words(Link_to_file);

                    try (FileWriter writer = new FileWriter(Link_to_save_files + '/' + "Statistic.txt", false)) {

                        for (String elem : array_of_topics) {
                            Map<String, Integer> Dict_for_topic_words = dictionary_for_statistic.get(elem);
                            writer.append(elem);
                            writer.append('\n');
                            writer.append('\n');
                            for (String key : Dict_for_topic_words.keySet()) {
                                writer.append(key);
                                writer.append(" ");
                                String count_words = Integer.toString(Dict_for_topic_words.get(key));
                                writer.append(count_words);
                                writer.append('\n');
                                writer.append('\n');
                            }
                        }

                        logger.info("Statistic file successfully create!");
                        button_3.setVisible(true);
                        button_2.setVisible(true);
                        comboBox.setVisible(true);

                    } catch (IOException ex) {
                        button_3.setVisible(false);
                        button_2.setVisible(false);
                        comboBox.setVisible(false);
                        JOptionPane.showMessageDialog(null, "Неправильно указан путь к директории для сохранения!", "Message", JOptionPane.ERROR_MESSAGE);
                        logger.warn(ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Некорректный формат файла!", "Message", JOptionPane.ERROR_MESSAGE);
                    logger.warn("Incorrect file format!");
                }
            } catch (FileNotFoundException e) {
                button_3.setVisible(false);
                button_2.setVisible(false);
                comboBox.setVisible(false);
                JOptionPane.showMessageDialog(null, "Файл не найден!", "Message", JOptionPane.ERROR_MESSAGE);
                logger.warn(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        button_2.addActionListener((ActionEvent _) -> {
            try {
                String Choose_topic = (String) comboBox.getSelectedItem();
                int index_for_topic = 0;
                for (int i = 0; i < array_of_topics.length; i++) {
                    if (array_of_topics[i].equals(Choose_topic)) {
                        index_for_topic = i;
                        break;
                    }
                }

                if (Array_for_Statistic.get(index_for_topic) != 0.0) {
                    JFrame frame = new JFrame("Гистограмма для темы: " + array_of_topics_russian[index_for_topic]);
                    HistogramVisualization histogram = new HistogramVisualization(dictionary_for_statistic.get(Choose_topic), frame.getHeight());
                    frame.setLayout(new BorderLayout());

                    // Создаем кнопку для сохранения графика
                    JButton saveButton = new JButton("Сохранить график в JPG");
                    saveButton.addActionListener((ActionEvent _) -> histogram.saveToJPG(Link_to_save_files + "/" + "histogram.jpg"));

                    // Добавляем компонент на фрейм
                    frame.add(histogram, BorderLayout.CENTER);
                    frame.add(saveButton, BorderLayout.SOUTH); // Кнопка снизу

                    frame.setSize(500, 400); // Увеличиваем размер окна
                    frame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "В тексте не найдены слова, относящиеся к данной тематике!", "Message", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Возникла непредвиденная ошибка!", "Message", JOptionPane.ERROR_MESSAGE);
                logger.warn(e.getMessage());
            }
        });
        button_3.addActionListener((ActionEvent _) -> {
            JFrame frame_2 = new JFrame("Таблица результатов");
            frame_2.setSize(250, 135);
            frame_2.add(table);
            frame_2.setVisible(true);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Files.copy(Paths.get(sourcePath), Paths.get(Link_to_save_files + "/logs.log"), StandardCopyOption.REPLACE_EXISTING);
                    dispose();
                } catch (IOException _) {
                    dispose();
                }
            }
        });
    }


    public static void main(String[] args) {
        Main_Class_for_TextClassification app = new Main_Class_for_TextClassification();
        app.setVisible(true);
    }
}