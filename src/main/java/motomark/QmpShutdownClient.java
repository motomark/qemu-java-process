package motomark;

import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class QmpShutdownClient {

    private static void send(SocketChannel ch, String json) throws Exception {
        ByteBuffer buf = ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8));
        while (buf.hasRemaining()) {
            ch.write(buf);
        }
    }

    private static String receive(SocketChannel ch) throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(8192);
        ch.read(buf);
        buf.flip();
        return StandardCharsets.UTF_8.decode(buf).toString();
    }

    public static void main(String[] args) throws Exception {

        Path socketPath = Path.of("/tmp/qmp-sock");
        UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socketPath);

        try (SocketChannel channel = SocketChannel.open(address)) {

            // 1️⃣ Read greeting
            System.out.println("GREETING:\n" + receive(channel));

            // 2️⃣ Enable capabilities
            send(channel, "{\"execute\":\"qmp_capabilities\"}\n");
            System.out.println("CAPABILITIES:\n" + receive(channel));

            // 3️⃣ Issue graceful shutdown
            send(channel, "{\"execute\":\"system_powerdown\"}\n");
            System.out.println("SHUTDOWN RESPONSE:\n" + receive(channel));
        }
    }
}