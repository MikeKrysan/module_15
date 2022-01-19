import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Утечка через потоки и thread-local переменные
public class ThreadLocaLeaklExample {
    static class Job implements Runnable {
        private static final ThreadLocal<List<Object>> LOCAL_DATA = ThreadLocal.withInitial(ArrayList::new);

        public void run() {
            List<Object> objects = LOCAL_DATA.get();
            objects.add(new byte[1024*1024]);
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for(int i = 0; i < 100; i++) {
            executor.execute(new Job());
        }
    }
}

/*
Мы создали пул из 5 потоков и дали ему на исполнение 100 задач Job. При исполнении каждый поток запишет в своё локальное хранилище объект размером около 1Mb.
После выполнения задач суммарно 5 потоков будут ссылаться на 100Mb данных, которые потенциально уже никогда никем не смогут быть использованы.
 В приведённом примере сборщик мусора сможет удалить эти эти данные только после остановки пула.

 Приведенный пример встречается в реальной жизни. К примеру, большинство многопоточных фреймворков веб-приложений обрабатывают запросы при помощи тред-пулов.

Для экономии эти потоки переиспользуются и могут жить продолжительное время. Использовать thread-local в этом случае нужно осторожно.
Общая рекомендация в данном случае — удалять данные сразу, как они перестанут быть нужными. Для этого хорошо подходит конструкция try-finally.

В нашем примере достаточно переписать Job следующим образом:

static class Job implements Runnable {
   private static final ThreadLocal<List<Object>> LOCAL_DATA =
           ThreadLocal.withInitial(ArrayList::new);

   @Override
   public void run() {
       try {
           List<Object> objects = LOCAL_DATA.get();
           objects.add(new byte[1024 * 1024]);
       } finally {
           LOCAL_DATA.remove();
       }
   }
}
 */