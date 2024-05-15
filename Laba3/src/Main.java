import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

class ProducerConsumer {
    static List<String> storage = new ArrayList<>();
    static Semaphore access = new Semaphore(1);
    static Semaphore full = new Semaphore(0);
    static Semaphore empty;

    static class Producer implements Runnable {
        int itemsToProduce;

        Producer(int itemsToProduce) {
            this.itemsToProduce = itemsToProduce;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < itemsToProduce; i++) {
                    empty.acquire();
                    access.acquire();
                    storage.add(String.valueOf(i));
                    System.out.println("Виробник поклав " + i );
                    access.release();
                    full.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Consumer implements Runnable {
        int itemsToConsume;

        Consumer(int itemsToConsume) {
            this.itemsToConsume = itemsToConsume;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < itemsToConsume; i++) {
                    full.acquire();
                    access.acquire();
                    String item = storage.remove(0);
                    System.out.println("Споживач взяв " + item );
                    access.release();
                    empty.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int storageSize = 20; // Максимальна місткість сховища
        int totalItems = 80; // Загальна кількість продукції
        int itemsPerProducer = totalItems / 6; // Кількість елементів для кожного виробника
        int itemsPerConsumer = totalItems / 10; // Кількість елементів для кожного споживача
        empty = new Semaphore(storageSize);

        Thread[] producers = new Thread[5];
        Thread[] consumers = new Thread[2];

        for (int i = 0; i < 3; i++) {
            producers[i] = new Thread(new Producer(itemsPerProducer));
            producers[i].start();
        }

        for (int i = 0; i < 5; i++) {
            consumers[i] = new Thread(new Consumer(itemsPerConsumer));
            consumers[i].start();
        }
    }
}
