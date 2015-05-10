package spiderbiggen.randomchampionselector;

import android.content.Context;
import android.view.View;

import java.util.Random;

/**
 * Created by Stefan on 10-5-2015.
 */
public class Champions {

    public static Champion[] CHAMPIONS;

    public static Champion pickRandomChampion(){
        Random rand = new Random();
        Champion champ = CHAMPIONS[rand.nextInt(CHAMPIONS.length)];
        System.out.println(champ);
        return champ;
    }
    public static Champion pickRandomChampion(Champion champion){
        Random rand = new Random();
        Champion champ;
        do {
            champ = CHAMPIONS[rand.nextInt(CHAMPIONS.length)];
        }while(champ.equals(champion));
        return champ;
    }
}
