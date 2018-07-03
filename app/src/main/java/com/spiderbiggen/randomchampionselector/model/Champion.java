package com.spiderbiggen.randomchampionselector.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class that defines the champion object.
 *
 * @author Stefan Breetveld
 */
@Entity
public class Champion implements Serializable, Parseable<Champion> {

    @PrimaryKey
    private int key;
    private String id;
    private String name;
    private String title;
    private String lore;
    private String blurb;
    @ColumnInfo(name = "roles")
    private String[] tags;
    @Embedded
    private Info info;
    @Embedded
    private Image image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Ignore
    public String getCapitalizedTitle() {
        return title.substring(0, 1).toUpperCase(Locale.ENGLISH) + title.substring(1);
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Get this champion's attack.
     *
     * @return null if info or the result of {@link Info#getAttack()}
     * @see Info#getAttack()
     */
    public Byte getAttack() {
        if (info == null) {
            return null;
        }
        return info.getAttack();
    }

    public Byte getDefense() {
        if (info == null) {
            return null;
        }
        return info.defense;
    }

    public Byte getMagic() {
        if (info == null) {
            return null;
        }
        return info.getMagic();
    }

    public Byte getDifficulty() {
        if (info == null) {
            return null;
        }
        return info.getDifficulty();
    }

    public String getFull() {
        if (image == null) {
            return null;
        }
        return image.getFull();
    }

    public String getSprite() {
        if (image == null) {
            return null;
        }
        return image.getSprite();
    }

    public String getGroup() {
        if (image == null) {
            return null;
        }
        return image.getGroup();
    }

    public Integer getX() {
        if (image == null) {
            return null;
        }
        return image.getX();
    }

    public Integer getY() {
        if (image == null) {
            return null;
        }
        return image.getY();
    }

    public Integer getW() {
        if (image == null) {
            return null;
        }
        return image.getW();
    }

    public Integer getH() {
        if (image == null) {
            return null;
        }
        return image.getH();
    }

    @Override
    public String toString() {
        return "Champion{" +
            "key=" + key +
            ", name='" + name + '\'' +
            ", roles='" + Arrays.toString(tags) + '\'' +
            '}';
    }

    @Override
    public Champion parse(JSONObject object) throws JSONException {
        setKey(object.getInt("key"));
        setId(object.getString("id"));
        setBlurb(object.optString("blurb"));
        setLore(object.optString("lore"));
        setTitle(object.optString("title"));
        setName(object.optString("name"));
        JSONArray array = object.optJSONArray("tags");
        if (array != null) {
            String[] tags = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                tags[i] = array.getString(i);
            }
            setTags(tags);
        }
        JSONObject infoObject = object.getJSONObject("info");
        if (infoObject != null) {
            Info info = new Info();
            setInfo(info.parse(infoObject));
        }
        JSONObject imageObject = object.getJSONObject("image");
        if (imageObject != null) {
            Image image = new Image();
            setImage(image.parse(imageObject));
        }
        return this;
    }

    public static class Info implements Serializable, Parseable<Info> {
        private byte attack;
        private byte defense;
        private byte magic;
        private byte difficulty;

        public byte getAttack() {
            return attack;
        }

        public void setAttack(byte attack) {
            this.attack = attack;
        }

        public byte getDefense() {
            return defense;
        }

        public void setDefense(byte defense) {
            this.defense = defense;
        }

        public byte getMagic() {
            return magic;
        }

        public void setMagic(byte magic) {
            this.magic = magic;
        }

        public byte getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(byte difficulty) {
            this.difficulty = difficulty;
        }

        @Override
        public Info parse(JSONObject object) throws JSONException {
            setAttack((byte) object.optInt("attack", 0));
            setDefense((byte) object.optInt("defense", 0));
            setMagic((byte) object.optInt("magic", 0));
            setDifficulty((byte) object.optInt("difficulty", 0));
            return this;
        }
    }

    public static class Image implements Serializable, Parseable<Image> {
        private String full;
        private String sprite;
        private String group;
        private int x;
        private int y;
        private int w;
        private int h;

        public String getFull() {
            return full;
        }

        public void setFull(String full) {
            this.full = full;
        }

        public String getSprite() {
            return sprite;
        }

        public void setSprite(String sprite) {
            this.sprite = sprite;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getW() {
            return w;
        }

        public void setW(int w) {
            this.w = w;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }

        @Override
        public Image parse(JSONObject object) throws JSONException {
            setFull(object.getString("full"));
            setSprite(object.getString("sprite"));
            setGroup(object.getString("group"));
            setX(object.optInt("x", 0));
            setY(object.optInt("y", 0));
            setW(object.optInt("w", 0));
            setH(object.optInt("x", 0));
            return this;
        }
    }
}
