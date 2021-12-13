package info.kgeorgiy.ja.vircev.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPNonblockingServer implements HelloServer {
    private SocketAddress socket;
    private ExecutorService mainTask;
    private Selector selector;
    private static final int BASE_TIMEOUT_MILLISECONDS = 100;

    @Override
    public void start(final int port, final int threads) {
        try {
            selector = Selector.open();
            socket = new InetSocketAddress(port);
            mainTask = Executors.newSingleThreadExecutor();
            mainTask.submit(() -> {
                try (final DatagramChannel channel = DatagramChannel.open()) {
                    final int buff = channel.socket().getReceiveBufferSize();
                    channel.bind(socket);
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                    while (!Thread.interrupted()) {
                        selector.select(BASE_TIMEOUT_MILLISECONDS);
                        for (final Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                            final SelectionKey key = i.next();
                            try {
                                if (key.isReadable()) {
                                    final ByteBuffer buffer = ByteBuffer.allocate(buff);
                                    final SocketAddress address = channel.receive(buffer);
                                    final String response = getResponseMessage(new String(buffer.array(), StandardCharsets.UTF_8).trim());
                                    channel.send(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)), address);
                                }
                            } finally {
                                i.remove();
                            }
                        }
                    }
                } catch (final IOException e) {
                    System.err.println("Error while working with request: " + e.getMessage());
                }
            });
        } catch (final SocketException e) {
            System.err.println("Can't start server: " + e.getMessage());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        mainTask.shutdownNow();
        try {
            selector.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private String getResponseMessage(final String message) {
        return "Hello, " + message;
    }

    public static void main(final String[] args) {
        HelloUDPServer.ServerMain(args);
    }
}
