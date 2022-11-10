package kr.nanoit;

import kr.nanoit.module.inbound.TcpServer;
import kr.nanoit.module.inbound.socket.SocketManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TCP SERVER TEST")
class TcpServerTest {

    private int port;
    private TcpServer tcpServer;

    @BeforeEach
    void setUp() throws IOException {
        port = new SecureRandom().nextInt(64511) + 1024;
        tcpServer = new TcpServer(new SocketManager(), port);
        tcpServer.serve();
    }

    @AfterEach
    void tearDown() throws IOException {
        tcpServer.close();
    }

    @DisplayName("String 1개 전송시 1개 return")
    @Test
    void should_received_when_string_send() throws IOException {
        // given
        String payload = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", port));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println(socket.isBound());
        System.out.println(socket.isConnected());
        System.out.println(socket.isInputShutdown());
        System.out.println(socket.isOutputShutdown());
        System.out.println(socket.isClosed());

        // when
        bufferedWriter.write(payload + "\r\n");
        bufferedWriter.flush();
        String actual = bufferedReader.readLine();

        // then
        assertThat(payload.length()).isEqualTo(26);
        assertThat(actual.length()).isEqualTo(26);
        assertThat(actual).isEqualTo(payload);
    }

    @DisplayName("String 10개 전송시 10개 return")
    @Test
    void should_received_10_string_when_10_string_send() throws IOException {
        // given
        String payload = makeBytesBySize(2000);
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", port));
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        // when
        dataOutputStream.write(payload.getBytes(StandardCharsets.UTF_8));
        byte[] actual = new byte[payload.getBytes(StandardCharsets.UTF_8).length];

        // then
        assertThat(payload.getBytes(StandardCharsets.UTF_8).length).isEqualTo(2000);
        assertThat(actual).isEqualTo(payload);
    }


    public String makeBytesBySize(int size) {
        int i = 0;
        while (true) {
            i++;
            String result = randomString(i);
            if (result.getBytes(StandardCharsets.UTF_8).length >= size) {
                return result;
            }
        }
    }

    public String randomString(int targetLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetLength);
        for (int i = 0; i < targetLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}