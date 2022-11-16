package kr.nanoit;


import java.io.IOException;

public class TestException {
    public static void main(String[] args) {
        try {
            int a = 123;
            out(a);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void out(int a) throws IOException {
        if (a != 2) {
            try {
                throw new IOException("gdgd");
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new IOException("두번째 ");
            }
        }
    }
}
