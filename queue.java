import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<>();

        // Adding elements to the queue
        queue.add("Pensil");
        queue.add("Buku");
        queue.add("Penghapus");

        int size = queue.size();

        System.out.println("Queue: " + queue);
        System.out.println("Item pertama: " + queue.peek());
        System.out.println("Item terakhir: " + queue.toArray()[size - 1]);
        System.out.println("Jumlah item pada queue: " + size);
}
}
