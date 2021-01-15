#include <ESP8266WiFi.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>
#include <time.h>
#include <Keypad.h>
#include "FirebaseESP8266.h"

//Firebase
#define FIREBASE_HOST " " 
#define FIREBASE_AUTH " " 
FirebaseData firebaseData;

#define WATT_PERHOUR 10  //10 Watt'lık bir tasarruf ampulünün saatlik harcadığı watt miktarı

int timezone = 3;
int dst = 0;

WiFiManager wifiManager;

//Keypad
const byte n_rows = 4; //four rows
const byte n_cols = 3; //three columns

char keys[n_rows][n_cols] = {
  {'1','2','3'},
  {'4','5','6'},
  {'7','8','9'},
  {'*','0','#'}
};
byte rowPins[n_rows] = {D0, D1, D2, D3};
byte colPins[n_cols] = {D4, D5, D6};
Keypad delayGiris = Keypad( makeKeymap(keys), rowPins, colPins, n_rows, n_cols); 

void setup() {
    pinMode(D8, OUTPUT);
    digitalWrite(D8,LOW);
  
    Serial.begin(115200);
    Serial.setDebugOutput(true);

    //zamanın ayarlanması
    configTime(timezone * 3600, dst * 0, "pool.ntp.org", "time.nist.gov");

    //firebase başlatılır
    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH); 
}

void loop() {
  //Kullanıcıdan wifi kontrolü için delay miktarı alınır
      Serial.println("GİRİŞ YAPINIZ");
      delay(500);
      Serial.println("----------------------------------------");
      char delayG= delayGiris.getKey();
      if (delayG !=NULL){
      Serial.print("Key pressed: ");
      Serial.println(delayG);
      }  
      int delayTime=((int)(delayG-48))*1000;
      
      //delay zamanı girilmişse wifi connection ister
      if(!(delayTime<0)){
     if(wifiManager.autoConnect("AutoConnectAP")){
      
      digitalWrite(D8,HIGH); //led yakılır

      //ledin yakıldığı zaman hesaplanır
      time_t now = time(nullptr); 
      char* startTime=ctime(&now);
      String data="";
      for(int i=0; i<=23; i++){
        data+=startTime[i];
      }

      //kullanıcının girdiği süre sonra yeniden saatin kaç olduğuna bakılır
      delay(delayTime);
      time_t now2 = time(nullptr);
      char* finishTime=ctime(&now2);
      String data2="";
      for(int i=0; i<=23; i++){
        data2+=finishTime[i];
      }

      //database gönderilmek için iki zaman dilimi birleştirilir
       String totaltime= data+ "-"+ data2;
       
       //database gönderilecek olan, watt miktarı hesaplanır
       int value= (delayTime/1000)*WATT_PERHOUR;

       //database'e gönderilir.
    if(Firebase.set(firebaseData, totaltime ,value)){
        //data başarılı bir şekilde gönderildi.
        Serial.println("Set int data success ");
        Serial.println(totaltime);
        wifiManager.resetSettings(); //wifi resetlenir
        delay(1000);
 
    }else{
      //data gönderilemedi.
      Serial.print("Error in setInt, ");
      Serial.println(firebaseData.errorReason());
      }

  }
     digitalWrite(D8,LOW); //bağlantı koptuğu için led söndürülür.
 }
}
