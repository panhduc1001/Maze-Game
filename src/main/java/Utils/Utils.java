package Utils;

import Panels.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * A class containing miscellaneous utility methods
 */
public class Utils {
    /**
     *
     * A convenience method to add a component to given grid bag
     * layout locations. Code due to Cay Horstmann
     *
     * @param c the component to add
     * @param constraints the grid bag constraints to use
     * @param x the x grid position
     * @param y the y grid position
     * @param w the grid width of the component
     * @param h the grid height of the component
     */
    public static void addToPanel(JPanel jp, Component c, GridBagConstraints
            constraints, int x, int y, int w, int h) {
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = w;
        constraints.gridheight = h;
        jp.add(c, constraints);
    }


    /**
     * Opens up a file chooser that only allows images, returning the path to the file that the user selected or null if they exited
     * @return The path to the image file that was selected or null if the file chooser was exited
     */
    public static String pickImage() {
        return pickImage("Select an image");
    }

    /**
     * Opens up a file chooser that only allows images, returning the path to the file that the user selected or null if they exited
     * @param fileChooserTitle The title of the file chooser window that will be opened
     * @return The path to the image file that was selected or null if the file chooser was exited
     */
    public static String pickImage(String fileChooserTitle) {
        return pickImage(fileChooserTitle, false);
    }

    /**
     * Opens up a file chooser that only allows images, returning the path to the file that the user selected or null if they exited
     * @param fileChooserTitle The title of the file chooser window that will be opened
     * @param saveImage If true the user will be asked to pick a location to save an image to, otherwise they will be asked to pick an image to open
     * @return The path to the image file that was selected or null if the file chooser was exited
     */
    public static String pickImage(String fileChooserTitle, boolean saveImage) {
        FileChooser fileChooser = new FileChooser(new String[]{"png", "jpg", "jpeg"});
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle(fileChooserTitle);

        FileNameExtensionFilter allImagesFilter = new FileNameExtensionFilter("All Picture Files", "png", "jpg", "jpeg");
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG (*.png)", "png");
        FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG (*.jpg, *.jpeg)", "jpg", "jpeg");

        fileChooser.addChoosableFileFilter(allImagesFilter);
        fileChooser.addChoosableFileFilter(pngFilter);
        fileChooser.addChoosableFileFilter(jpgFilter);

        int response;
        if(saveImage)
            response = fileChooser.showSaveDialog(null);
        else
            response = fileChooser.showOpenDialog(null);

        if(response == JFileChooser.APPROVE_OPTION) {
            return  String.valueOf(fileChooser.getSelectedFile().getAbsoluteFile());
        }

        return null;
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param originalImage - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    public static BufferedImage getScaledImage(Image originalImage, int w, int h){
        BufferedImage scaledImage = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = scaledImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(originalImage, 0, 0, w, h, null);
        g2.dispose();
        return scaledImage;
    }

    /**
     * Reads a specified file path to a {@link BufferedImage} returning null if the image couldn't be read
     * @param path The full path to the image file
     * @return A {@link BufferedImage} representing the specified file or null if the file could not be read or parsed properly
     */
    public static BufferedImage getImageFromPath(String path) {
        if(path == null)
            return null;
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Compares two images pixel by pixel.
     * Code from Mr. Polywhirl at <a href="https://stackoverflow.com/a/29886786">https://stackoverflow.com/a/29886786</a>
     *
     * @param imgA the first image.
     * @param imgB the second image.
     * @return whether the images are both the same or not.
     */
    public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
        // The images must be the same size.
        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
            return false;
        }

        int width  = imgA.getWidth();
        int height = imgA.getHeight();

        // Loop over every pixel.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Compare the pixels for equality.
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Converts a {@link BufferedImage} into a {@link InputStream}
     * @param image The {@link BufferedImage} to get an {@link InputStream} from
     * @return An {@link InputStream} representing the {@link BufferedImage}
     * @exception IOException thrown if the image cannot be written to a stream
     */
    public static InputStream imageToInputStream(BufferedImage image) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    /**
     * Opens up a file chooser that allows the user to pick a folder, returning the path to the file that the user selected or null if they exited
     * @param fileChooserTitle The title of the file chooser window that will be opened
     * @param save If true the opened window will be one for saving to a folder, otherwise it will be to open one
     * @return The path to the folder that was selected or null if the file chooser was exited
     */
    public static String pickFolder(String fileChooserTitle, boolean save) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(fileChooserTitle);

        int response;
        if(save)
            response = fileChooser.showSaveDialog(null);
        else
            response = fileChooser.showOpenDialog(null);

        if(response == JFileChooser.APPROVE_OPTION) {
            return  String.valueOf(fileChooser.getSelectedFile().getAbsoluteFile());
        }

        return null;
    }

    /**
     * Creates a new {@link File} that doesn't already exist by appending (i) onto it where i is a number like filename(1).extension
     * @param file The file to base the new one off
     * @return a new {@link File} that doesn't already exist
     */
    public static File createUniqueFile(File file) {
        String folder = file.getParent();
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));

        int i = 1;
        while (file.exists()) {
            file = new File(folder, fileName + "(" + i + ")" + extension);
            i++;
        }
        return file;
    }
}
