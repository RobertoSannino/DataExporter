package concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Permits {

    private static Semaphore execPermits;

    public static void setExecPermitsNumber(int permits) {
        execPermits = new Semaphore(permits);
    }

    public static void acquireExecPermits(int permits) {
        try {
            execPermits.acquire(permits);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void releaseExecPermits(int permits) {
        execPermits.release(permits);
    }
}
