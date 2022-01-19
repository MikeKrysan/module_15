public class NMTExample {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                private byte[] data;
                @Override
                public void run() {
                    while (true) {
                        data = new byte[1024 * 1024];
                    }
                }
            }, "thread-" + i).start();
        }
    }
}
