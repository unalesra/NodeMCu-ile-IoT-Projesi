/***********************************************
 Ana ekran
 *************************************************/
package com.esraunal.b171210086iotodevi;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListViewing(); //Liste görünümünün oluşturulduğu metod
    }

    public void ListViewing(){
        final ListView listView=findViewById(R.id.listView);
        final ArrayList<Object> dates= new ArrayList<>();
        final ArrayList<String> watts= new ArrayList<>();
        final ArrayList<String> TLs= new ArrayList<>();

        //Veritabanı bağlantısı yapılır
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReferenceFromUrl("https://iotodevi-e806a.firebaseio.com/");

        // Veritabanından okuma yapma
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long dataCount=dataSnapshot.getChildrenCount();  //kaç veri olduğu alınır
                Iterable datas=dataSnapshot.getChildren(); //datalar veritabanından alınır.
                dates.clear();
                watts.clear();
                TLs.clear();

                //listelerin içerisine veriler koyulur
                for(int i=0; i<dataCount; i++){
                    dataSnapshot= (DataSnapshot) datas.iterator().next();
                    dates.add(dataSnapshot.getKey());
                    watts.add(dataSnapshot.getValue().toString());
                    TLs.add(calculateTL(dataSnapshot.getValue()));
                }

                //listeye tıklandığı zaman açılacak olan sayfaya(DetailActivity) verilerin gönderilmesi
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent= new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("watt", watts.get(i));
                        intent.putExtra("tl", TLs.get(i));
                        startActivity(intent);
                    }
                });

                //arraylistler ile listeview'un bağlanması
                ArrayAdapter arrayAdapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1, dates);
                listView.setAdapter(arrayAdapter);

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Eğer veritabanından veri okunamazsa
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    //okunan watt değerinin kaç TL'ye denk geldiğinin hesaplanması
    String calculateTL(Object value){
        Double tl=null;
        Double watt=null;
        if(value instanceof  Number){
            tl=((Number) value).doubleValue();
            watt=((Number) value).doubleValue();
        }
        watt=watt/1000;
        tl=watt*0.53d;
        return Double.toString(tl);
    }
}
