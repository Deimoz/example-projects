package info.kgeorgiy.ja.vircev.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPClient implements HelloClient {
    private static final int BASE_TIMEOUT_MILLISECONDS = 1000; // :NOTE: no time units
    private static final int AWAIT_MULTIPLIER = 10;
    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        InetSocketAddress socketAddress = new InetSocketAddress(host, port);
        ExecutorService workers = Executors.newFixedThreadPool(threads);
        for (int thread = 0; thread < threads; thread++) {
            int finalThread = thread;
            Runnable clientTask = () -> {
                try (DatagramSocket socket = new DatagramSocket()) {
                    DatagramPacket receivePacket = new DatagramPacket(new byte[0], 0);
                    receivePacket.setSocketAddress(socketAddress);

                    DatagramPacket sendPacket = new DatagramPacket(new byte[0], 0);
                    sendPacket.setSocketAddress(socketAddress);

                    socket.setSoTimeout(BASE_TIMEOUT_MILLISECONDS);
                    for (int request = 0; request < requests; request++) {
                        receivePacket.setData(new byte[socket.getReceiveBufferSize()]);
                        receivePacket.setLength(socket.getReceiveBufferSize());

                        String message = getSendMessage(prefix, finalThread, request);
                        byte[] byteMessage = message.getBytes(StandardCharsets.UTF_8);

                        sendPacket.setData(byteMessage);
                        sendPacket.setLength(byteMessage.length);

                        while (true) {
                            try {
                                socket.send(sendPacket);
                                socket.receive(receivePacket);
                                String receivedMessage = new String(
                                        receivePacket.getData(),
                                        receivePacket.getOffset(),
                                        receivePacket.getLength(),
                                        StandardCharsets.UTF_8
                                );
                                if (receivedMessage.contains(message)) {
                                    System.out.println(receivedMessage);
                                    break;
                                }
                            } catch(SocketTimeoutException e) {
                                System.err.println("Socket timeout: " + e.getMessage());
                            } catch (IOException e) {
                                System.err.println("IO exception while sending packet: " + e.getMessage());
                            }
                        }
                    }
                } catch (SocketException e) {
                    System.err.println("Socket can't be opened");
                }
            };

            workers.submit(clientTask);
        }

        workers.shutdown();
        try {
            workers.awaitTermination((long) threads * requests * AWAIT_MULTIPLIER, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
    }

    private String getSendMessage(String prefix, int thread, int request) {
        return prefix + thread + "_" + request;
    }

    public static void main(String[] args) {
        if (args == null || args.length != 5) {
            System.out.println("Should be 5 arguments: host, port, prefix, number of threads and number of requests");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String prefix = args[2];
        int threads = Integer.parseInt(args[3]);
        int requests = Integer.parseInt(args[4]);
        HelloClient client = new HelloUDPClient();
        client.run(host, port, prefix, threads, requests);
    }
}
