import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ServerNIST {

    public static void serve() {
        final int PORT = 37; // Server port number
        byte[] receiveBuffer = new byte[1024]; // Buffer to receive client request

        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("UDP server is running...");

            // Receive the request packet from client, ignore any content
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            serverSocket.receive(receivePacket);

            // Get client address and port
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            // Calculate seconds since 1900-01-01 00:00:00 UTC
            long secondsSince1900 = calculateSecondsSince1900();

            // Convert to unsigned int and then to byte array of 4 bytes.
            byte[] responseBytes = intToBytes((int) (secondsSince1900 & 0xFFFFFFFFL));

            // Send byte array back to the client
            DatagramPacket sendPacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress,
                    clientPort);
            serverSocket.send(sendPacket);

            System.out.println("Sent " + secondsSince1900 + " seconds since 1900-01-01UTC to client.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Server has shut down.");
    }

    // Method to calculate the number of seconds since 1900-01-01 00:00:00 UTC
    private static long calculateSecondsSince1900() {
        LocalDateTime startDateTime = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        ZonedDateTime startZonedDateTime = startDateTime.atZone(ZoneOffset.UTC);
        Instant startInstant = startZonedDateTime.toInstant();
        Instant currentInstant = Instant.now();
        Duration duration = Duration.between(startInstant, currentInstant);
        return duration.getSeconds();
    }

    // Method to convert an int value to a 4-byte array
    private static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(x);
        return buffer.array();
    }

    public static void main(String[] args) {
        serve();
    }
}
