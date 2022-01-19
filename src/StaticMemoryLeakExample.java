import java.util.LinkedList;
import java.util.List;

/*
Утечка через статические поля

Ранее мы говорили о том, что системный загрузчик является корнем сборщика мусора, а проинициализированные статические поля загруженных классов из него достижимы и,
следовательно, сборщик мусора не имеет права их удалить.
 */

public class StaticMemoryLeakExample {
    static class MemoryLeak {
        static List<byte[]> DATA = new LinkedList<>();

        MemoryLeak() {
            for(int i = 0; i < 100; i++) {
                DATA.add(new byte[1024*1024]);
            }
        }

        void printDataSize() {
            System.out.println(DATA.size());
        }
    }

    public static void main(String[] args) {
        createMemoryLeak();
    }

    static void createMemoryLeak() {
        MemoryLeak memoryLeak = new MemoryLeak();
        memoryLeak.printDataSize();
    }
}

/*
В данном примере несмотря на то, что объект MemoryLeak может быть удален сборщиком мусора после завершения метода createMemoryLeak() и сам объект больше никогда не будет использоваться,
данные, хранящиеся в списке DATA, не будут удалены вплоть до завершения программы.
 */