/*
В качестве примера возьмем приложение с двумя потоками.
Первый поток периодически генерирует мусорные объекты, второй — засыпает на секунду и затем распечатывает, сколько на самом деле он спал:
 */

import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;

public class VirtualVMExemple {
    public static void main(String[] args) throws Exception {
        // поток, временно выделяющий и освобождающий ~100 Mb памяти
        Thread t1 = new Thread(() -> {
            while(true) {
                List<byte[]> bytes = new LinkedList<>();
                for(int i = 0; i < 100; i++) {
                    bytes.add(new byte[1024*1024]);
                    sleeping(1);
                }
            }
        });

        // поток, распечатывающий время, прошедшее
        // за время секундной паузы
        Thread t2 = new Thread(()-> {
            while(true) {
                //Сохраним текущее время в милисекундах
                long start = System.currentTimeMillis();
                sleeping(1000);
                // распечатаем количество миллисекунд, прошедших
                // с момента предыдущего сохранения времени
                System.out.println(System.currentTimeMillis() - start);
            }
        });

        t1.start();
        t2.start();
    }

    // метод-обёртка, игнорирующий возможный эксепшн
    // никогда не делайте так в реальных продакшн-приложениях ;)
    private static void sleeping(long millis) {
        try {
            sleep(millis);
        } catch (InterruptedException ignored) {

        }
    }
}
