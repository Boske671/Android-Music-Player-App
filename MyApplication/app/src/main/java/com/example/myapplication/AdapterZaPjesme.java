package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterZaPjesme extends RecyclerView.Adapter<AdapterZaPjesme.MusicViewHolder> {
    private ArrayList<String> ListaPjesama;
    private Context context;
    private SharedPreferences sharedPreferences;

    public AdapterZaPjesme(ArrayList<String> ListaPjesama, Context context, SharedPreferences sharedPreferences) {
        this.ListaPjesama = ListaPjesama;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pjesma_u_listi, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final String ImePjesme = ListaPjesama.get(position);
        holder.ImePjesmeTextView.setText(ImePjesme);
        holder.GumbZaMicanjePjesme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Jeste li sigurni da želite maknuti pjesmu iz liste?");
                builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListaPjesama.remove(ImePjesme);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(ImePjesme, ImePjesme);
                        editor.apply();
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AktivnostPjesama.class);
                intent.putStringArrayListExtra("lista_pjesama", ListaPjesama);
                intent.putExtra("ime_pjesme", ImePjesme);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ListaPjesama.size();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView ImePjesmeTextView;
        ImageButton GumbZaMicanjePjesme;

        public MusicViewHolder(View itemView) {
            super(itemView);
            ImePjesmeTextView = itemView.findViewById(R.id.ImePjesme);
            ImePjesmeTextView.setSelected(true);
            //OVO SVE JE POTREBNO ZA ANIMACIJU PREDUGOG TEKSTA (UKLJUCUJUCI: ImePjesmeTextView.setSelected(true))
            //        android:ellipsize="marquee"
            //        android:singleLine="true"
            //        android:marqueeRepeatLimit="marquee_forever"
            //        android:scrollHorizontally="true"
            GumbZaMicanjePjesme = itemView.findViewById(R.id.GumbZaMicanje);
        }
    }
}

