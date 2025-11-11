import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class DnaConcurrentMain {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java DnaSerialMain DIRETORIO_ARQUIVOS PADRAO");
            System.err.println("Exemplo: java DnaSerialMain dna_inputs CGTAA");
            System.exit(1);
        }

        String dirName = args[0];
        String pattern = args[1];

        File dir = new File(dirName);
        if (!dir.isDirectory()) {
            System.err.println("Caminho não é um diretório: " + dirName);
            System.exit(2);
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.err.println("Nenhum arquivo .txt encontrado em: " + dirName);
            System.exit(3);
        }

        int lenFiles = files.length;
        Thread[] threads = new Thread[lenFiles];
        Task[] tasks = new Task[lenFiles];
        int counter = 0;

        try {
            for (int i = 0; i < lenFiles; i++) {
                Task task = new Task(files[i], pattern);
                Thread thread = new Thread(task, "file-0"+i);
                threads[i] = thread;
                tasks[i] = task;
                thread.start();
            }

            for (int i = 0; i < lenFiles; i++) {
                Thread thread = threads[i];
                Task task = tasks[i];
                thread.join();
                counter += task.getCounter();
            }

            System.out.println("Sequência " + pattern + " foi encontrada " + counter + " vezes.");

        } catch (Exception e) {

            System.out.println("Erro!");
        }
        
    }

}
