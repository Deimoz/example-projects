package info.kgeorgiy.ja.vircev.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HelloUDPNonblockingClient implements HelloClient {
    private static final int BASE_TIMEOUT_MILLISECONDS = 100;
    // :NOTE: ??

    @Override
    public void run(final String host, final int port, final String prefix, final int threads, final int requests) {
        final InetSocketAddress socketAddress = new InetSocketAddress(host, port);
        final Map<DatagramChannel, Integer> channels = new HashMap<>();
        try (final Selector selector = Selector.open()) {
            final List<Integer> channelReceiveRequests = new ArrayList<>(Collections.nCopies(threads, 0));
            // :NOTE: ??

            int completed = 0;
            for (int i = 0; i < threads; i++) {
                channels.put(createChannel(socketAddress, selector), i);
            }
            boolean notFinished = true;
            while (!Thread.interrupted() && notFinished) {
                selector.select(BASE_TIMEOUT_MILLISECONDS);
                // :NOTE: Лишняя проверка
                if (selector.selectedKeys().isEmpty()) {
                    for (final SelectionKey key : selector.keys()) {
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                }
                for (final Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final SelectionKey key = i.next();
                    try {
                        final DatagramChannel channel = (DatagramChannel) key.channel();
                        // :NOTE: Производительность
                        // :NOTE: Контекстные объекты
                        final int thread = channels.get(channel);
                        final int request = channelReceiveRequests.get(thread);

                        if (request >= requests) {
                            channel.close();
                            break;
                        }

                        final String message = getSendMessage(prefix, thread, request);

                        if (key.isWritable()) {
                            key.interestOps(SelectionKey.OP_READ);
                            channel.write(getBuffMessage(message));
                        }

                        if (key.isReadable()) {
                            final ByteBuffer buffer = ByteBuffer.allocate(channel.socket().getReceiveBufferSize());
                            channel.read(buffer);
                            final String receivedMessage = new String(buffer.array(), StandardCharsets.UTF_8);
                            if (receivedMessage.contains(message)) {
                                channelReceiveRequests.set(thread, channelReceiveRequests.get(thread) + 1);
                                if (channelReceiveRequests.get(thread) >= requests) {
                                    completed++;
                                }
                                key.interestOps(SelectionKey.OP_WRITE);
                            }
                        }

                        if (completed == threads) {
                            notFinished = false;
                            break;
                        }
                    } finally {
                        i.remove();
                    }
                }
            }

        } catch (final IOException e) {
            System.err.println("Error while sending messages: " + e.getMessage());
        } finally {
            try {
                for (final Map.Entry<DatagramChannel, Integer> channel : channels.entrySet()) {
                    channel.getKey().close();
                }
            } catch (final IOException e) {
                System.err.println("Couldn't close channels: " + e.getMessage());
            }
        }
    }

    private static DatagramChannel createChannel(final InetSocketAddress socket, final Selector selector) throws IOException {
        final DatagramChannel channel = DatagramChannel.open();
        channel.connect(socket);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_WRITE);
        return channel;
    }

    private String getSendMessage(final String prefix, final int thread, final int request) {
        return prefix + thread + "_" + request;
    }

    private ByteBuffer getBuffMessage(final String message) {
        return ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(final String[] args) {
        if (args == null || args.length != 5) {
            System.out.println("Should be 5 arguments: host, port, prefix, number of threads and number of requests");
            return;
        }
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        final String prefix = args[2];
        final int threads = Integer.parseInt(args[3]);
        final int requests = Integer.parseInt(args[4]);
        final HelloClient client = new HelloUDPNonblockingClient();
        client.run(host, port, prefix, threads, requests);
    }
}
