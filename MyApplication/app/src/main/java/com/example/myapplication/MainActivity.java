    package com.example.myapplication;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.Manifest;
    import android.content.ContentResolver;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.database.Cursor;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.view.View;
    import android.widget.Button;

    import java.util.ArrayList;

    public class MainActivity extends AppCompatActivity {

        private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
        RecyclerView Lista;
        AdapterZaPjesme AdapterPjesama;
        ArrayList<String> ListaPjesama;
        SharedPreferences sharedPreferences;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Lista = findViewById(R.id.RecyclerView);
            ListaPjesama = new ArrayList<String>();
            sharedPreferences = getSharedPreferences("maknute_pjesme", MODE_PRIVATE);
            AdapterPjesama = new AdapterZaPjesme(ListaPjesama, this, sharedPreferences);

            Lista.setLayoutManager(new LinearLayoutManager(this));
            Lista.setAdapter(AdapterPjesama);

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Prikaži objašnjenje korisniku *asinkrono* -- ne blokiraj
                    // ova nit čeka odgovor korisnika! Nakon što korisnik
                    // vidi objašnjenje, pokušava se ponovno zatražiti dopuštenje.
                } else {
                    // traži se dopuštenje
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
            } else {
                DobivanjePjesama();
                AdapterPjesama = new AdapterZaPjesme(ListaPjesama, this, sharedPreferences);
                Lista.setLayoutManager(new LinearLayoutManager(this));
                Lista.setAdapter(AdapterPjesama);
            }

            Button OsvjeziGumb = findViewById(R.id.OsvjeziGumb);
            OsvjeziGumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sharedPreferences.edit().clear().apply();
                    DobivanjePjesama();
                }
            });

        }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DobivanjePjesama();
                } else {
                    // dopuštenje odbijeno
                }
                return;
            }
        }

        private void DobivanjePjesama() {
            ListaPjesama.clear();
            ContentResolver contentResolver = getContentResolver();
            Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            Cursor CursorPjesama = contentResolver.query(songUri, null, selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC");
            if (CursorPjesama != null && CursorPjesama.moveToFirst()) {
                int songTitle = CursorPjesama.getColumnIndex(MediaStore.Audio.Media.TITLE);
                do {
                    String TrenutnaPjesma = CursorPjesama.getString(songTitle);
                    String MaknutaPjesma = sharedPreferences.getString(TrenutnaPjesma, null);
                    if (MaknutaPjesma == null) {
                        ListaPjesama.add(TrenutnaPjesma);
                    }
                } while (CursorPjesama.moveToNext());
            }
            CursorPjesama.close();
            AdapterPjesama.notifyDataSetChanged();
        }
    }