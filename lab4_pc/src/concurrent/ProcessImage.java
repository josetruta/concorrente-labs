import java.awt.image.BufferedImage;

public class ProcessImage implements Runnable {

    private final BufferedImage originalImage;
    private final int width;
    private final int initialHeight;
    private final int endingHeight;
    private final int kernelSize;
    private BufferedImage filteredImage;
    private int[] changedPixels;
    private int index;
    
    public ProcessImage(BufferedImage originalImage, BufferedImage filteredImage, int width, int initialHeight, int endingHeight, int kernelSize, int[] changedPixels, int index) {
        this.originalImage = originalImage;
        this.width = width;
        this.initialHeight = initialHeight;
        this.endingHeight = endingHeight;
        this.kernelSize = kernelSize;
        this.filteredImage = filteredImage;
        this.changedPixels = changedPixels;
        this.index = index;
    }
    
    @Override
    public void run() {
        int counter = 0;
        for (int y = initialHeight; y < endingHeight; y++) {
            for (int x = 0; x < width; x++) {
                // Calculate neighborhood average
                int[] avgColor = calculateNeighborhoodAverage(originalImage, x, y, kernelSize);
                int filteredRgb = (avgColor[0] << 16) | (avgColor[1] << 8) | avgColor[2];
                int rgb = originalImage.getRGB(x, y);
                
                // Set filtered pixel
                filteredImage.setRGB(x, y, 
                    (avgColor[0] << 16) | 
                    (avgColor[1] << 8)  | 
                    avgColor[2]
                );

                if (filteredRgb != rgb) counter++;

            }
        }
        changedPixels[index] = counter;
    }
    
    /**
     * Calculates average colors in a pixel's neighborhood
     * 
     * @param image      Source image
     * @param centerX    X coordinate of center pixel
     * @param centerY    Y coordinate of center pixel
     * @param kernelSize Kernel size
     * @return Array with R, G, B averages
     */
    private static int[] calculateNeighborhoodAverage(BufferedImage image, int centerX, int centerY, int kernelSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        int pad = kernelSize / 2;
        
        // Arrays for color sums
        long redSum = 0, greenSum = 0, blueSum = 0;
        int pixelCount = 0;
        
        // Process neighborhood
        for (int dy = -pad; dy <= pad; dy++) {
            for (int dx = -pad; dx <= pad; dx++) {
                int x = centerX + dx;
                int y = centerY + dy;
                
                // Check image bounds
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    // Get pixel color
                    int rgb = image.getRGB(x, y);
                    
                    // Extract color components
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    
                    // Sum colors
                    redSum += red;
                    greenSum += green;
                    blueSum += blue;
                    pixelCount++;
                }
            }
        }
        
        // Calculate average
        return new int[] {
            (int)(redSum / pixelCount),
            (int)(greenSum / pixelCount),
            (int)(blueSum / pixelCount)
        };
    }
}
