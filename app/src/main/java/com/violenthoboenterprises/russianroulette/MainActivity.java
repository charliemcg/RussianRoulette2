package com.violenthoboenterprises.russianroulette;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Handler;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.violenthoboenterprises.russianroulette.model.ThePresenterImpl;
import com.violenthoboenterprises.russianroulette.presenter.ThePresenter;
import com.violenthoboenterprises.russianroulette.view.TheView;

public class MainActivity extends AppCompatActivity implements TheView {

    int interstitial;
    boolean bannerDisplay;
    int chambersNum;
    int bulletsNum;
    boolean gamePlay;
    int currentChamber;
    int count;
    private ThePresenter thePresenter;
    AdView adView;
    AdRequest banRequest;
    Button multiButton;
    TextView chambersRemainingTextView;
    TextView bulletsRemainingTextView;
    TextView chambersRemainingValue;
    TextView bulletsRemainingValue;
    MediaPlayer emptyChamber;
    MediaPlayer gunShot;
    Vibrator vibrate;
    Spinner chamberSpinner;
    Spinner bulletSpinner;
    boolean[] chambers;
    InterstitialAd interstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        thePresenter = new ThePresenterImpl(MainActivity.this);

        //Initialising the Google mobile ads SDK
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2378583121223638~2709009506");

        //Initialising a counter to be used when determining when interstitial displays
        interstitial = 0;

        //Initialising a boolean flag to be used when determining when banner ad displays
        bannerDisplay = true;

        //Initialising interstitial ad
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        AdRequest intRequest = new AdRequest.Builder()/*.addTestDevice
                (AdRequest.DEVICE_ID_EMULATOR)*/.build();
        interstitialAd.loadAd(intRequest);

        //Initialising banner ad
        adView = findViewById(R.id.adView);
        banRequest = new AdRequest.Builder()/*.addTestDevice
                (AdRequest.DEVICE_ID_EMULATOR)*/.build();

        //Initialising vibrator.
        vibrate = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        //Spinner allows user to select number of bullets. One to however many
        //available chambers inclusive.
        bulletSpinner = findViewById(R.id.bulletSpinner);

        //Empty chamber sound clip.
        emptyChamber = MediaPlayer.create(this, R.raw.emptychamber);
        emptyChamber.setVolume(0.5f, 0.5f);

        //Gun shot sound clip.
        gunShot = MediaPlayer.create(this, R.raw.gunshot);

        //Number of chambers to be selected by user.
        chambersNum = 0;

        //Number of bullets to be selected by user.
        bulletsNum = 0;

        //Spinner allows user to select number of chambers. 1 to 12 inclusive.
        chamberSpinner = findViewById(R.id.chamberSpinner);
        Integer[] chamberValues = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        final ArrayAdapter<Integer> chamberAdapter = new ArrayAdapter<>(this,
                R.layout.custom_spinner, chamberValues);
        chamberSpinner.setAdapter(chamberAdapter);

        //Sets default spinner value to six chambers
        chamberSpinner.setSelection(5);

        chamberSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                        chambersNum = (int) parent.getItemAtPosition(pos);
                        bulletSpinnerCreate(chambersNum, bulletSpinner);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        //Initialise array capable of holding a maximum of 12 chambers.
        chambers = new boolean[12];

        //Initialising text views to inform user of remaining chambers and bullets.
        chambersRemainingTextView = findViewById(R.id.chambersRemainingTextView);

        bulletsRemainingTextView = findViewById(R.id.bulletsRemainingTextView);

        chambersRemainingValue = findViewById(R.id.chambersRemainingValue);

        bulletsRemainingValue = findViewById(R.id.bulletsRemainingValue);

        //This button is used to start or reset the game. It is also used to pull
        //the trigger during game play. Button text is updated to reflect the player's actions.
        multiButton = findViewById(R.id.multiPurposeButton);
        multiButton.setText(getResources().getString(R.string.play));

        //True when game is in play, false when game is not in play. It's a flag used to
        //determine what the multi purpose button should do.
        gamePlay = false;

        //This number represents the chamber being fired.
        currentChamber = 0;

        //This number is used to compare shots fired against number of available bullets.
        count = 0;

