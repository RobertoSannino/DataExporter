package concurrent;

import java.util.concurrent.Semaphore;

public class Permits {

    private Permits() throws Exception {
        throw new Exception("Do not instanstiate static class");
    }

    private static Semaphore execPermits;

    public static void setExecPermitsNumber(int permits) {
        execPermits = new Semaphore(permits);
    }

    public static void acquireExecPermits(int permits) throws InterruptedException {
        execPermits.acquire(permits);
    }

    public static void releasePermit(int n) {
        execPermits.release(n);
    }
}
