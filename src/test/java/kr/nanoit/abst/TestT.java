package kr.nanoit.abst;

public class TestT {
    public static final Object resource1 = new Object();
    public static final Object resource2 = new Object();
    private static class ThreadDemo1 extends Thread {
        public void run() {
            synchronized (resource1) {
                System.out.println("스레드 1: 자원1 점유 중");

                try { Thread.sleep(10); }
                catch (InterruptedException e) {}
                System.out.println("스레드 1: 자원2 사용 대기 중");

                synchronized (resource2) {
                    System.out.println("스레드 1 : 자원1 & 2 점유 중");
                }
            }
        }
    }

    private static class ThreadDemo2 extends Thread {
        public void run() {
            synchronized (resource2) {
                System.out.println("스레드 2: 자원2 점유 중");

                try { Thread.sleep(10); }
                catch (InterruptedException e) {}
                System.out.println("스레드 2: 자원1 사용 대기 중");

                synchronized (resource1) {
                    System.out.println("스레드 2: 자원1 & 2 점유 중");
                }
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        ThreadDemo1 t1 = new ThreadDemo1();
        ThreadDemo2 t2 = new ThreadDemo2();
        t1.start();
        t2.start();

        Thread.sleep(1000);
        System.out.println(t1.getState()); // 현재 스레드 상태를 출력 BLOCKED
        System.out.println(t2.getState()); // 현재 스레드 상태를 출력 BLOCKED
    }
}