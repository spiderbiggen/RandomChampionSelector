package spiderbiggen.randomchampionselector;

import java.util.ArrayList;
import java.util.Random;

/**
 * Contains all champions and some Basic String values.
 * Created by Stefan on 10-5-2015.
 */
public class Champions {

    public static ArrayList<Champion> CHAMPIONS;
    public static ArrayList<Champion> TANKS;
    public static ArrayList<Champion> FIGHTERS;
    public static ArrayList<Champion> MAGES;
    public static ArrayList<Champion> SUPPORTS;
    public static ArrayList<Champion> MARKSMANS;
    public static ArrayList<Champion> ASSASSINS;

    protected static String TANK;
    protected static String FIGHTER;
    protected static String MAGE;
    protected static String ASSASSIN;
    protected static String SUPPORT;
    protected static String MARKSMAN;
    protected static String MANA;
    protected static String ENERGY;

    /**
     * Populates the CHAMPIONS list with all the champions.
     */
    public static void populateChampions() {
        CHAMPIONS = new ArrayList<>();

        CHAMPIONS.add(new Champion("Aatrox", FIGHTER, 1983, 0, null, 150, 345));
        CHAMPIONS.add(new Champion("Ahri", MAGE, 1874, 1184, MANA, 550, 330));
        CHAMPIONS.add(new Champion("Akali", ASSASSIN, 2033, 200, ENERGY, 125, 350));
        CHAMPIONS.add(new Champion("Alistar", TANK, 2347, 925, MANA, 125, 330));
        CHAMPIONS.add(new Champion("Amumu", TANK, 2041, 967, MANA, 125, 335));
        CHAMPIONS.add(new Champion("Anivia", MAGE, 1658, 1247, MANA, 600, 325));
        CHAMPIONS.add(new Champion("Annie", MAGE, 1804, 1184, MANA, 625, 335));
        CHAMPIONS.add(new Champion("Ashe", MARKSMAN, 1871, 827, MANA, 600, 325));
        CHAMPIONS.add(new Champion("Azir", MAGE, 1884, 1065, MANA, 525, 335));
        CHAMPIONS.add(new Champion("Bard", SUPPORT, 1980, 1200, MANA, 500, 330));
        CHAMPIONS.add(new Champion("Blitzcrank", TANK, 2198, 947, MANA, 125, 325));
        CHAMPIONS.add(new Champion("Brand", MAGE, 1800, 1091, MANA, 550, 340));
        CHAMPIONS.add(new Champion("Braum", SUPPORT, 2055, 1076, MANA, 125, 335));
        CHAMPIONS.add(new Champion("Caitlyn", MARKSMAN, 1884, 909, MANA, 650, 325));
        CHAMPIONS.add(new Champion("Cassiopeia", MAGE, 1781, 1391, MANA, 550, 335));
        CHAMPIONS.add(new Champion("Cho'Gath", TANK, 1934, 952, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Corki", MARKSMAN, 1907, 934, MANA, 550, 325));
        CHAMPIONS.add(new Champion("Darius", FIGHTER, 2163, 901, MANA, 125, 340));
        CHAMPIONS.add(new Champion("Diana", FIGHTER, 2119, 977, MANA, 150, 345));
        CHAMPIONS.add(new Champion("Dr. Mundo", FIGHTER, 2096, 0, null, 125, 345));
        CHAMPIONS.add(new Champion("Ekko", ASSASSIN, 1940, 1130, MANA, 125, 340));
        CHAMPIONS.add(new Champion("Draven", MARKSMAN, 1952, 1025, MANA, 550, 330));
        CHAMPIONS.add(new Champion("Elise", MAGE, 1889, 1174, MANA, 550, 330));
        CHAMPIONS.add(new Champion("Evelynn", ASSASSIN, 2061, 1031, MANA, 125, 340));
        CHAMPIONS.add(new Champion("Ezreal", MARKSMAN, 1844, 1076, MANA, 550, 325));
        CHAMPIONS.add(new Champion("Fiddlesticks", MAGE, 1884, 1353, MANA, 480, 335));
        CHAMPIONS.add(new Champion("Fiora", FIGHTER, 2038, 967, MANA, 125, 350));
        CHAMPIONS.add(new Champion("Fizz", ASSASSIN, 2020, 947, MANA, 175, 335));
        CHAMPIONS.add(new Champion("Galio", TANK, 2023, 1169, MANA, 125, 335));
        CHAMPIONS.add(new Champion("Gangplank", FIGHTER, 2008, 962, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Garen", FIGHTER, 2248, 0, null, 125, 345));
        CHAMPIONS.add(new Champion("Gnar", FIGHTER, 1645, 0, null, 485, 325));
        CHAMPIONS.add(new Champion("Gragas", FIGHTER, 2097, 1199, MANA, 125, 330));
        CHAMPIONS.add(new Champion("Graves", MARKSMAN, 1979, 1002, MANA, 525, 330));
        CHAMPIONS.add(new Champion("Hecarim", FIGHTER, 2215, 957, MANA, 175, 345));
        CHAMPIONS.add(new Champion("Heimerdinger", MAGE, 1751, 987, MANA, 550, 340));
        CHAMPIONS.add(new Champion("Irelia", FIGHTER, 2137, 884, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Janna", SUPPORT, 1813, 1498, MANA, 475, 335));
        CHAMPIONS.add(new Champion("Jarvan IV", TANK, 2101, 982, MANA, 175, 340));
        CHAMPIONS.add(new Champion("Jax", FIGHTER, 2038, 884, MANA, 125, 350));
        CHAMPIONS.add(new Champion("Jayce", FIGHTER, 2101, 987, MANA, 125, 335));
        CHAMPIONS.add(new Champion("Jinx", MARKSMAN, 1912, 1011, MANA, 525, 325));
        CHAMPIONS.add(new Champion("Kalista", MARKSMAN, 1929, 827, MANA, 550, 325));
        CHAMPIONS.add(new Champion("Karma", MAGE, 1933, 1224, MANA, 525, 335));
        CHAMPIONS.add(new Champion("Karthus", MAGE, 1791, 1409, MANA, 450, 335));
        CHAMPIONS.add(new Champion("Kassadin", ASSASSIN, 1890, 1538, MANA, 150, 340));
        CHAMPIONS.add(new Champion("Katarina", ASSASSIN, 1921, 0, null, 125, 345));
        CHAMPIONS.add(new Champion("Kayle", FIGHTER, 2155, 1002, MANA, 125, 335));
        CHAMPIONS.add(new Champion("Kennen", MAGE, 1879, 200, ENERGY, 550, 335));
        CHAMPIONS.add(new Champion("Kha'Zix", ASSASSIN, 2018, 1007, MANA, 125, 350));
        CHAMPIONS.add(new Champion("Kog'Maw", MARKSMAN, 2025, 1002, MANA, 500, 325));
        CHAMPIONS.add(new Champion("LeBlanc", ASSASSIN, 1791, 1184, MANA, 525, 335));
        CHAMPIONS.add(new Champion("Lee Sin", FIGHTER, 2016, 200, ENERGY, 125, 350));
        CHAMPIONS.add(new Champion("Leona", TANK, 2055, 982, MANA, 125, 335));
        CHAMPIONS.add(new Champion("Lissandra", MAGE, 1934, 1154, MANA, 550, 325));
        CHAMPIONS.add(new Champion("Lucian", MARKSMAN, 1914, 996, MANA, 500, 335));
        CHAMPIONS.add(new Champion("Lulu", SUPPORT, 1947, 1227, MANA, 550, 325));
        CHAMPIONS.add(new Champion("Lux", MAGE, 1821, 1184, MANA, 550, 330));
        CHAMPIONS.add(new Champion("Malphite", TANK, 2104, 962, MANA, 125, 335));
        CHAMPIONS.add(new Champion("Malzahar", MAGE, 1874, 1091, MANA, 550, 340));
        CHAMPIONS.add(new Champion("Maokai", TANK, 2102, 1109, MANA, 125, 335));
        CHAMPIONS.add(new Champion("Master Yi", ASSASSIN, 2163, 965, MANA, 125, 355));
        CHAMPIONS.add(new Champion("Miss Fortune", MARKSMAN, 2023, 922, MANA, 550, 325));
        CHAMPIONS.add(new Champion("Mordekaiser", FIGHTER, 1915, 0, null, 125, 340));
        CHAMPIONS.add(new Champion("Morgana", MAGE, 2009, 1361, MANA, 450, 335));
        CHAMPIONS.add(new Champion("Nami", SUPPORT, 1747, 1108, MANA, 550, 335));
        CHAMPIONS.add(new Champion("Nasus", FIGHTER, 2091, 1041, MANA, 125, 350));
        CHAMPIONS.add(new Champion("Nautilus", TANK, 2038, 1134, MANA, 175, 325));
        CHAMPIONS.add(new Champion("Nidalee", ASSASSIN, 1871, 1061, MANA, 525, 335));
        CHAMPIONS.add(new Champion("Nocturne", ASSASSIN, 2028, 869, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Nunu", SUPPORT, 2128, 998, MANA, 125, 350));
        CHAMPIONS.add(new Champion("Olaf", FIGHTER, 2178, 1031, MANA, 125, 350));
        CHAMPIONS.add(new Champion("Orianna", MAGE, 1861, 1184, MANA, 525, 325));
        CHAMPIONS.add(new Champion("Pantheon", FIGHTER, 2058, 845, MANA, 150, 355));
        CHAMPIONS.add(new Champion("Poppy", FIGHTER, 1936, 745, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Quinn", MARKSMAN, 1978, 864, MANA, 525, 335));
        CHAMPIONS.add(new Champion("Rammus", TANK, 2026, 871, MANA, 125, 335));
        CHAMPIONS.add(new Champion("Rek'Sai", FIGHTER, 2141, 0, null, 175, 335));
        CHAMPIONS.add(new Champion("Renekton", FIGHTER, 2041, 0, null, 125, 345));
        CHAMPIONS.add(new Champion("Rengar", ASSASSIN, 2116, 0, null, 125, 345));
        CHAMPIONS.add(new Champion("Riven", FIGHTER, 2020, 0, null, 125, 340));
        CHAMPIONS.add(new Champion("Rumble", FIGHTER, 1944, 0, null, 125, 345));
        CHAMPIONS.add(new Champion("Ryze", FIGHTER, 2020, 1277, MANA, 550, 340));
        CHAMPIONS.add(new Champion("Sejuani", TANK, 2215, 1080, MANA, 125, 340));
        CHAMPIONS.add(new Champion("Shaco", ASSASSIN, 2010, 977, MANA, 125, 350));
        CHAMPIONS.add(new Champion("Shen", TANK, 2016, 200, ENERGY, 125, 355));
        CHAMPIONS.add(new Champion("Shyvana", FIGHTER, 2210, 0, null, 125, 350));
        CHAMPIONS.add(new Champion("Singed", TANK, 1937, 1056, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Sion", TANK, 1784, 1041, MANA, 150, 345));
        CHAMPIONS.add(new Champion("Sivir", MARKSMAN, 1910, 1134, MANA, 500, 335));
        CHAMPIONS.add(new Champion("Skarner", FIGHTER, 2233, 952, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Sona", SUPPORT, 1791, 1106, MANA, 550, 325));
        CHAMPIONS.add(new Champion("Soraka", SUPPORT, 1855, 1371, MANA, 550, 325));
        CHAMPIONS.add(new Champion("Swain", MAGE, 1842, 1174, MANA, 500, 335));
        CHAMPIONS.add(new Champion("Syndra", MAGE, 1837, 1184, MANA, 550, 330));
        CHAMPIONS.add(new Champion("Tahm Kench", SUPPORT, 2225, 1005, MANA, 200, 335));
        CHAMPIONS.add(new Champion("Talon", ASSASSIN, 2028, 1007, MANA, 125, 350));
        CHAMPIONS.add(new Champion("Taric", SUPPORT, 2149, 1301, MANA, 125, 340));
        CHAMPIONS.add(new Champion("Teemo", MARKSMAN, 1910, 947, MANA, 500, 330));
        CHAMPIONS.add(new Champion("Thresh", SUPPORT, 2074, 1022, MANA, 450, 335));
        CHAMPIONS.add(new Champion("Tristana", MARKSMAN, 1937, 791, MANA, 543, 325));
        CHAMPIONS.add(new Champion("Trundle", FIGHTER, 2248, 1047, MANA, 125, 350));
        CHAMPIONS.add(new Champion("Tryndamere", FIGHTER, 2292, 0, null, 125, 345));
        CHAMPIONS.add(new Champion("Twisted Fate", MAGE, 1916, 912, MANA, 525, 330));
        CHAMPIONS.add(new Champion("Twitch", MARKSMAN, 1902, 967, MANA, 550, 330));
        CHAMPIONS.add(new Champion("Udyr", FIGHTER, 2276, 780, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Urgot", MARKSMAN, 2100, 1247, MANA, 425, 335));
        CHAMPIONS.add(new Champion("Varus", MARKSMAN, 1932, 922, MANA, 575, 330));
        CHAMPIONS.add(new Champion("Vayne", MARKSMAN, 1909, 827, MANA, 550, 330));
        CHAMPIONS.add(new Champion("Veigar", MAGE, 1887, 1277, MANA, 525, 340));
        CHAMPIONS.add(new Champion("Vel'Koz", MAGE, 1800, 1091, MANA, 525, 340));
        CHAMPIONS.add(new Champion("Vi", FIGHTER, 2028, 1061, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Viktor", MAGE, 1842, 1174, MANA, 525, 335));
        CHAMPIONS.add(new Champion("Vladimir", MAGE, 1988, 0, null, 450, 335));
        CHAMPIONS.add(new Champion("Volibear", TANK, 2046, 780, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Warwick", FIGHTER, 2046, 780, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Wukong", FIGHTER, 2023, 912, MANA, 175, 345));
        CHAMPIONS.add(new Champion("Xerath", MAGE, 1874, 1116, MANA, 525, 340));
        CHAMPIONS.add(new Champion("Xin Zhao", FIGHTER, 2070, 869, MANA, 175, 345));
        CHAMPIONS.add(new Champion("Yasuo", FIGHTER, 1912, 0, null, 175, 345));
        CHAMPIONS.add(new Champion("Yorick", FIGHTER, 2009, 889, MANA, 125, 345));
        CHAMPIONS.add(new Champion("Zac", TANK, 2230, 0, null, 125, 335));
        CHAMPIONS.add(new Champion("Zed", ASSASSIN, 1939, 200, ENERGY, 125, 345));
        CHAMPIONS.add(new Champion("Ziggs", MAGE, 1884, 1184, MANA, 550, 325));
        CHAMPIONS.add(new Champion("Zilean", SUPPORT, 1808, 1381, MANA, 550, 335));
        CHAMPIONS.add(new Champion("Zyra", MAGE, 1737, 1184, MANA, 575, 325));

        categorizeChampions();
    }

    /**
     * Categorizes all champions eg Fighters into a list of fighters and tanks in a list of tanks.
     */
    public static void categorizeChampions() {
        FIGHTERS = new ArrayList<>();
        TANKS = new ArrayList<>();
        MAGES = new ArrayList<>();
        SUPPORTS = new ArrayList<>();
        MARKSMANS = new ArrayList<>();
        ASSASSINS = new ArrayList<>();

        for (Champion champ : CHAMPIONS) {
            if (champ.getRole().equals(FIGHTER)) {
                FIGHTERS.add(champ);
            } else if (champ.getRole().equals(TANK)) {
                TANKS.add(champ);
            } else if (champ.getRole().equals(MAGE)) {
                MAGES.add(champ);
            } else if (champ.getRole().equals(SUPPORT)) {
                SUPPORTS.add(champ);
            } else if (champ.getRole().equals(MARKSMAN)) {
                MARKSMANS.add(champ);
            } else if (champ.getRole().equals(ASSASSIN)) {
                ASSASSINS.add(champ);
            }
        }
    }

    /**
     * Picks a semi-random {@link Champion} that is not the same as the previous one and is of a specific champion type.
     *
     * @param champion     previous champion
     * @param championType The champion type (role) the user wants
     * @return a semi-random {@link Champion}
     */
    public static Champion pickRandomChampion(Champion champion, String championType) {
        ArrayList<Champion> List = new ArrayList<>();
        if (championType.equals("All")) {
            List = CHAMPIONS;
        } else if (championType.equals(FIGHTER)) {
            List = FIGHTERS;
        } else if (championType.equals(TANK)) {
            List = TANKS;
        } else if (championType.equals(MAGE)) {
            List = MAGES;
        } else if (championType.equals(SUPPORT)) {
            List = SUPPORTS;
        } else if (championType.equals(MARKSMAN)) {
            List = MARKSMANS;
        } else if (championType.equals(ASSASSIN)) {
            List = ASSASSINS;
        }

        Random rand = new Random();
        Champion champ;
        do {
            champ = List.get(rand.nextInt(List.size()));
        } while (champ.equals(champion));
        return champ;
    }
}
