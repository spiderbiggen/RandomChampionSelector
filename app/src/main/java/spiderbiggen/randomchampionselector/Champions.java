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

        categorizeChampions();
    }

    /**
     * Categorizes all champions eg Fighters into a list of fighters and tanks in a list of tanks.
     */
    public static void categorizeChampions(){
        FIGHTERS = new ArrayList<>();
        TANKS = new ArrayList<>();
        MAGES = new ArrayList<>();
        SUPPORTS = new ArrayList<>();
        MARKSMANS = new ArrayList<>();
        ASSASSINS = new ArrayList<>();
        for(Champion champ : CHAMPIONS){
            if(champ.ROLE.equals(FIGHTER)){
                FIGHTERS.add(champ);
            }else if (champ.ROLE.equals(TANK)){
                TANKS.add(champ);
            }else if (champ.ROLE.equals(MAGE)){
                MAGES.add(champ);
            }else if (champ.ROLE.equals(SUPPORT)){
                SUPPORTS.add(champ);
            }else if (champ.ROLE.equals(MARKSMAN)){
                MARKSMANS.add(champ);
            }else if (champ.ROLE.equals(ASSASSIN)){
                ASSASSINS.add(champ);
            }
        }
    }

    /**
     * Picks a semi-random {@link Champion} that is not the same as the previous one and is of a specific champion type.
     * @param champion previous champion
     * @param championType The champion type (role) the user wants
     * @return a semi-random {@link Champion}
     */
    public static Champion pickRandomChampion(Champion champion, String championType) {
        ArrayList<Champion> List = new ArrayList<>();
        if (championType.equals("All")) {
            List = CHAMPIONS;
        } else if (championType.equals(FIGHTER)) {
            List = FIGHTERS;
        }else if (championType.equals(TANK)){
            List = TANKS;
        }else if (championType.equals(MAGE)){
            List = MAGES;
        }else if (championType.equals(SUPPORT)){
            List = SUPPORTS;
        }else if (championType.equals(MARKSMAN)){
            List = MARKSMANS;
        }else if (championType.equals(ASSASSIN)){
            List = ASSASSINS;
        }

        Random rand = new Random();
        Champion champ;
        do {
            champ = List.get(rand.nextInt(List.size()));
        }while(champ.equals(champion));
        return champ;
    }
}
