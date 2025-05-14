import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

public class ClientNIST {
    public static byte[] getDateTime(String ip, int port) throws Exception {
        // Get IP address
        InetAddress serverIP = InetAddress.getByName(ip);

        // Create socket
        DatagramSocket socket = new DatagramSocket();

        // Send packet to server
        DatagramPacket requestPacket = new DatagramPacket(new byte[1024], 1024, serverIP, port);
        socket.send(requestPacket);

        // Receive packet from server
        DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
        socket.receive(receivePacket);

        // Close socket
        socket.close();

        // Copy reply to new array
        byte[] replyBytes = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());

        // Return reply
        return replyBytes;
    }

    public static ZonedDateTime convertDateTime(byte[] content) {
        // Use this method to convert the response into a readable format
        int value = 0;
        for (byte b : content) {
            value = (value << 8) + (b & 0xFF);
        }

        System.out.println(Integer.toUnsignedString(value));

        ZonedDateTime epoch = ZonedDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime now = epoch.plusSeconds(Integer.toUnsignedLong(value))
                .withZoneSameInstant(ZoneId.of("America/New_York"));

        return now;
    }

    public static void main(String[] args) throws Exception {
        String ip = "127.0.0.1";
        int port = 37;
        System.out.println(convertDateTime(getDateTime(ip, port)));
    }
}
