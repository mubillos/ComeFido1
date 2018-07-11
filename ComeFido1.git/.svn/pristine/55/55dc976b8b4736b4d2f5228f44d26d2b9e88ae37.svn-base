#include <SoftwareSerial.h>
#include <Servo.h>


SoftwareSerial ArduinoUno(2,4); // Siempre es (Rx,Tx)
String MensajeParaESP;

const int analogInPinVerde = A1; // Pin analogico 1 para la lectura del Fototransistor de arriba
const int OutPinVerde = 7;      // Pin para el led indicador verde
int sensorValueVerde = 0;        // Inicializamos el valor del sensor verde
const int analogInPinRojo = A2; // Pin analogico 2 para la lectura del Fototransistor de abajo
const int OutPinRojo = 8;      // Pin para el led indicador rojo
int sensorValueRojo = 0;        // Inicializamos el valor del sensor rojo
String tanque = "";

//Variable donde almacenaremos el valor del potenciometro
long valorPoten=0;
Servo myservo;  // create servo object to control a servo
int potpin = 0;  // analog pin used to connect the potentiometer
int TamanioPorcion = 0; // 0=nada 1=pequenia 2=mediana 3=grande

int senialCarrera = 13; //pin fin de carrera
int valorCarrera; //recibire el valor del fin de carrera

unsigned long startMillis;  //some global variables available anywhere in the program
unsigned long currentMillis;
int peticion = 0; //indica si estoy sirviendo a una orden o no


const int buzzer = 6; //buzzer to arduino pin 6

int basura=0;

/**
 
const int c = 261;
const int d = 294;
const int e = 329;
const int f = 349;
const int g = 391;
const int gS = 415;
const int a = 440;
const int aS = 455;
const int b = 466;
const int cH = 523;
const int cSH = 554;
const int dH = 587;
const int dSH = 622;
const int eH = 659;
const int fH = 698;
const int fSH = 740;
const int gH = 784;
const int gSH = 830;
const int aH = 880;
 
const int buzzerPin = 6;
 
int counter = 0;

**/




void setup(){
  pinMode(OutPinVerde, OUTPUT); //Pin de Led Verde en modo output
  pinMode(OutPinRojo, OUTPUT); //Pin de Led Rojo en modo output
  
  //Inicializamos la comunicación serial
  Serial.begin(9600);
  //Escribimos por el monitor serie mensaje de inicio
  myservo.attach(10);  // attaches the servo on pin 10 to the servo object

  pinMode(senialCarrera, INPUT); //pin fin de carrera en modo input

  ArduinoUno.begin(115200);

  startMillis = 0; //seteo en 0 para que la primer pasada tenga contra que restar

  Serial.println("esperando por ordenes...");

/**
  //Setup pin modes
  pinMode(buzzerPin, OUTPUT);
**/
}


