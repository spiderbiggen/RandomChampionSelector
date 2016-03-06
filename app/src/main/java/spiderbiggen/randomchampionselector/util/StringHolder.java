package spiderbiggen.randomchampionselector.util;

import android.content.Context;

import spiderbiggen.randomchampionselector.R;
import spiderbiggen.randomchampionselector.champion.ChampionAttackType;
import spiderbiggen.randomchampionselector.champion.ChampionResource;
import spiderbiggen.randomchampionselector.champion.ChampionRole;

/**
 * Created by Stefan Breetveld on 12-12-2015.
 */
public class StringHolder {

    private static StringHolder ourInstance = null;

    public final String NONE;
    public final String TANK;
    public final String FIGHTER;
    public final String MAGE;
    public final String ASSASSIN;
    public final String SUPPORT;
    public final String MARKSMAN;

    private final String MANA;
    private final String ENERGY;

    private final String MELEE;
    private final String RANGED;

    public StringHolder(Context context) {
        if (ourInstance == null) {
            ourInstance = this;
        }

        NONE = context.getString(R.string.none);

        TANK = context.getString(R.string.tank);
        FIGHTER = context.getString(R.string.fighter);
        MAGE = context.getString(R.string.mage);
        ASSASSIN = context.getString(R.string.assassin);
        SUPPORT = context.getString(R.string.support);
        MARKSMAN = context.getString(R.string.marksman);

        MANA = context.getString(R.string.mana);
        ENERGY = context.getString(R.string.energy);

        MELEE = context.getString(R.string.melee);
        RANGED = context.getString(R.string.ranged);
    }

    public static StringHolder getInstance() {
        return ourInstance;
    }

    public String roleEnumToString(ChampionRole role) {
        switch (role) {
            case ASSASSIN: //Enum Type
                return ASSASSIN; //Final String
            case FIGHTER:
                return FIGHTER;
            case MAGE:
                return MAGE;
            case MARKSMAN:
                return MARKSMAN;
            case SUPPORT:
                return SUPPORT;
            case TANK:
                return TANK;
            default:
                return NONE;
        }
    }

    public String attackTypeEnumToString(ChampionAttackType attackType) {
        switch (attackType) {
            case MELEE:
                return MELEE;
            case RANGED:
                return RANGED;
            default:
                return NONE;
        }
    }

    public String resourceTypeEnumToString(ChampionResource resourceType) {
        switch (resourceType) {
            case MANA:
                return MANA;
            case ENERGY:
                return ENERGY;
            case FURY:
            default:
                return NONE;
        }
    }


}
