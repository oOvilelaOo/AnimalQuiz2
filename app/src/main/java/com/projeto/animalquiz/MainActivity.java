package com.projeto.animalquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // keys for reading data from SharedPreferences
    // chaves para ler os dados de SharedPreferences
    public static final String QUESTIONS = "pref_numberOfQuestions" ;
    public static final String CHOICES = "pref_numberOfChoices";
    public static final String ANIMALS = "pref_animalsToInclude";
    public static final String INVERTEBRATES = "pref_invertebratesToInclude";


    private boolean phoneDevice = true; // used to force portrait mode - usado para impor o modo retrato
    private boolean preferencesChanged = true; // did preferences change? -Preferencias mudaram?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set default values in the app's SharedPreferences - configura os valores padrão para o SharedPreferences do app
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // register listener for SharedPreferences changes - Registra o receptor para alteraçoes em SharedPreferences
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        // determine screen size - Determina tamanho de tela
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        // if device is a tablet, set phoneDevice to false - Se o device é um tablet, phoneDevice = false
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            phoneDevice = false; // not a phone-sized device
        }

        // if running on phone-sized device, allow only portrait orientation
        //se estiver sendo executado me dispositivo do tamanho de um telefone, so permite orientaçao retrato
        if (phoneDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    // called after onCreate completes execution - Chamado depois do onCreate
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();
        if (preferencesChanged) {
            // now that the default preferences have been set, - agora que as preferencias padrão foram configuradas
            // initialize MainActivityFragment and start the quiz - inicializa MainActivityFragment e inicia o teste
            MainActivityFragment quizFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);
            quizFragment.updateAnimals(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.updateInvertebrates(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.updateGuessRows(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.updateQuestion(PreferenceManager.getDefaultSharedPreferences(this));

            quizFragment.resetQuiz();
            preferencesChanged = false;
        }
    }

    // show menu if app is running on a phone or a portrait-oriented tablet
    // mostra o menu se o app estiver rodando em um telefone ou um tablet na orientação retrato
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the device's current orientation - obtem a orientação atual
        int orientation = getResources().getConfiguration().orientation;

        // display the app's menu only in portrait orientation - so exibe menu no retrato
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            // inflate the menu - infla o menu
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;

        } else {
            return false;
        }
    }

    // displays the SettingsActivity when running on a phone
    // exibe SettingsActivity ao ser executado em um telefone
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }

    // listener for changes to the app's SharedPreferences
    // receptor para alterações feitas em SharedPreferences do app
    private SharedPreferences.OnSharedPreferenceChangeListener  preferencesChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
        // called when the user changes the app's preferences
        // chamado quando o usuario altera as preferencias do app
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            preferencesChanged = true; // user changed app settings - user mudou as configuraçoes do app
            MainActivityFragment quizFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);

            if (key.equals(QUESTIONS)) { // nº of choices to display changed
                quizFragment.updateQuestion(sharedPreferences);
                quizFragment.resetQuiz();
            }
            if (key.equals(CHOICES)) { // nº of choices to display changed
                quizFragment.updateGuessRows(sharedPreferences);
                quizFragment.resetQuiz();
            }
            else if ((key.equals(ANIMALS))) { // animals to include changed - as regioes a incluir mudaram
                Set<String> animals = sharedPreferences.getStringSet(ANIMALS, null);

                if (animals != null && animals.size() > 0) {
                    quizFragment.updateAnimals(sharedPreferences);
                    quizFragment.resetQuiz();
                }
                else {
                    // must select one region--set North America as default
                    // deve selecionar uma região - define North America como default
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    animals.add(getString(R.string.default_animal));
                    editor.putStringSet(ANIMALS, animals);
                    editor.apply();

                    Toast.makeText(MainActivity.this, R.string.default_animal_message, Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(MainActivity .this,R.string.restarting_quiz,Toast.LENGTH_SHORT).show();

            }else if ((key.equals(INVERTEBRATES))) { // animals to include changed - as regioes a incluir mudaram
                Set<String> animals = sharedPreferences.getStringSet(INVERTEBRATES, null);

                if (animals != null && animals.size() > 0) {
                    quizFragment.updateAnimals(sharedPreferences);
                    quizFragment.resetQuiz();
                }
                else {
                    // must select one region--set North America as default
                    // deve selecionar uma região - define North America como default
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    animals.add(getString(R.string.default_animal));
                    editor.putStringSet(ANIMALS, animals);
                    editor.apply();

                    Toast.makeText(MainActivity.this, R.string.default_animal_message, Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(MainActivity .this,R.string.restarting_quiz,Toast.LENGTH_SHORT).show();
            }
        }
    };
}