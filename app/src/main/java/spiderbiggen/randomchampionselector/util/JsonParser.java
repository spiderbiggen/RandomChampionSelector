package spiderbiggen.randomchampionselector.util;

import android.content.res.AssetManager;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import spiderbiggen.randomchampionselector.champion.Ability;
import spiderbiggen.randomchampionselector.champion.Champion;
import spiderbiggen.randomchampionselector.champion.ChampionAttackType;
import spiderbiggen.randomchampionselector.champion.ChampionResource;
import spiderbiggen.randomchampionselector.champion.ChampionRole;

/**
 * Created by Stefan Breetveld on 12-12-2015.
 */
public class JsonParser {

    private List<Champion> champions;
    private List<Ability> abilities;

    private AssetManager assetManager;

    public JsonParser(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void readFile(String fileName) throws IOException {
        InputStream in = assetManager.open(fileName);
        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            reader.beginObject();
            while (reader.hasNext()) {
                String tag = reader.nextName();
                switch (tag) {
                    case "champions":
                         champions = readChampionArray(reader);
                        break;
                    case "abilities":
                        reader.beginArray();
                        reader.endArray();
                        break;
                }
            }
            reader.endObject();
        }
    }

    private List<Champion> readChampionArray(JsonReader reader) throws IOException {
        List<Champion> champions = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            champions.add(readChampion(reader));
        }
        reader.endArray();
        return champions;
    }

    private Champion readChampion(JsonReader reader) throws IOException {
        String name = "";
        ChampionRole role = ChampionRole.NONE;
        int health = -1;
        int resource = -1;
        ChampionResource resourceType = ChampionResource.NONE;
        ChampionAttackType attackType = ChampionAttackType.NONE;

        reader.beginObject();
        while (reader.hasNext()) {
            String tag = reader.nextName();
            switch (tag) {
                case "name":
                    name = reader.nextString();
                    break;
                case "role":
                    role = roleToEnum(reader.nextString());
                    break;
                case "health":
                    health = reader.nextInt();
                    break;
                case "resource":
                    resource = reader.nextInt();
                    break;
                case "resource_type":
                    resourceType = resourceTypeToEnum(reader.nextString());
                    break;
                case "attack_type":
                    attackType = attackTypeToEnum(reader.nextString());
                    break;
            }
        }
        reader.endObject();
        return new Champion(name, role, health, resource, resourceType, attackType);
    }

    private ChampionAttackType attackTypeToEnum(String s) {
        switch (s.toUpperCase()) {
            case "MELEE":
                return ChampionAttackType.MELEE;
            case "RANGED":
                return ChampionAttackType.RANGED;
            default:
                return ChampionAttackType.NONE;
        }
    }

    private ChampionResource resourceTypeToEnum(String s) {
        switch (s.toUpperCase()) {
            case "MANA":
                return ChampionResource.MANA;
            case "ENERGY":
                return ChampionResource.ENERGY;
            case "FURY":
                return ChampionResource.FURY;
            default:
                return ChampionResource.NONE;
        }
    }

    private ChampionRole roleToEnum(String role) {
        switch (role.toUpperCase()) {
            case "FIGHTER":
                return ChampionRole.FIGHTER;
            case "ASSASSIN":
                return ChampionRole.ASSASSIN;
            case "MAGE":
                return ChampionRole.MAGE;
            case "TANK":
                return ChampionRole.TANK;
            case "SUPPORT":
                return ChampionRole.SUPPORT;
            case "MARKSMAN":
                return ChampionRole.MARKSMAN;
            default:
                return ChampionRole.NONE;
        }
    }

    public List<Champion> getChampions() {
        return champions;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

}
