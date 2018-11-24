package com.violenthoboenterprises.russianroulette.presenter;

import android.media.MediaPlayer;
import android.widget.Spinner;

public interface ThePresenter {

    void buttonClick(boolean gamePlay, boolean[] chambers, int currentChamber, int chambersNum,
                     int bulletsNum, int count, MediaPlayer gunShot, Spinner chamberSpinner,
                     Spinner bulletSpinner, String youDiedString,
                     String pullTriggerString, String playAgainString, int interstitial);
    void generate(boolean[] chambers, int chambersNum, int bulletsNum);

}
