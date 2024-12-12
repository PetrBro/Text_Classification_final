package TextClassification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.StemmerPorterRU;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Класс, реализующий поиск слов, фраз, выражений в заданном тексте
 *
 * @author Котелин Пётр "petya.kotelin@mail.ru"
 * @see TextFileReader
 */
public class FindWords {

    /**
     * Поле пути к файлам со встроенными словарями
     */
    public String URL_topic;

    /**
     * Поле пути к файлу с текстом
     */
    public String URL_text;

    /**
     * Поле для логирования
     */
    public Logger logger = LogManager.getLogger(FindWords.class);

    /**
     * Поле словаря для статистики по словам
     */
    public Map<String, Integer> words_in_topic_file = new HashMap<>();

    /**
     * Поле файла с текстом
     */
    public StringBuilder text_file = new StringBuilder();

    /**
     * Поле количества уникальных слов в тексте из тематических словарей
     */
    public int Count_topic_words = 0;

    /**
     * Конструктор класса
     *
     * @param URL_topic Путь к тематическим словарям
     * @param URL_text  Путь к заданному тексту
     */
    public FindWords(String URL_topic, String URL_text) {
        this.URL_topic = URL_topic;
        this.URL_text = URL_text;
    }

    /**
     * Метод стемминга слов или выражений
     *
     * @param topic_word слово или выражение из тематического словаря
     * @return Преобразованное слова или выражение
     */
    private String Stemming_word_or_words(String topic_word) {
        String[] topic_words = topic_word.split(" ");
        if (topic_words.length >= 2) {
            for (int i = 0; i < topic_words.length; i++) {
                topic_words[i] = StemmerPorterRU.stem(topic_words[i].toLowerCase());
            }

            return String.join(" ", topic_words);
        } else {
            topic_word = StemmerPorterRU.stem(topic_word);
            return topic_word;
        }
    }

    /**
     * Метод загрузки словарей и файла в программу
     *
     * @throws IOException ошибка загрузки файлов
     */
    public void Find_Same_Words_Download_Files() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(URL_topic))) {
            String topic_word;

            while ((topic_word = br.readLine()) != null) {

                this.words_in_topic_file.put(topic_word.toLowerCase(), 0);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }

        TextFileReader WordFileReader = new TextFileReader(this.URL_text);
        if (WordFileReader.isDoc_or_isDocx()) {
            this.text_file = WordFileReader.readFile();
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(URL_text))) {
                String line;

                while ((line = br.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (int i = 0; i < words.length; i++) {
                        words[i] = words[i].replaceAll("[\\pP\\s]", "").toLowerCase();
                        if (words[i].length() > 3) {
                            words[i] = StemmerPorterRU.stem(words[i]);
                            this.text_file.append(words[i]);
                            this.text_file.append(" ");
                        }
                    }
                    this.text_file.append('\n');
                }
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    /**
     * Метод для нахождения количество вхождений заданного слова в заданный текст
     *
     * @param seq  исходное слово
     * @param text заданный текст
     * @return Количество найденных вхождений seq в text
     */
    public int FindByString(String seq, StringBuilder text) {
        List<Integer> indices = new ArrayList<>();
        int strIdx = 0;
        while (strIdx < text.length()) {
            int idx = text.indexOf(seq, strIdx);
            if (idx == -1)
                break;
            strIdx = idx + seq.length();
            if (((idx == 0) || (text.charAt(idx - 1) == ' ')) && ((strIdx) < text.length()) && (text.charAt(strIdx) == ' ')) {
                indices.add(idx);
            }
        }

        return indices.size();
    }

    /**
     * Основной метод, осуществляющий собирание статистики вхождения каждого слова или выражения из тематического словаря в текст
     *
     * @return Словарь статистики по словам
     * @throws IOException ошибка загрузки файлов
     */
    public Map<String, Integer> Find_Same_words() throws IOException {
        Find_Same_Words_Download_Files();

        for (String word : this.words_in_topic_file.keySet()) {
            String stemming_word = Stemming_word_or_words(word);
            int count_words = FindByString(stemming_word, this.text_file);

            int count_words_in_text = this.words_in_topic_file.get(word);
            count_words_in_text += count_words;
            if (count_words != 0) {
                this.Count_topic_words++;
            }
            this.words_in_topic_file.put(word, count_words_in_text);
        }
        return this.words_in_topic_file;
    }

    /**
     * Метод для вывода статистики отношения количества уникальных тематических слов в тексте к общему количеству слов в тексте
     *
     * @return Статистика
     */
    public String Return_Statistic() {
        return Float.toString(Count_topic_words / (float) text_file.length() * 100);
    }

}
