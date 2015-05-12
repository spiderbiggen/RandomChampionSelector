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
    protected static String BLOOD_WELL;

    public static void populateChampions() {
        CHAMPIONS = new ArrayList<>();

        CHAMPIONS.add(new Champion(ChampionNames.AATROX, FIGHTER, 1983, 870, BLOOD_WELL, 150, 345, ChampionImages.AATROX));
        CHAMPIONS.add(new Champion("Ahri", MAGE, 1874, 1184, MANA, 550, 330, ChampionImages.AHRI));
        CHAMPIONS.add(new Champion("Akali", ASSASSIN, 2033, 200, ENERGY, 125, 350, ChampionImages.AKALI));
        CHAMPIONS.add(new Champion("Alistar", TANK, 2347, 925, MANA, 125, 330, ChampionImages.ALISTAR));
        CHAMPIONS.add(new Champion("Amumu", TANK, 2041, 967, MANA, 125, 335, ChampionImages.AMUMU));
        CHAMPIONS.add(new Champion("Anivia", MAGE, 1658, 1247, MANA, 600, 325, ChampionImages.ANIVIA));
        CHAMPIONS.add(new Champion("Annie", MAGE, 1804, 1184, MANA, 625, 335, ChampionImages.ANNIE));
        CHAMPIONS.add(new Champion("Ashe", MARKSMAN, 1871, 827, MANA, 600, 325, ChampionImages.ASHE));
        CHAMPIONS.add(new Champion("Azir", MAGE, 1884, 1065, MANA, 525, 335, ChampionImages.AZIR));
        CHAMPIONS.add(new Champion("Bard", SUPPORT, 1980, 1200, MANA, 500, 330, ChampionImages.BARD));
        CHAMPIONS.add(new Champion("Blitzcrank", TANK, 2198, 947, MANA, 125, 325, ChampionImages.BLITZCRANK));
        CHAMPIONS.add(new Champion("Brand", MAGE, 1800, 1091, MANA, 550, 340, ChampionImages.BRAND));
        CHAMPIONS.add(new Champion("Braum", SUPPORT, 2055, 1076, MANA, 125, 335, ChampionImages.BRAUM));
        CHAMPIONS.add(new Champion("Caitlyn", MARKSMAN, 1884, 909, MANA, 650, 325, ChampionImages.CAITLYN));
        CHAMPIONS.add(new Champion("Cassiopeia", MAGE, 1781, 1391, MANA, 550, 335, ChampionImages.CASSIOPEIA));
        CHAMPIONS.add(new Champion("Cho'Gath", TANK, 1934, 952, MANA, 125, 345, ChampionImages.CHOGATH));
        CHAMPIONS.add(new Champion("Corki", MARKSMAN, 1907, 934, MANA, 550, 325, ChampionImages.CORKI));
        CHAMPIONS.add(new Champion("Darius", FIGHTER, 2163, 901, MANA, 125, 340, ChampionImages.DARIUS));
        CHAMPIONS.add(new Champion("Diana", FIGHTER, 2119, 977, MANA, 150, 345, ChampionImages.DIANA));
        CHAMPIONS.add(new Champion("Dr. Mundo", FIGHTER, 2096, 0, null, 125, 345, ChampionImages.DRMUNDO));
        CHAMPIONS.add(new Champion("Draven", MARKSMAN, 1952, 1025, MANA, 550, 330, ChampionImages.DRAVEN));
        CHAMPIONS.add(new Champion("Elise", MAGE, 1889, 1174, MANA, 550, 330, ChampionImages.ELISE));
        CHAMPIONS.add(new Champion("Evelynn", ASSASSIN, 2061, 1031, MANA, 125, 340, ChampionImages.EVELYNN));
        CHAMPIONS.add(new Champion("Ezreal", MARKSMAN, 1844, 1076, MANA, 550, 325, ChampionImages.EZREAL));
        CHAMPIONS.add(new Champion("Fiddlesticks", MAGE, 1884, 1353, MANA, 480, 335, ChampionImages.FIDDLESTICKS));
        CHAMPIONS.add(new Champion("Fiora", FIGHTER, 2038, 967, MANA, 125, 350, ChampionImages.FIORA));
        CHAMPIONS.add(new Champion("Fizz", ASSASSIN, 2020, 947, MANA, 175, 335, ChampionImages.FIZZ));

        categorizeChampions();
    }

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

    public static Champion pickRandomChampion(Champion champion){
        Random rand = new Random();
        Champion champ;
        do {
            champ = CHAMPIONS.get(rand.nextInt(CHAMPIONS.size()));
        }while(champ.equals(champion));
        return champ;
    }

    public static Champion pickRandomChampion(Champion champion, String championType) {
        ArrayList<Champion> List = new ArrayList<>();
        if(championType.equals(FIGHTER)){
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