void loop(){
  // leemos el pin para y asignamos el valor a la variable.
  sensorValueVerde = analogRead(analogInPinVerde);
  sensorValueRojo = analogRead(analogInPinRojo);
  
  // Si el valor obtenido es mayor a 10 se activa el LED
  if(sensorValueVerde < 60)
  {
    digitalWrite(OutPinVerde, HIGH); //activa led verde (estado alto)
    tanque = "3";
  }
  else
  {
    digitalWrite(OutPinVerde, LOW);
    tanque = "2";
  }
  if(sensorValueRojo > 34)
  {
    digitalWrite(OutPinRojo, HIGH); // activa led rojo y buzzer (estado bajo)
    tanque = "1";
  }
  else
  {
    digitalWrite(OutPinRojo, LOW);
    //tanque = "2";
  }


  Serial.println("valor verde:");
  Serial.println(sensorValueVerde);
  Serial.println("valor rojo:");
  Serial.println(sensorValueRojo);



  // leemos del pin A0 valor
  valorPoten = analogRead(potpin);
  valorPoten = map(valorPoten, 5, 1022, 13, 67);     // scale it to use it with the servo (value between 0 and 180)
  //Imprimimos por el monitor serie
  //Serial.print("El valor es = ");
  //Serial.println(valorPoten);
  if(valorPoten<=16){
    TamanioPorcion=0;
    if(peticion == 0){
      myservo.write(10);
    }
  }
  if(valorPoten>16 && valorPoten<=33){
    TamanioPorcion=1;
    myservo.write(27);
  }
  if(valorPoten>33 && valorPoten<=45){
    TamanioPorcion=2;
    myservo.write(40);
  }
  if(valorPoten>45 && valorPoten<=67){
    TamanioPorcion=3;
    myservo.write(60);
  }

  //veo si el plato esta saturado - 1 es NO, 0 es SI
  valorCarrera = digitalRead(senialCarrera);
  
  //recibo orden desde el ESP o no
  String orden = "";
  char character;
  while(ArduinoUno.available()) {
    character = ArduinoUno.read();
    orden.concat(character);
    Serial.println("===============================================");
    Serial.println(orden);
  }

  
  if (orden != "" && valorCarrera == 1) {
    //codigo de apertura con servo


//  Serial.println("ORDEN = ");
//  Serial.println(orden);
//  Serial.println("CARRERA =");
//  Serial.println(valorCarrera);
    
    switch (orden.toInt()) {
    case 1:
      Serial.println("sirviendo porcion pequenia");
      myservo.write(33);
      break;
    case 2:
      Serial.println("sirviendo porcion mediana");
      myservo.write(45);
      break;
    case 3:
      Serial.println("sirviendo porcion grande");
      myservo.write(67);
      break;
    case 4:
      Serial.println("BZZZZZ-ings!");
      tone(buzzer, 1000, 500); // Send 1KHz sound signal...
      basura=1;

/**
 
  //Play first section
  firstSection();
 
  //Play second section
  secondSection();
 
  //Variant 1
  beep(f, 250);  
  beep(gS, 500);  
  beep(f, 350);  
  beep(a, 125);
  beep(cH, 500);
  beep(a, 375);  
  beep(cH, 125);
  beep(eH, 650);
 
  delay(500);
 
  //Repeat second section
  secondSection();
 
  //Variant 2
  beep(f, 250);  
  beep(gS, 500);  
  beep(f, 375);  
  beep(cH, 125);
  beep(a, 500);  
  beep(f, 375);  
  beep(cH, 125);
  beep(a, 650);  
 
  delay(650);

**/
      
      break;
    default:
      Serial.println("recibi basura");
      basura=1;
      break;
    }
    if(basura == 0){
      currentMillis = millis(); //ejecuta solo en la pasada que recive una orden
      startMillis = currentMillis;
      peticion = 1;
    }
    if(basura == 1){
      basura = 0;
    }
  } //deberia solo haber nada o alguna orden 1, 2, 3, o 4

  if(orden != "" && valorCarrera == 0){
    //ignoro orden y aviso que plato esta saturado     
    MensajeParaESP = "respuesta=0&plato=0&tanque=" + tanque + "z";
    Serial.println("mando mensaje de error");
    ArduinoUno.println(MensajeParaESP); //envia mensaje al ESP
  }
  //cerrado con millis
  currentMillis = millis();
  if(TamanioPorcion == 0 && peticion == 1){ //evita que se cierre si estoy manualmente abriendo
    if (currentMillis - startMillis >= 2000){  //ejecuta en la primer pasada 2 segundos despues de abrir
      myservo.write(13); //cierra el servo
      peticion = 0; //indica que la peticion se atendio y mandará una confirmacion
      valorCarrera = digitalRead(senialCarrera);
      if(sensorValueVerde < 60)
      {
        digitalWrite(OutPinVerde, HIGH); //activa led verde (estado alto)
        tanque = "3";
      }
      else
      {
        digitalWrite(OutPinVerde, LOW);
        tanque = "2";
      }
      if(sensorValueRojo > 34)
      {
        digitalWrite(OutPinRojo, HIGH); // activa led rojo y buzzer (estado bajo)
        tanque = "1";
      }
      else
      {
        digitalWrite(OutPinRojo, LOW);
        //tanque = "2";
      }
      MensajeParaESP = "respuesta=1&plato=" + String(valorCarrera) + "&tanque=" + String(tanque) + "z";
      if(tanque == 1){
        MensajeParaESP = "respuesta=0&plato=" + String(valorCarrera) + "&tanque=" + "1" + "z";
      }
      Serial.println("mando mensaje de con dato: " + MensajeParaESP);
      ArduinoUno.println(MensajeParaESP); //envia mensaje al ESP
    }
  }
}





/**

void beep(int note, int duration)
{
  //Play tone on buzzerPin
  tone(buzzerPin, note, duration);
 
  //Play different LED depending on value of 'counter'
  
    delay(duration);
  
 
  //Stop tone on buzzerPin
  noTone(buzzerPin);
 
  delay(50);
 
}
 
void firstSection()
{
  beep(a, 500);
  beep(a, 500);    
  beep(a, 500);
  beep(f, 350);
  beep(cH, 150);  
  beep(a, 500);
  beep(f, 350);
  beep(cH, 150);
  beep(a, 650);
 
  delay(500);
 
  beep(eH, 500);
  beep(eH, 500);
  beep(eH, 500);  
  beep(fH, 350);
  beep(cH, 150);
  beep(gS, 500);
  beep(f, 350);
  beep(cH, 150);
  beep(a, 650);
 
  delay(500);
}
 
void secondSection()
{
  beep(aH, 500);
  beep(a, 300);
  beep(a, 150);
  beep(aH, 500);
  beep(gSH, 325);
  beep(gH, 175);
  beep(fSH, 125);
  beep(fH, 125);    
  beep(fSH, 250);
 
  delay(325);
 
  beep(aS, 250);
  beep(dSH, 500);
  beep(dH, 325);  
  beep(cSH, 175);  
  beep(cH, 125);  
  beep(b, 125);  
  beep(cH, 250);  
 
  delay(350);
}

**/
