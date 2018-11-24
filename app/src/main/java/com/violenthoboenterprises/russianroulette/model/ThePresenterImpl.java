package com.violenthoboenterprises.russianroulette.model;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.violenthoboenterprises.russianroulette.presenter.ThePresenter;
import com.violenthoboenterprises.russianroulette.view.TheView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThePresenterImpl implements ThePresenter {

    private TheView theView;

    public ThePresenterImpl(TheView theView){this.theView = theView;}

    @Override
    public void buttonClick(boolean gamePlay, boolean[] chambers, int currentChamber,
                            int chambersNum, int bulletsNum, int count, MediaPlayer gunShot,
                            Spinner chamberSpinner, Spinner bulletSpinner, String youDiedString,
                            String pullTriggerString, String playAgainString, int interstitial) {

        if (gamePlay){
            //If chamber is empty it is incremented and text is updated accordingly.
            theView.loadAd();
            //If there is no bullet in the chamber the game continues to the next chamber
            if (!chambers[currentChamber]) {
                String chambersRemaining = ("" + ((chambersNum
                        - currentChamber) - 1));
                String bulletsRemaining = ("" + (bulletsNum - count));
                theView.continueGame(chambersRemaining, bulletsRemaining, ++currentChamber);
            //If chamber is loaded the player is informed of their death.
            }else{
                //If there are still bullets left in the gun the player is simply
                //informed of their death but can continue playing.
                if(bulletsNum != (count + 1)){
                    String newButtonText = youDiedString + "\n" + pullTriggerString;
                    String chambersRemaining = ("" + ((chambersNum
                            - currentChamber) - 1));
                    String bulletsRemaining = ("" + (bulletsNum - count));
                    theView.continueAfterDeath(newButtonText, chambersRemaining, bulletsRemaining,
                            ++currentChamber, ++count);
                //If all bullets have been fired the player can reset the game.
                }else{
                    String newButtonText = youDiedString + "\n" + playAgainString;
                    String chambersRemaining = ("" + ((chambersNum - currentChamber) - 1));
                    String bulletsRemaining = ("" + ((bulletsNum - count) - 1));
                    currentChamber = 0;
                    gamePlay = false;
                    for (int i = 0; i < chambers.length; i++){
                        chambers[i] = false;
                    }
                    count = 0;
                    theView.gameOver(newButtonText, chambersRemaining, bulletsRemaining,
                            currentChamber, count, gamePlay, ++interstitial, chambers);
                }
            }
        //Puts the game into 'play' mode.
        }else{
            //Interstitial ad is displayed after six game plays
            theView.showInterstitial();
            gamePlay = true;
            String chambersRemaining = ("" + chambersNum);
            String bulletsRemaining = ("" + bulletsNum);
            theView.play(gamePlay, chambersRemaining, bulletsRemaining);
        }
    }

    @Override
    public void generate(boolean[] chambers, int chambersNum, int bulletsNum) {

        //An array list is created to the length of the number of available chambers.
        List<Integer> tempArray = new ArrayList<>();

        //The list is populated with integers 1 to max number of chambers inclusive.
        for (int i = 0; i < chambersNum; i++) {
            tempArray.add(i);
        }

        //The list is shuffled.
        Collections.shuffle(tempArray);

        //The first x number of values are selected where x is the number of bullets. These
        //randomly chosen values represent the chambers being loaded with bullets.
        for (int i = 0; i < bulletsNum; i++) {
            chambers[tempArray.get(i)] = true;
        }

        //To cheat with. This prints out the chambers while showing which one(s) is loaded.
        System.out.println(" ");
        for (int i = 0; i < chambersNum; i++) {
            System.out.println("Chamber " + i + " : " + chambers[i]);
        }

    }

}
