package kr.nanoit.abst;

import static java.lang.Thread.sleep;

public class TEst {
    public static void main(String[] args) throws InterruptedException {
        new ThreadProcess1("1");
        new ThreadProcess1("2");
        new ThreadProcess1("3");
        sleep(99999999);
    }
}
