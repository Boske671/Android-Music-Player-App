package com.example.myapplication;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;

public class AktivnostPjesama extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    ImageView animacija;
    Animation AnimacijaRotiranja;
    MediaPlayer mediaPlayer;
    TextView ImePjesmeTextView, TrenutnoVrijemeTextView, UkupnoVrijemeTextView;
    String ImePjesme;
    ImageButton PlayStopGumb, PrijasnjaPjesmaGumb, SljedecaPjesmaGumb, LoopGumb;
    SeekBar seekBar;
    Handler handler;
    Runnable runnable;

    int UkupnoVrijeme;
    boolean loopaLiSe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pustanje_pjesama);

        ImePjesmeTextView = findViewById(R.id.naziv_pjesme);
        ImePjesme = getIntent().getStringExtra("ime_pjesme");
        ImePjesmeTextView.setText(ImePjesme);
        ImePjesmeTextView.setSelected(true);
        TrenutnoVrijemeTextView = findViewById(R.id.trenutno_vr);
        UkupnoVrijemeTextView = findViewById(R.id.ukupno_vr);

        PlayStopGumb = findViewById(R.id.Zaustavi_Pokreni);
        PrijasnjaPjesmaGumb = findViewById(R.id.Prijasnja);
        SljedecaPjesmaGumb = findViewById(R.id.Sljedeca);
        seekBar = findViewById(R.id.seek_bar);
        animacija = findViewById(R.id.Animacija);
        AnimacijaRotiranja = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);

        PlayStopGumb.setOnClickListener(this);
        PrijasnjaPjesmaGumb.setOnClickListener(this);
        SljedecaPjesmaGumb.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        LoopGumb = findViewById(R.id.LoopGumb);
        LoopGumb.setOnClickListener(this);


        handler = new Handler();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(DobivanjePutanjePjesme(ImePjesme));
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (loopaLiSe == true) {
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                        StartRotacije();
                    } else {
                        PustanjeSljedecePjesme();
                        handler.removeCallbacks(runnable);
                        StopRotacije();
                        StartRotacije();
                    }
                    // Ažuriranje elemenata korisničkog sučelja odmah nakon što media player završi reprodukciju pjesme
                    AzuriranjeTextViewova();
                    AzuriranjePozicijeSeekbara();
                }
            });

            UkupnoVrijeme = mediaPlayer.getDuration();

            seekBar.setMax(UkupnoVrijeme);
            seekBar.setClickable(false);

            // Stvaranje novog Runnable objekta koji ažurira trenutno vrijeme i položaj trake za traženje svake sekunde
            runnable = new Runnable() {
                @Override
                public void run() {
                    AzuriranjeTextViewova();
                    AzuriranjePozicijeSeekbara();
                    handler.postDelayed(this, 100);
                }
            };
            handler.postDelayed(runnable, 100);
            mediaPlayer.start();
            StartRotacije();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AzuriranjeTextViewova();
    }

    private void StartRotacije() {
        animacija.startAnimation(AnimacijaRotiranja);
    }

    private void StopRotacije() {
        animacija.clearAnimation();
    }


    private void AzuriranjePozicijeSeekbara() {
        // Ažuriranje pozicije SeekBara s trenutnom pozicijom MediaPlayera
        int TrenutnaPozicija = mediaPlayer.getCurrentPosition();
        seekBar.setProgress(TrenutnaPozicija);
    }


    private String DobivanjePutanjePjesme(String ImePjesme) {
        String putanja = null;
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.TITLE + "=?";
        String[] selectionArgs = {ImePjesme};
        Cursor CursorPjesama = contentResolver.query(songUri, null, selection, selectionArgs, MediaStore.Audio.Media.DATE_ADDED + " DESC");
        if (CursorPjesama != null && CursorPjesama.moveToFirst()) {
            int songPath = CursorPjesama.getColumnIndex(MediaStore.Audio.Media.DATA);
            putanja = CursorPjesama.getString(songPath);
        }
        CursorPjesama.close();
        return putanja;
    }

    private void PustanjeSljedecePjesme() {
        if (mediaPlayer != null) {
            List<String> songList = getIntent().getStringArrayListExtra("lista_pjesama");
            int TrenutniIndex = songList.indexOf(ImePjesme);
            if (TrenutniIndex != -1) {
                if (TrenutniIndex < songList.size() - 1) {
                    // pustanje sljedeće pjesme
                    String nextSongName = songList.get(TrenutniIndex + 1);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(DobivanjePutanjePjesme(nextSongName));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        PlayStopGumb.setImageResource(R.drawable.pauziraj);
                        StartRotacije(); // počinje animacija
                        AzuriranjeSeekBara();
                        AzuriranjeTextViewova();
                        ImePjesme = nextSongName;
                        ImePjesmeTextView.setText(ImePjesme);
                        UkupnoVrijeme = mediaPlayer.getDuration();
                        seekBar.setMax(UkupnoVrijeme);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // pustanje prve pjesme u listi
                    String firstSongName = songList.get(0);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(DobivanjePutanjePjesme(firstSongName));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        StartRotacije(); // počinje se animacija
                        AzuriranjeSeekBara();
                        AzuriranjeTextViewova();
                        ImePjesme = firstSongName;
                        ImePjesmeTextView.setText(ImePjesme);
                        UkupnoVrijeme = mediaPlayer.getDuration();
                        seekBar.setMax(UkupnoVrijeme);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void PustanjePrijasnjePjesme() {
        if (mediaPlayer != null) {
            List<String> songList = getIntent().getStringArrayListExtra("lista_pjesama");
            int TrenutniIndex = songList.indexOf(ImePjesme);
            if (TrenutniIndex > 0) {
                // puštanje Prijasnje pjesme
                String prevSongName = songList.get(TrenutniIndex - 1);
                mediaPlayer.stop();
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(DobivanjePutanjePjesme(prevSongName));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    StartRotacije();
                    AzuriranjeSeekBara();
                    AzuriranjeTextViewova();
                    ImePjesme = prevSongName;
                    ImePjesmeTextView.setText(ImePjesme);
                    UkupnoVrijeme = mediaPlayer.getDuration();
                    seekBar.setMax(UkupnoVrijeme);
                    PlayStopGumb.setImageResource(R.drawable.pauziraj);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // reproduciranje posljednje pjesme na popisu
                String lastSongName = songList.get(songList.size() - 1);
                mediaPlayer.stop();
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(DobivanjePutanjePjesme(lastSongName));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    StartRotacije();
                    AzuriranjeSeekBara();
                    AzuriranjeTextViewova();
                    ImePjesme = lastSongName;
                    ImePjesmeTextView.setText(ImePjesme);
                    UkupnoVrijeme = mediaPlayer.getDuration();
                    seekBar.setMax(UkupnoVrijeme);
                    PlayStopGumb.setImageResource(R.drawable.pauziraj);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void AzuriranjeSeekBara() {
        if (mediaPlayer != null) {
            int TrenutnaPozicija = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(TrenutnaPozicija);
            TrenutnoVrijemeTextView.setText(FormatiranjeVremena(TrenutnaPozicija));
            UkupnoVrijemeTextView.setText(FormatiranjeVremena(UkupnoVrijeme - TrenutnaPozicija));
            handler.postDelayed(runnable, 100);
        }
    }
    private String FormatiranjeVremena(int vrijemeuMilis) {
        int sekunde = vrijemeuMilis / 1000;
        int minute = sekunde / 60;
        sekunde %= 60;
        return String.format("%02d:%02d", minute, sekunde);
    }

    private void AzuriranjeTextViewova() {
        int currentTime = mediaPlayer.getCurrentPosition();
        int minute = (currentTime / 1000) / 60;
        int sekunde = (currentTime / 1000) % 60;
        String TrenutnoFormatiranoVrijeme = String.format("%02d:%02d", minute, sekunde);
        TrenutnoVrijemeTextView.setText(TrenutnoFormatiranoVrijeme);

        minute = (UkupnoVrijeme / 1000) / 60;
        sekunde = (UkupnoVrijeme / 1000) % 60;
        String UkupnoFormatiranoVrijeme = String.format("%02d:%02d", minute, sekunde);
        UkupnoVrijemeTextView.setText(UkupnoFormatiranoVrijeme);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Zaustavi_Pokreni:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    PlayStopGumb.setImageResource(R.drawable.pokreni);
                    StopRotacije();
                } else {
                    mediaPlayer.start();
                    PlayStopGumb.setImageResource(R.drawable.pauziraj);
                    StartRotacije();
                }
                // Ažuriranje elemenata korisničkog sučelja odmah nakon što korisnik klikne gumb za reprodukciju/pauzu
                AzuriranjeTextViewova();
                AzuriranjePozicijeSeekbara();
                break;
            case R.id.Prijasnja:
                PustanjePrijasnjePjesme();
                // Ažuriranje elemenata korisničkog sučelja odmah nakon što korisnik klikne gumb Prijašnja
                AzuriranjeTextViewova();
                AzuriranjePozicijeSeekbara();
                break;
            case R.id.Sljedeca:
                PustanjeSljedecePjesme();
                // Ažuriranje elemenata korisničkog sučelja odmah nakon što korisnik klikne gumb Prijašnja
                AzuriranjeTextViewova();
                AzuriranjePozicijeSeekbara();
                break;
            case R.id.LoopGumb:
                // Loop gumb je pritisnut
                loopaLiSe = !loopaLiSe; // mijenjanje loop stanja
                if (loopaLiSe) {
                    LoopGumb.setImageResource(R.drawable.loop_upaljen); // mijenja se slika
                    mediaPlayer.setLooping(true);
                } else {
                    LoopGumb.setImageResource(R.drawable.loop_ugasen); // mijenja se slika
                    mediaPlayer.setLooping(false);
                }
                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mediaPlayer.seekTo(progress);
            AzuriranjeTextViewova();
        }
        if (fromUser && progress == seekBar.getMax()) {
            //Ako je SeekBar korisnik postavio na max, pokreće se rotacija i pušta sljedeca pjesma
            PustanjeSljedecePjesme();
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // Uklonjaju se sva ažuriranja pozicije SeekBar dok korisnik s njom komunicira
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Kada korisnik pusti seekbar, nastavlja se s ažuriranjem položaja seekbara
        int TrenutnaPozicija = seekBar.getProgress();
        mediaPlayer.seekTo(TrenutnaPozicija);
        AzuriranjeTextViewova();
        handler.postDelayed(runnable, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(runnable);
        StopRotacije();
    }
}