        //Game play.
        multiButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                thePresenter.buttonClick(gamePlay, chambers, currentChamber, chambersNum,
                        bulletsNum, count, gunShot, chamberSpinner,
                        bulletSpinner, getResources().getString(R.string.you_died),
                        getResources().getString(R.string.pull_trigger),
                        getResources().getString(R.string.play_again), interstitial);
            }
        });
    }

    //Creating a spinner for number of bullets. Maximum value is updated
    //to be equal to the number of chambers
    public void bulletSpinnerCreate(int chambersNum, Spinner bulletSpinner) {

        final Integer[] bulletValues = new Integer[chambersNum];

        for (int i = 0; i < bulletValues.length; i++) {
            bulletValues[i] = i + 1;
        }
        ArrayAdapter<Integer> bulletAdapter = new ArrayAdapter<>(this,
                R.layout.custom_spinner, bulletValues);
        bulletSpinner.setAdapter(bulletAdapter);

        bulletSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                        bulletsNum = (int) parent.getItemAtPosition(pos);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    @Override
    public void loadAd() {
        //Banner ad is displayed after one game play
        if (interstitial == 1 && bannerDisplay) {
            adView.loadAd(banRequest);
            bannerDisplay = false;
        }
    }

    @Override
    public void continueGame(String chambersRemaining, String bulletsRemaining, int currentChamberVal) {
        currentChamber = currentChamberVal;
        multiButton.setText(getResources().getString(R.string.pull_trigger));
        chambersRemainingTextView.setText(getResources().getString(R.string.chambers_remaining));
        bulletsRemainingTextView.setText(getResources().getString(R.string.bullets_remaining));
        chambersRemainingValue.setText(chambersRemaining);
        bulletsRemainingValue.setText(bulletsRemaining);
        emptyChamber.start();
        vibrate.vibrate(100);
        //Multi Purpose button is briefly paused after pulling the trigger
        //to allow for the vibration and sound effect to play undisturbed.
        multiButton.setTextColor(Color.BLACK);
        multiButton.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                multiButton.setTextColor(Color.WHITE);
                multiButton.setEnabled(true);
            }
        }, 400);
    }

    @Override
    public void continueAfterDeath(String newButtonText, String chambersRemaining,
                                   String bulletsRemaining, int currentChamberVal, int countVal) {

        currentChamber = currentChamberVal;
        count = countVal;
        multiButton.setText(newButtonText);
        chambersRemainingTextView.setText(R.string.chambers_remaining);
        bulletsRemainingTextView.setText(R.string.bullets_remaining);
        chambersRemainingValue.setText(chambersRemaining);
        bulletsRemainingValue.setText(bulletsRemaining);
        gunShot.start();
        vibrate.vibrate(300);
        multiButton.setTextColor(Color.BLACK);
        multiButton.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                multiButton.setTextColor(Color.WHITE);
                multiButton.setEnabled(true);
            }
        }, 800);
    }

    @Override
    public void gameOver(String newButtonText, String chambersRemaining, String bulletsRemaining,
                         int currentChamberVal, int countVal, boolean gamePlayVal,
                         int interstitialVal, boolean[] chamberVals) {

        multiButton.setText(newButtonText);
        chambersRemainingTextView.setText(R.string.chambers_remaining);
        bulletsRemainingTextView.setText(R.string.bullets_remaining);
        chambersRemainingValue.setText(chambersRemaining);
        bulletsRemainingValue.setText(bulletsRemaining);
        gunShot.start();
        vibrate.vibrate(300);
        currentChamber = currentChamberVal;
        gamePlay = gamePlayVal;
        chamberSpinner.setEnabled(true);
        bulletSpinner.setEnabled(true);
        multiButton.setSoundEffectsEnabled(true);
        chambers = chamberVals;
        count = countVal;
        interstitial = interstitialVal;

    }

    @Override
    public void showInterstitial() {
        if(interstitialAd.isLoaded() && (interstitial == 6)){
            interstitialAd.show();
        }
    }

    @Override
    public void play(boolean gamePlayVal, String chambersRemaining, String bulletsRemaining) {

        gamePlay = gamePlayVal;
        //Spinners are disabled during game play.
        chamberSpinner.setEnabled(false);
        bulletSpinner.setEnabled(false);
        //Default button sound is disabled and replaced with gun sounds
        multiButton.setSoundEffectsEnabled(false);
        multiButton.setText(R.string.pull_trigger);
        chambersRemainingTextView.setText(R.string.chambers_remaining);
        bulletsRemainingTextView.setText(R.string.bullets_remaining);
        chambersRemainingValue.setText(chambersRemaining);
        bulletsRemainingValue.setText(bulletsRemaining);
        thePresenter.generate(chambers, chambersNum, bulletsNum);

    }

}
