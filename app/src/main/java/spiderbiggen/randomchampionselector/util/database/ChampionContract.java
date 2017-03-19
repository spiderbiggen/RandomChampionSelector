package spiderbiggen.randomchampionselector.util.database;

import android.provider.BaseColumns;

/**
 * Created by Stefan Breetveld on 18-3-2017.
 * Part of RandomChampionSelector.
 */

public final class ChampionContract {

    /**
     * Prevent accidental initiation.
     */
    private ChampionContract() {}

    /**
     * Define champions table.
     */
    public static class ChampionEntry implements BaseColumns {
        public static final String TABLE_NAME = "champions";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ROLE = "role";
        public static final String COLUMN_NAME_HEALTH = "health";
        public static final String COLUMN_NAME_RESOURCE = "resource";
        public static final String COLUMN_NAME_RESOURCE_TYPE = "resource_type";
        public static final String COLUMN_NAME_ATTACK_TYPE = "attack_type";
    }

    /**
     * Define ability table.
     */
    public static class AbilityEntry implements BaseColumns {
        public static final String TABLE_NAME = "abilities";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_DESCRIPTION = "desc";
        public static final String COLUMN_NAME_COOLDOWN = "cooldown";
    }
}
