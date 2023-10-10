package edu.shabda.synergy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AppendableAudioInputStream extends InputStream {

    private final List<byte[]> dataChunks = new ArrayList<>();
    private int currentChunkIndex = 0;
    private int currentPosition = 0;

    public void appendData(byte[] data, int offset, int length) throws IOException {
        byte[] chunk = new byte[length];
        System.arraycopy(data, offset, chunk, 0, length);
        synchronized (dataChunks) {
            dataChunks.add(chunk);
        }
    }

    @Override
    public int read() throws IOException {
        while (currentChunkIndex < dataChunks.size()) {
            byte[] currentChunk = dataChunks.get(currentChunkIndex);
            if (currentPosition < currentChunk.length) {
                return currentChunk[currentPosition++] & 0xFF;
            } else {
                // Move to the next chunk
                currentPosition = 0;
                currentChunkIndex++;
            }
        }
        return -1; // End of stream
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int totalBytesRead = 0;
        while (totalBytesRead < len) {
            int bytesRead = read();
            if (bytesRead == -1) {
                return totalBytesRead > 0 ? totalBytesRead : -1; // Return -1 if no data was read
            }
            b[off + totalBytesRead] = (byte) bytesRead;
            totalBytesRead++;
        }
        return totalBytesRead;
    }

    // Add other InputStream methods as needed
}

