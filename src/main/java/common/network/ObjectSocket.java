package common.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ObjectSocket {

    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final Socket socket;

    public ObjectSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Write an object to the socket
     * @param object The object to write
     * @throws IOException If the object cannot be written
     */
    public void write(Object object) throws IOException {
        out.reset();
        out.writeObject(object);
    }

    /**
     * Read an object from the socket
     * @return The object read
     * @param <T> The type of the object to read (deduced from the context of the method call)
     * @throws IOException If the object cannot be read
     * @throws ClassNotFoundException If the object class cannot be found
     */
    public <T> T read() throws IOException, ClassNotFoundException {
        return (T) in.readObject();
    }
    public void flush() throws IOException {
        out.flush();
    }

    public void close() {
        try {
            this.in.close();
            this.out.close();
            this.socket.close();
        } catch (IOException e) {
            // Ignore
        }
    }
}
