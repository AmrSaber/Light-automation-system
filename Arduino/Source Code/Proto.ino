#include <SoftwareSerial.h>
#include <NewPing.h>

int bulb = 5;
bool lightOn = false;
bool lightOff = false;

int heatSensor = A0;
float creticalTemp = 25;

int buzzer = 4;
//int bzr_vol = 200;

SoftwareSerial HC05(2, 3); //RX, TX

NewPing sonarOut(8, 9, 200); //Trig, Echo
NewPing sonarIn(10, 11, 200);  //Trig, Echo

bool sonarOn = true;
int dIn, dOut,
    sMinDistance = 70,
    smedian = 2,
    sDelay = 100,
    sCount = 0,
    spCount = 0,
    sReset = 15;
bool sOut = false,
     sIn = false;

void setup() {

  pinMode(bulb, OUTPUT);
  pinMode(buzzer, OUTPUT);
  HC05.begin(9600);
  Serial.begin(9600);

}

void loop() {

  if(HC05.available()){
    bluetooth();
  }
  
  if(sonarOn){
    ultrasonic();
  }

  if((spCount > 0 && !lightOff) || lightOn){
    light(true);
  }else{
    light(false);
  }

}

void light(bool turnOn){
  if(turnOn){
    digitalWrite(bulb, HIGH);
  }else{
    digitalWrite(bulb, LOW);
  }
}

void bluetooth(){
  int val = HC05.read();
  //Serial.println((char)val);
  if(val == 'B'){
    sonarOn = false;
    resetSonar();
    Serial.println("\n#### Sonar off ####");
  }else if(val == 'F'){
    sonarOn = true;
    Serial.println("\n#### Sonar on ####");
  }else if(val == 'R'){
    lightOff = false;
    lightOn = true;
    Serial.println("\n#### Light on ####");
  }else if(val == 'L'){
    lightOn  = false;
    lightOff = true;
    Serial.println("\n#### Light off ####");
  }else if(val == 'A'){
    lightOn = false;
    lightOff = false;
  }else if(val == 'T'){ //get Temperature
    int t = getTemp();
    HC05.write((char)t);
    Serial.print("\n#### Sent Temperature : ");
    Serial.print(t);
    Serial.println(" ####");
  }else if(val == 'G'){  //get people count
    HC05.write(spCount);
    Serial.print("\n#### Sent People count : ");
    Serial.print(spCount);
    Serial.println(" ####");
  }else if(val == 'S'){ //set people count
    delay(150);
    if( !HC05.available() ) return;
    int p = HC05.read();
    if(p < 0){p = 0;}
    spCount = p;
    Serial.print("\n#### Set new People count : ");
    Serial.print(spCount);
    Serial.println(" ####");
    
  }
}

void ultrasonic(){

  dOut = sonarOut.ping_median(smedian)/48;
  dIn = sonarIn.ping_median(smedian)/48;


  if( (dIn < sMinDistance && dIn != 0) && (dOut > sMinDistance || dOut == 0)){
    if(sOut){
      sOut = false;
      spCount++;
      Serial.print("\nPeople count : ");
      Serial.println(spCount);
      digitalWrite(buzzer, HIGH);
      delay(sDelay);
      digitalWrite(buzzer, LOW);
    }else{
      if(!sIn){
        Serial.println("Sonar In - true");
    }
      sIn = true;
    }
  }

  if( (dOut < sMinDistance && dOut != 0) && (dIn > sMinDistance || dIn == 0)){
    if(sIn){
      sIn = false;
      //pCount--;
      spCount = (spCount == 0)? 0 : spCount-1;
      Serial.print("\nPeople count : ");
      Serial.println(spCount);
      digitalWrite(buzzer, HIGH);
      delay(sDelay);
      digitalWrite(buzzer, LOW);
      return;
    }else{
      if(!sOut){
        Serial.println("Sonar Out - true");
      }
      sOut = true;
    }
  }

  if(dOut < sMinDistance && dIn < sMinDistance && dOut != 0 && dIn != 0){
    Serial.println("********** Restart - Sonar **********");
    return;
  }

  if(sOut || sIn){
    sCount++;
  }

  if(sCount > sReset){
    sOut = sIn = false;
    sCount = 0;
    Serial.print("---------------\nReset - Sonar\n---------------\n");
  }
  
}

void resetSonar(){
  dIn = 0;
  dOut = 0;
  sCount = 0;
  spCount = 0;
  sOut = false;
  sIn = false;
}

float getTemp(){
  int r = analogRead(A0);
  float v = r * 5.0/1024; //get the output voltage
  return abs((v)*100);  //transform the voltage into temperature
}

