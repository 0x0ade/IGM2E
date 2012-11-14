package etc;

import java.io.IOException;
import java.io.InputStream;

public final class StoredInputStream extends InputStream {
	
	private byte[] data;
	private boolean closed = false;
	private int i = 0;
	
	public StoredInputStream(byte[] data) {
		this.data = data;
	}
	
	@Override
	public int read() throws IOException {
		if (closed) {
			throw new IOException("Stream closed!");
		}
		if (i >= data.length) {
			return -1;
		}
		int ii = i;
		i++;
		return data[ii];
	}
	
	@Override
	public void close() {
		closed = true;
	}

}
