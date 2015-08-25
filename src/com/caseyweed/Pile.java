package com.caseyweed;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Pile will create a grid of images on a Graphics2D object.
 * Using this we can add as many images as we have rows and columns to
 * create a mosaic of images for export, display, or magic shows. The
 * possibilities are truly limitless.
 *
 * @author Casey Weed
 * @version 1.1
 */
public class Pile {
    private static boolean maintainAspectRatio = false;
    private final static Color defaultColor = Color.BLACK;
    private int rows;
    private int cols;
    private int total;
    private int length;
    private Graphics2D g;
    private BufferedImage output;
    private ArrayList<BufferedImage> images = new ArrayList<>();

    /**
     * Create new Pile object based off the number of rows, columns, and image
     * list. The longest side of the first image in the image list will be used
     * to establish the grid cell size.
     *
     * @param rows   Number of rows
     * @param cols   Number of columns
     * @param images {@link java.util.ArrayList} of BufferedImages
     * @throws IllegalArgumentException
     */
    public Pile(int rows, int cols, BufferedImage[] images, boolean maintainAspectRatio) throws IllegalArgumentException {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Rows & columns must be 1 or greater.");
        }

        if (images.length < 1) {
            throw new IllegalArgumentException("At least one image is required.");
        }

        // get rows, cols, and total cells
        this.rows = rows;
        this.cols = cols;
        this.total = rows * cols;
        this.maintainAspectRatio = maintainAspectRatio;

        // get length based off first image in list
        BufferedImage baseImage = images[0];
        this.length = Math.max(baseImage.getWidth(), baseImage.getHeight());

        // create output buffer and init graphics
        this.output = new BufferedImage(this.length * cols, this.length * rows, BufferedImage.TYPE_INT_RGB);
        this.g = this.output.createGraphics();

        // add all images to list
        Collections.addAll(this.images, images);

