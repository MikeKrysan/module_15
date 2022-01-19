/*
Утечка через внутренний класс
Давайте на практике рассмотрим один из классических примеров утечки памяти,
который довольно легко допустить:
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MemoryLeakExample {
    // Класс, реализующий циклическую коллекцию
    static class CyclicCollection {
        private final List<byte[]> list = new ArrayList<>(10);

        CyclicCollection() {
            //Займем ~ 10Mb памяти
            for (int i = 0; i < 10; i++) {
                list.add(new byte[1024 * 1024]);
            }
        }

        Element getElement(int index) {
            // Возвращаем один из десяти элементов, хранящихся
            // в списке. В качестве индекса возьмём
            // остаток от деления на 10. Таким образом внешнему
            // наблюдателю будет казаться, что в коллекции
            // бесконечное количество повторяющихся элементов.
            return new Element(list.get(index % 10));
        }


        // Внутренний класс, хранящий в себе
        // элемент коллекции
        //class Element {             //Фича! Класс внутренний, но не обернут во static, т.о он держит ссылку на внешний класс(содержит ссылку на саму коллекцию и все данные внутри нее) поэтому неочевидно для пользователя
        static class Element {
            final byte[] data;

            Element(byte[] data) {
                this.data = data;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.in.read();   //программа ждет ввода в консоль
        System.out.println("Started");
        // Список, в котором будем хранить по одному элементу
        // из ста циклических коллекций
        List<CyclicCollection.Element> listRun = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            CyclicCollection collection = new CyclicCollection();
            listRun.add(collection.getElement(i));

        }

        System.out.println("Finished!");
    }
}
