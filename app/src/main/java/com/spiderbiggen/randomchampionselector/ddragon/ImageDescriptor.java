package com.spiderbiggen.randomchampionselector.ddragon;

import com.spiderbiggen.randomchampionselector.model.ImageType;

import java.io.File;

/**
 * Created on 15-3-2018.
 *
 * @author Stefan Breetveld
 */
public class ImageDescriptor {
    private String champion;
    private ImageType type;
    private File file;
    private boolean valid = true;

    ImageDescriptor(String champion, ImageType type, File file) {
        this.champion = champion;
        this.type = type;
        this.file = file;
    }

    /**
     * Gets champion
     *
     * @return value of champion
     */
    public String getChampion() {
        return champion;
    }

    /**
     * Sets champion.
     *
     * @param champion the new value of champion
     */
    public void setChampion(String champion) {
        this.champion = champion;
    }

    /**
     * Gets type
     *
     * @return value of type
     */
    public ImageType getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the new value of type
     */
    public void setType(ImageType type) {
        this.type = type;
    }

    /**
     * Gets file
     *
     * @return value of file
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets file.
     *
     * @param file the new value of file
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Gets valid
     *
     * @return value of valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets valid.
     *
     * @param valid the new value of valid
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
