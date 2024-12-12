package TextClassification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Класс для визуализации гистограммы
 *
 * @author Котелин Пётр "petya.kotelin@mail.ru"
 * @see Main_Class_for_TextClassification
 */

public class HistogramVisualization extends JPanel {

    /**
     * Поле словаря с входными данными
     */
    public final Map<String, Integer> data;

    /**
     * Поле для логирования
     */
    public Logger logger = LogManager.getLogger(HistogramVisualization.class);

    /**
     * Поле количества не нулевых значений словаря
     */
    public int nonZeroCount;

    /**
     * Поле высоты отображаемого окна
     */
    public int Interface_height;

    /**
     * Конструктор класса
     *
     * @param data             входной словарь данных
     * @param Interface_height высота отображаемого окна
     */
    public HistogramVisualization(Map<String, Integer> data, int Interface_height) {
        this.data = data;
        this.Interface_height = Interface_height;
        setPreferredSize(new Dimension(500, 300)); // Установка предпочтительного размера для компонента
    }

    /**
     * Метод для отрисовки границ, столбцов и т.д.
     *
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int maxHeight = getMaxHeight(); // Получаем максимальное значение из словаря
        nonZeroCount = countNonZeroValues();

        int barWidth = 40;
        if (nonZeroCount > 7) {
            barWidth = (getWidth() - 40) / nonZeroCount; // Расчет ширины столбца с учетом только ненулевых значений
        }
        // Рисуем сетку с подписями
        drawGrid(g, maxHeight);

        int index = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();

            // Пропускаем значения, равные 0
            if (value == 0) {
                continue;
            }
            int height;
            if (maxHeight >= 5) {
                height = (int) ((double) value / maxHeight * (getHeight() - 70)); // Максимальная высота - 40 для оси X и 30 для сетки
            } else {
                height = (int) ((double) value / maxHeight * (getHeight() - 70) / 2);
            }
            int yPosition = getHeight() - height - 40; // Позиция Y для рисования столбца

            // Устанавливаем цвет и рисуем столбец
            g.setColor(Color.BLUE);
            g.fillRect(index * barWidth + 20, yPosition, barWidth - 5, height);

            // Выводим имя ключа под столбцом
            g.setColor(Color.BLACK);
            drawRotatedString(g, key, index * barWidth + 20 + (barWidth / 4), getHeight() - 25, 25); // Положение текста под столбцом
            index++;
        }
    }

    /**
     * Метод для отрисовки сетки
     *
     * @param g         графика
     * @param maxHeight граничное значение отрисовки сетки
     */
    private void drawGrid(Graphics g, int maxHeight) {
        g.setColor(Color.LIGHT_GRAY);
        int step = (int) Math.ceil(maxHeight / 10.0); // Шаг для рисования сетки
        // Рисуем горизонтальные линии и их значения

        if (maxHeight >= 5) {
            for (int i = 0; i <= 10; i++) {
                int value = step * i;
                if (value > maxHeight) continue; // Не рисуем линии выше максимума
                int y = getHeight() - 40 - (int) ((double) value / maxHeight * (getHeight() - 70)); // Позиция Y в зависимости от значения

                g.drawLine(20, y, getWidth() - 20, y); // Рисуем линию

                // Выводим числовые значения сбоку от графика
                g.drawString(String.valueOf(value), 5, y + 5); // Положение текста
            }
        } else {
            for (int i = 0; i <= 10; i++) {
                int value = step * i;

                if (value > 10) continue;  // Не рисуем линии выше максимума

                int y = getHeight() - 40 - (int) ((double) value / maxHeight * (getHeight() - 70) / 2); // Позиция Y в зависимости от значения

                g.drawLine(20, y, getWidth() - 20, y); // Рисуем линию

                // Выводим числовые значения сбоку от графика
                g.drawString(String.valueOf(value), 5, y + 5); // Положение текста
            }
        }
        // Рисуем ось X
        g.drawLine(20, getHeight() - 40, getWidth() - 20, getHeight() - 40); // Ось X
    }

    /**
     * Метод получение наибольшего значения из словаря
     *
     * @return Наибольшее значение из словаря
     */
    private int getMaxHeight() {
        return data.values().stream().max(Integer::compare).orElse(1); // Возвращаем максимальное значение
    }

    /**
     * @param g     Графика
     * @param text  Отображаемый текст
     * @param x     Положение текста по оси x
     * @param y     Положение текста по оси x
     * @param angle Угол наклона
     */
    private void drawRotatedString(Graphics g, String text, int x, int y, int angle) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.rotate(Math.toRadians(angle), x, y); // Устанавливаем угол поворота
        g2d.drawString(text, x, y); // Рисуем текст
        g2d.rotate(-Math.toRadians(angle), x, y); // Возвращаем угол в исходное положение
    }

    /**
     * Метод подсчета ненулевых значений словаря
     *
     * @return количество ненулевых значений словаря
     */
    private int countNonZeroValues() {
        return (int) data.values().stream().filter(value -> value > 0).count(); // Считаем значения больше 0
    }

    /**
     * Метод реализующий сохранение в формате JPG
     *
     * @param filePath путь для сохранения изображения
     */
    // Метод для сохранения графика в виде JPG
    public void saveToJPG(String filePath) {
        int width = getWidth();

        int height = getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Рисуем график в BufferedImage
        paintComponent(g2d);

        // Освобождаем ресурсы
        g2d.dispose();

        try {
            ImageIO.write(image, "jpg", new File(filePath)); // Сохраняем изображение
            System.out.println("График сохранен в: " + filePath);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка в сохранении картинки! Проверьте путь к директории!", "Message", JOptionPane.ERROR_MESSAGE);
            logger.warn("An error in saving the image! Check the path to the directory!");
        }
    }
}
