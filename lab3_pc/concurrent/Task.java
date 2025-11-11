import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Task implements Runnable {
    private final File file;
    private final String pattern;
    private int counter;
    
    public Task(File file, String pattern) {
        this.file = file;
        this.pattern = pattern;
        this.counter = 0;
    }

    @Override
    public void run() {
        try {
            this.counter += this.countInFile(file, pattern);
        } catch(Exception e) {
            return;
        }
        
    }

    public int getCounter() {
        return counter;
    }

    private long countInFile(File file, String pattern) throws IOException {
        long total = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    total += countInSequence(line, pattern);
                }
            }
        }
        return total;    
    }

    private long countInSequence(String sequence, String pattern) {
        if (sequence == null || pattern == null) {
            return 0;
        }
        int n = sequence.length();
        int m = pattern.length();
        if (m == 0 || n < m) {
            return 0;
        }
        long count = 0;
        for (int i = 0; i <= n - m; i++) {
            if (sequence.regionMatches(false, i, pattern, 0, m)) {
                count++;
            }
        }
        return count;
    }
}