        // clear and update graphics
        this.updateGraphics();
    }

    /**
     * Create Pile object based off lenght, rows, and columns. Image list is
     * initially empty. Images can be added at will using {@link Pile#addImage(BufferedImage)}
     * or set using {@link Pile#setImages(ArrayList)}.
     *
     * @param length Length of grid cells
     * @param rows   Number of rows
     * @param cols   Number of columns
     * @throws IllegalArgumentException
     * @see Pile#Pile(int, int, BufferedImage[])
     * @see Pile#addImage(BufferedImage)
     */
    public Pile(int length, int rows, int cols, boolean maintainAspectRatio) throws IllegalArgumentException {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Rows & columns must be 1 or greater.");
        }

        if (length < 1) {
            throw new IllegalArgumentException("Length must be at least 1 pixel or greater.");
        }

        // get rows, cols, and total cells
        this.length = length;
        this.rows = rows;
        this.cols = cols;
        this.total = rows * cols;
        this.maintainAspectRatio = maintainAspectRatio;

        // create output buffer and init graphics
        this.output = new BufferedImage(this.length * cols, this.length * rows, BufferedImage.TYPE_INT_RGB);
        this.g = this.output.createGraphics();

        // clear and update graphics
        this.updateGraphics();
    }

    /**
     * Create Pile object specifying the length, rows, columns, and images.
     *
     * @param length Length of grid cells in pixels
     * @param rows   Number of rows
     * @param cols   Number of columns
     * @param images {@link java.util.ArrayList} of BufferedImages
     * @throws IllegalArgumentException
     * @see Pile#Pile(int, int, BufferedImage[])
     */
    public Pile(int length, int rows, int cols, BufferedImage[] images, boolean maintainAspectRatio) throws IllegalArgumentException {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Rows & columns must be 1 or greater.");
        }

        if (length < 1) {
            throw new IllegalArgumentException("Length must be at least 1 pixel or greater.");
        }

        if (images.length < 1) {
            throw new IllegalArgumentException("At least one image is required.");
        }

        // get rows, cols, and total cells
        this.length = length;
        this.rows = rows;
        this.cols = cols;
        this.total = this.rows * this.cols;
        this.maintainAspectRatio = maintainAspectRatio;

        // create output buffer and init graphics
        this.output = new BufferedImage(this.length * cols, this.length * rows, BufferedImage.TYPE_INT_RGB);
        this.g = this.output.createGraphics();

        // add all images
        Collections.addAll(this.images, images);

        // clear and update graphics
        this.updateGraphics();
    }

    /**
     * Specifying only a list of images will use the longest side of the first
     * image as the length for all grid cells. The shape of the grid will be
     * determined using the number of images closest to the nearest perfect square.
     * If there are empty rows they will be removed.
     *
     * @param images {@link java.util.ArrayList} of BufferedImages
     */
    public Pile(BufferedImage[] images, boolean maintainAspectRatio) {
        if (images.length < 1) {
            throw new IllegalArgumentException("At least one image is required.");
        }

        this.maintainAspectRatio = maintainAspectRatio;

        // get length based off first image in list
        BufferedImage baseImage = images[0];
        this.length = Math.max(baseImage.getWidth(), baseImage.getHeight());

        // get square shape
        double s = Math.sqrt(images.length);
        double c = Math.ceil(s);
        this.rows = (int) c;
        this.cols = (int) c;

        /* create fake grid representative of the grid to be made, remove the
        bottom most row if it is completely empty */
        for (int i = 0; i < this.rows; i++) {
            int clear = 0;
            for (int j = 0; j < this.cols; j++) {
                if ((i * this.cols + j) >= images.length) {
                    clear++;
                }
            }
            if (clear == this.cols) {
                this.rows--;
            }
        }

        // get total grid cells
        this.total = this.rows * this.cols;

        // create output buffer and init graphics
        this.output = new BufferedImage(this.length * this.cols, this.length * this.rows, BufferedImage.TYPE_INT_RGB);
        this.g = this.output.createGraphics();

        // add all images
        Collections.addAll(this.images, images);

        // clear and update graphics
        this.updateGraphics();
    }

    /**
     * Will perform the same adjustments seen here: {@link Pile#Pile(BufferedImage[])}.
     * Best used after adding or removing a number of images.
     */
    public void selfAdjust() {
        // get square shape
        double s = Math.sqrt(this.images.size());
        double c = Math.ceil(s);
        this.rows = (int) c;
        this.cols = (int) c;

        /* create fake grid representative of the grid to be made, remove the
        bottom most row if it is completely empty */
        for (int i = 0; i < this.rows; i++) {
            int clear = 0;
            for (int j = 0; j < this.cols; j++) {
                if ((i * this.cols + j) >= images.size()) {
                    clear++;
                }
            }
            if (clear == this.cols) {
                this.rows--;
            }
        }

        // get total grid cells
        this.total = this.rows * this.cols;

        // create output buffer and init graphics
        this.output = new BufferedImage(this.length * this.cols, this.length * this.rows, BufferedImage.TYPE_INT_RGB);
        this.g = this.output.createGraphics();

        // update the graphics
        this.updateGraphics();
    }

    /**
     * Update graphics ({@link Pile#g}), optionally wrapping through the images.
     * For example, 0..10, restarts at index of 0 until grid is filled.
     *
     * @param wrap Whether or not to wrap
     */
    public void updateGraphics(boolean wrap, boolean maintainAspectRatio) {
        this.clearPile();
        int imageIndex = 0;
        if (this.images.size() == this.total) {
            // cannot wrap anyway, default to regular behavior
            wrap = false;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (wrap) {
                    // cycle through images, as in 0..10 starts back at 0
                    if (this.maintainAspectRatio) {
                        BufferedImage target = this.images.get(imageIndex);
                        if ((double) target.getWidth() / (double) target.getHeight() < this.length / this.length) {
                            // taller
                            int newWidth = this.length;
                            int newHeight = (int) Math.floor((double) target.getWidth() * (double) this.length / (double) target.getHeight());
                            this.g.drawImage(target, j * this.length, i * this.length, newHeight, newWidth, null);
                        } else {
                            // wider
                            int newHeight = this.length;
                            int newWidth = (int) Math.floor((double) target.getHeight() * (double) this.length / (double) target.getWidth());
                            this.g.drawImage(target, j * this.length, i * this.length, newHeight, newWidth, null);
                        }
                    } else {
                        this.g.drawImage(this.images.get(imageIndex), j * this.length, i * this.length, this.length, this.length, null);
                    }
                    imageIndex++;
                    if (imageIndex >= this.images.size()) {
                        imageIndex = 0;
                    }
                } else {
                    // do not wrap, simply cycle through the available images then stop
                    imageIndex = i * cols + j;
                    if (imageIndex < this.images.size()) {
                        if (this.maintainAspectRatio) {
                            BufferedImage target = this.images.get(imageIndex);
                            if ((double) target.getWidth() / (double) target.getHeight() < this.length / this.length) {
                                // taller
                                int newWidth = this.length;
                                int newHeight = (int) Math.floor((double) target.getWidth() * (double) this.length / (double) target.getHeight());
                                this.g.drawImage(target, j * this.length, i * this.length, newHeight, newWidth, null);
                            } else {
                                // wider
                                int newHeight = this.length;
                                int newWidth = (int) Math.floor((double) target.getHeight() * (double) this.length / (double) target.getWidth());
                                this.g.drawImage(target, j * this.length, i * this.length, newHeight, newWidth, null);
                            }
                        } else {
                            this.g.drawImage(this.images.get(imageIndex), j * this.length, i * this.length, this.length, this.length, null);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Shortcut for {@link Pile#updateGraphics(boolean)} defaulted to false.
     */
    public void updateGraphics() {
        this.updateGraphics(false, this.maintainAspectRatio);
    }

    /**
     * Clear entire summary image to default color ({@link java.awt.Color#BLACK}).
     */
    public void clearPile() {
        this.g.setColor(defaultColor);
        Dimension dim = this.getDimensions();
        g.fillRect(0, 0, dim.width, dim.height);
    }

    /**
     * Clear entire summary image to color specified ({@link java.awt.Color}).
     *
     * @param bg Color to be used as background
     */
    public void clearPile(Color bg) {
        this.g.setColor(bg);
        Dimension dim = this.getDimensions();
        g.fillRect(0, 0, dim.width, dim.height);
    }

    /**
     * Add image to the current image list, but only doing so if there is a
     * valid number of openings available based on the grid size. For example,
     * six images will not fit in a 2x2 grid, therefore the images are dropped.
     *
     * @param image Image to be added to the list
     */
    public void addImage(BufferedImage image) {
        this.images.add(image);
    }

    /**
     * Optionally update the summary image if specified.
     *
     * @param image  Image to be added to the list
     * @param update Whether or not to update the summary image without
     *               wrapping.
     * @see Pile#addImage(BufferedImage)
     */
    public void addImage(BufferedImage image, boolean update) {
        this.images.add(image);
        if (update) {
            this.updateGraphics();
        }
    }

    /**
     * Optionally update the summary image and wrap if specified.
     *
     * @param image  Image to be added to the list
     * @param update Whether or not to update the graphics
     * @param wrap   Whether or not to wrap the images
     * @see Pile#addImage(BufferedImage)
     */
    public void addImage(BufferedImage image, boolean update, boolean wrap) {
        this.images.add(image);
        if (update) {
            this.updateGraphics(wrap, this.maintainAspectRatio);
        }
    }

    /**
     * Saves entire Pile to location using String, which is then
     * used in a File object.
     *
     * @param filename String for location of Pile to be saved
     * @throws IOException
     */
    public void savePile(String filename) throws IOException {
        ImageIO.write(this.output, "PNG", new File(filename));
    }

    /**
     * Save entire Pile to location using File object.
     *
     * @param fileLocation Location for Pile to be saved
     * @throws IOException
     */
    public void savePile(File fileLocation) throws IOException {
        ImageIO.write(this.output, "PNG", fileLocation);
    }

    /**
     * Returns a new Dimension object for the total width and height pixel
     * length of the entire graphics object.
     *
     * @return Dimension
     */
    public Dimension getDimensions() {
        int width = this.rows * this.length;
        int height = this.cols * this.length;
        return new Dimension(width, height);
    }

    /**
     * Get the number of rows.
     *
     * @return Number of rows in grid
     */
    public int getRows() {
        return rows;
    }

    /**
     * Set the number of rows post initialization.
     *
     * @param rows New amount of rows
     * @throws IllegalArgumentException
     */
    public void setRows(int rows) throws IllegalArgumentException {
        if (rows < 1) {
            throw new IllegalArgumentException("Rows must be 1 or more.");
        }

        // get rows, cols, and total cells
        this.rows = rows;
        this.total = rows * cols;

        // create output buffer and init graphics
        this.output = new BufferedImage(this.length * cols, this.length * rows, BufferedImage.TYPE_INT_RGB);
        this.g = this.output.createGraphics();
    }

    /**
     * Get the number of columns.
     *
     * @return Number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Set the number of columns post initialization.
     *
     * @param cols New number of columns
     * @throws IllegalArgumentException
     */
    public void setCols(int cols) throws IllegalArgumentException {
        if (cols < 1) {
            throw new IllegalArgumentException("Columns must be 1 or more.");
        }

        // get rows, cols, and total cells
        this.cols = cols;
        this.total = rows * cols;

        // create output buffer and init graphics
        this.output = new BufferedImage(this.length * cols, this.length * rows, BufferedImage.TYPE_INT_RGB);
        this.g = this.output.createGraphics();
    }

    /**
     * Get the grid cell size in pixels.
     *
     * @return Grid cell size
     */
    public int getLength() {
        return length;
    }

    /**
     * Set the new length of each grid cell in pixels.
     *
     * @param length Pixel length of grid cells
     * @throws IllegalArgumentException
     */
    public void setLength(int length) throws IllegalArgumentException {
        if (length < 1) {
            throw new IllegalArgumentException("Length must be 1 or more.");
        }

        // get rows, cols, and total cells
        this.length = length;

        // create output buffer and init graphics
        this.output = new BufferedImage(this.length * cols, this.length * rows, BufferedImage.TYPE_INT_RGB);
        this.g = this.output.createGraphics();
    }

    /**
     * Get the {@link java.util.ArrayList} of BufferedImages.
     *
     * @return {@link java.util.ArrayList} of BufferedImages
     */
    public ArrayList<BufferedImage> getImages() {
        return images;
    }

    /**
     * Set images to a new list of BufferedImages.
     *
     * @param images Replacement ArrayList of images
     */
    public void setImages(ArrayList<BufferedImage> images) {
        this.images = images;
    }

    /**
     * Set images to BufferedImage[] array.
     *
     * @param images Replacement array of images
     */
    public void setImages(BufferedImage[] images) {
        this.images.clear();
        Collections.addAll(this.images, images);
    }

    /**
     * Remove the last image in the image list.
     */
    public void popImage() {
        if (this.images.size() > 0) {
            this.images.remove(this.images.size() - 1);
            this.updateGraphics();
        }
    }

    /**
     * Get the output BufferedImage for the pile.
     *
     * @return BufferedImage of the pile
     */
    public BufferedImage getOutput() {
        return output;
    }

    /**
     * Show PreviewPanel of the current pile.
     */
    public void showPreview() {
        PreviewPanel pp = new PreviewPanel(this.output, "Preview");
        pp.setVisible(true);
    }
}
