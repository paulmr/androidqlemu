package m68k.memory;

import java.nio.channels.FileChannel;
import java.io.IOException;

public class ROMAddressSpace extends ByteBufferAddressSpace
{

        public ROMAddressSpace(FileChannel fchan, int fpos, int size, int startAddr) throws IOException {
                buffer = fchan.map(FileChannel.MapMode.READ_ONLY, fpos, size);
                this.size = size;
                this.startAddr = startAddr;
        }

        public ROMAddressSpace(FileChannel fchan, int startAddr) throws IOException {
                this(fchan, 0, (int) fchan.size(), startAddr);

                if(fchan.size() > Integer.MAX_VALUE) {
                        throw new IllegalArgumentException("File too large to be rom");
                }
        }

}
