/************************************************
 Listeden tıklandığında gidilen ikinci ekran
 *************************************************/
package com.esraunal.b171210086iotodevi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView wattText= findViewById(R.id.t_watt);
        TextView tlText= findViewById(R.id.t_tl);

        //Gelen verilerin textviewlara gönderilmesi
        Intent intent= getIntent();
        wattText.setText("Watt: "+ intent.getStringExtra("watt"));
        tlText.setText( "TL: "+ intent.getStringExtra("tl"));

    }
}