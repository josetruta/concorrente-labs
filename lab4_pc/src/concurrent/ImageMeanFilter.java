import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

/**
 * This class provides functionality to apply a mean filter to an image.
 * The mean filter is used to smooth images by averaging the pixel values
 * in a neighborhood defined by a kernel size.
 * 
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * ImageMeanFilter.applyMeanFilter("input.jpg", "output.jpg", 3);
 * }
 * </pre>
 * 
 * <p>Supported image formats: JPG, PNG</p>
 * 
 * <p>Author: temmanuel@comptuacao.ufcg.edu.br</p>
 */
public class ImageMeanFilter {
    
    /**
     * Applies mean filter to an image
     * 
     * @param inputPath  Path to input image
     * @param outputPath Path to output image 
     * @param kernelSize Size of mean kernel
     * @throws IOException If there is an error reading/writing
     */
    public static void applyMeanFilter(String inputPath, int numberOfThreads, String outputPath, int kernelSize) throws IOException {
        // Load image
        BufferedImage originalImage = ImageIO.read(new File(inputPath));
        
        // Create result image
        BufferedImage filteredImage = new BufferedImage(
            originalImage.getWidth(), 
            originalImage.getHeight(), 
            BufferedImage.TYPE_INT_RGB
        );
        
        // Image processing
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int initialHeight = 0;
        int partialHeight = height/numberOfThreads;
        int endingHeight = partialHeight;

        // Threads
        Thread[] threads = new Thread[numberOfThreads];
        int[] changedPixels = new int[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            ProcessImage work = new ProcessImage(originalImage, filteredImage, width, initialHeight, endingHeight, kernelSize, changedPixels, i);
            Thread t = new Thread(work, "ProcessImage-"+i);
            threads[i] = t;
            t.start();
            initialHeight += partialHeight;
            endingHeight += partialHeight;
        }

        try {
            for (int i = 0; i < numberOfThreads; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Save filtered image
        ImageIO.write(filteredImage, "jpg", new File(outputPath));

        // Count changed pixels
        int counter = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            counter += changedPixels[i];
        }
        
        System.out.println(">> Number of changed pixels: " + counter);
        System.out.println(">> Number of unchanged pixels: " + ((width * height) - counter));
    }
    
    /**
     * Main method for demonstration
     * 
     * Usage: java ImageMeanFilter <input_file>
     * 
     * Arguments:
     *   input_file - Path to the input image file to be processed
     *                Supported formats: JPG, PNG
     * 
     * Example:
     *   java ImageMeanFilter input.jpg
     * 
     * The program will generate a filtered output image named "filtered_output.jpg"
     * using a 7x7 mean filter kernel
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java ImageMeanFilter <input_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        int numberOfThreads = Integer.parseInt(args[1]);
        try {
            applyMeanFilter(inputFile, numberOfThreads, "filtered_output.jpg", 7);
        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
        }
    }
}
