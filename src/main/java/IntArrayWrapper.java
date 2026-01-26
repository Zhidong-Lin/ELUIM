import java.util.Arrays;

public class IntArrayWrapper {
    private final int[] data;
    private final int hash;

    public IntArrayWrapper(int[] data) {
        this.data = Arrays.copyOf(data, data.length);
        this.hash = Arrays.hashCode(this.data);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntArrayWrapper)) return false;
        IntArrayWrapper other = (IntArrayWrapper) o;
        return Arrays.equals(this.data, other.data);
    }

    public int getHash() {   // ✅ 新增：允许外部访问 hash 值
        return hash;
    }

    public int[] getData() {
        return Arrays.copyOf(data, data.length);
    }
}

