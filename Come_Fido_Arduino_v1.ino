#include <SoftwareSerial.h>
#include <Servo.h>


SoftwareSerial ArduinoUno(2,4); // Siempre es (Rx,Tx)
String MensajeParaESP;

const int analogInPinVerde = A1; // Pin analogico 1 para la lectura del Fototransistor de arriba
const int OutPinVerde = 7;      // Pin para el led indicador verde
int sensorValueVerde = 0;        // Inicializamos el valor del sensor verde
const int analogInPinRojo = A2; // Pin analogico 2 para la lectura del Fototransistor de abajo
const int OutPinRojo = 12;      // Pin para el led indicador rojo y buzzer
int sensorValueRojo = 0;        // Inicializamos el valor del sensor rojo

//Variable donde almacenaremos el valor del potenciometro
long valorPoten=0;
Servo myservo;  // create servo object to control a servo
int potpin = 0;  // analog pin used to connect the potentiometer
int TamanioPorcion = 0; // 0=nada 1=pequenia 2=mediana 3=grande

int senialCarrera = 13; //pin fin de carrera
int valorCarrera; //recibire el valor del fin de carrera


void setup(){
  pinMode(OutPinVerde, OUTPUT); //Pin de Led Verde en modo output
  pinMode(OutPinRojo, OUTPUT); //Pin de Led Rojo en modo output
  
  //Inicializamos la comunicación serial
  Serial.begin(9600);
  //Escribimos por el monitor serie mensaje de inicio
  myservo.attach(10);  // attaches the servo on pin 10 to the servo object

  pinMode(senialCarrera, INPUT); //pin fin de carrera en modo input

  ArduinoUno.begin(115200);

}


void loop(){
  // leemos el pin para y asignamos el valor a la variable.
  sensorValueVerde = analogRead(analogInPinVerde);
  sensorValueRojo = analogRead(analogInPinRojo);
  
  // Si el valor obtenido es mayor a 10 se activa el LED
  if(sensorValueVerde < 10)
  {
    digitalWrite(OutPinVerde, HIGH); //activa led verde (estado alto)
  }
  else
  {
    digitalWrite(OutPinVerde, LOW);
  }
  if(sensorValueRojo > 10)
  {
    digitalWrite(OutPinRojo, HIGH); // activa led rojo y buzzer (estado bajo)
  }
  else
  {
    digitalWrite(OutPinRojo, LOW);
  }

  // leemos del pin A0 valor
  valorPoten = analogRead(potpin);
  valorPoten = map(valorPoten, 5, 1022, 13, 67);     // scale it to use it with the servo (value between 0 and 180)
  myservo.write(valorPoten);                  // sets the servo position according to the scaled value
  //Imprimimos por el monitor serie
  Serial.print("El valor es = ");
  Serial.println(valorPoten);
  if(valorPoten<=13){
    TamanioPorcion=0;
  }
  if(valorPoten>14 && valorPoten<=33){
    TamanioPorcion=1;
  }
  if(valorPoten>33 && valorPoten<=45){
    TamanioPorcion=2;
  }
  if(valorPoten>45 && valorPoten<=67){
    TamanioPorcion=3;
  }
  valorCarrera = digitalRead(senialCarrera);
  if (valorCarrera == 1){ //plato vacio - disponible para llenar
    while(valorCarrera == 1){ // ciclo while para que el servo se corra a la posicion determinada por TamanioPorcion y no segun el potenciometro
        //cuenta tiempo segun TamanioPorcion
    } //plato lleno
  }
  ArduinoUno.println(MensajeParaESP); //envia mensaje al ESP


}
