package com.violenthoboenterprises.russianroulette.view;

public interface TheView {

    void loadAd();
    void continueGame(String chambersRemaining, String bulletsRemaining, int currentChamber);
    void continueAfterDeath(String newButtonText, String chambersRemaining, String bulletsRemaining,
                            int currentChamber, int count);
    void gameOver(String newButtonText, String chambersRemaining, String bulletsRemaining,
                  int currentChamber, int count, boolean gamePlay, int interstitial, boolean[] chambers);
    void showInterstitial();
    void play(boolean gamePlay, String chambersRemaining, String bulletsRemaining);

}
