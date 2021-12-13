package info.kgeorgiy.ja.vircev.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPServer implements HelloServer {
    private DatagramSocket socket;
    private ExecutorService receives;
    private ExecutorService workers;
    private static final int BASE_TIMEOUT_SECONDS = 10;

    @Override
    public void start(int port, int threads) {
        try {
            socket = new DatagramSocket(port);
            receives = Executors.newSingleThreadExecutor();
            workers = Executors.newFixedThreadPool(threads);
            Runnable serverTask = () -> {
                while (!Thread.interrupted()) {
                    DatagramPacket receivePacket = new DatagramPacket(new byte[0], 0);
                    try {
                        byte[] message = new byte[socket.getReceiveBufferSize()];
                        receivePacket.setData(message);
                        receivePacket.setLength(message.length);
                        socket.receive(receivePacket);
                        Runnable sendTask = () -> {
                            byte[] byteMessage = getResponseMessage(
                                    new String(
                                            receivePacket.getData(),
                                            receivePacket.getOffset(),
                                            receivePacket.getLength(),
                                            StandardCharsets.UTF_8)
                            ).getBytes(StandardCharsets.UTF_8);
                            DatagramPacket sendPacket = new DatagramPacket(byteMessage, byteMessage.length, receivePacket.getSocketAddress());
                            try {
                                socket.send(sendPacket);
                            } catch (IOException e) {
                                System.err.println("IO exception while sending packet: " + e.getMessage());
                            }
                        };

                        workers.submit(sendTask);
                    } catch (SocketException e) {
                        System.err.println("Error accessing socket: " + e.getMessage());
                    } catch (IOException e) {
                        System.err.println("IO exception while working with datagram: " + e.getMessage());
                    }
                }
            };

            receives.submit(serverTask);
        } catch (SocketException e) {
            System.err.println("Socket can't be opened");
        }
    }

    @Override
    public void close() {
        socket.close();
        receives.shutdownNow();
        workers.shutdown();
        try {
            while (!workers.awaitTermination(BASE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                workers.shutdownNow();
            }
        } catch (InterruptedException ignored) {}
    }

    private String getResponseMessage(String message) {
        return "Hello, " + message;
    }

    public static void main(String[] args) {
        ServerMain(args);
    }

    static void ServerMain(String[] args) {
        if (args == null || args.length != 2) {
            System.out.println("Should be 2 arguments: port and number of threads");
            return;
        }
        int port = Integer.parseInt(args[0]);
        int threads = Integer.parseInt(args[1]);
        HelloServer server = new HelloUDPServer();
        server.start(port, threads);
    }
}
