#include "ESP8266WiFi.h"
#include <SoftwareSerial.h>

// WiFi parameters
const char* ssid = "SOa-IoT-N750";// "SOa-IoT-N750";
const char* password = "ioteshumo";// "ioteshumo";
                    
String thingName1="ComeFido1"; //entran ordenes - redirigir a TX
String thingName2="ComeFido2"; //salen estados - redirigir de RX

const char* host = "dweet.io";

unsigned long startMillis;  
unsigned long currentMillis;
unsigned long connectStartMillis;
unsigned long connectCurrentMillis;
String ordenVieja;
String ordenNueva;

//TX y RX
SoftwareSerial esp(4,5);
String estados = "";


void setup() {
  Serial.begin(9600);
  //wifi things
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  //initialice and connect to wifi lan
  WiFi.begin(ssid, password);  
  int retries = 0;
  while ((WiFi.status() != WL_CONNECTED) && (retries < 15)) {
    retries++;
//    delay(500);
    connectStartMillis = millis();
    connectCurrentMillis = connectStartMillis;
    while(connectCurrentMillis - connectStartMillis < 500){
      connectCurrentMillis = millis();
      yield();
    }

    Serial.print(".");
  }
  
  if(retries>14){
    Serial.println(F("WiFi conection FAILED"));
  }
  if (WiFi.status() == WL_CONNECTED) {
  Serial.println(F("WiFi connected"));
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  Serial.println(F("======================================================")); 
  }
  Serial.println(F("Setup ready"));

  //tomar la orden antigua para no tomarla como nueva
  getdweetdata();//pull it 1st time
  Serial.println("1er linea leida");
  Serial.println(ordenNueva);
  ordenVieja = ordenNueva;

  esp.begin(115200);
}

void loop() {
  // leer de Dweet cada 1.5 seg para no saturar
  currentMillis=millis();
//  if(currentMillis - startMillis > 1500){
    getdweetdata();
    startMillis = millis();
    if(!(ordenNueva.equals(ordenVieja))){
      Serial.println("NUEVA ORDEN");
      Serial.println(millis());
      ordenVieja=ordenNueva;
      int indice = ordenNueva.indexOf("}");
      String valorCantidad = ordenNueva.substring(indice-3,indice-2);
//      Serial.println(valorCantidad);
      esp.println(valorCantidad);
    }
//  }
  // leer de arduino si hay algo
  char character;
  estados="";
  if(esp.available()){
    estados = esp.readStringUntil('z');
//      Serial.println(estados);
    /**
    while(estados.charAt(estados.length() != 'z')){
      while(esp.available()) {
        character = esp.read();
        estados.concat(character);
        Serial.println(estados);
      }
      //yield();
      Serial.println(estados);
      Serial.println("esperando por TODO el mensaje...");
    }   **/
    estados.remove(estados.indexOf("z"));
//    Serial.println("++++++++++++++++++++++++++++++");
//    Serial.println("MENSAJE PARA DWEET:");
  }

  if (estados != "") {
//    Serial.println(estados);
    postdweetdata();
      Serial.println("FIN");
      Serial.println(millis());
  }
}


void getdweetdata(){//connects TCP,sends dweet,drops connection, prints the server response on the console 
  // Use WiFiClient class to create TCP connections
  WiFiClient client;
  const int httpPort = 80;
  if (!client.connect(host, httpPort)) {
    Serial.println("connection failed");
    return;
  }

    client.print(GetDweetStringGetHttpBuilder());
//  delay(3000);//slow doown ...stability stuff
  startMillis = millis();
  currentMillis = startMillis;
  while(currentMillis - startMillis < 2000){
    currentMillis = millis();
    yield();
  }

  // Read all the lines of the reply from dweet server and print them to Serial
  
//  Serial.println("BYTES PARA LEER");
//  Serial.println(client.available());
  while(client.available()){
    ordenNueva = client.readStringUntil('\r');
//    Serial.print(ordenNueva);
    
  }
  //just display ending conection on the serial port
//  Serial.println();
//  Serial.println("closing GET connection");
   
}

String GetDweetStringGetHttpBuilder() {
  String dweetHttpGet="GET /get/latest/dweet/for/";//initial empty get request
  dweetHttpGet=dweetHttpGet+String(thingName1);//start concatenating the thing name (dweet.io) 
  dweetHttpGet=dweetHttpGet+" HTTP/1.1\r\n"+
                  "Host: " + 
                    host + 
                  "\r\n" + 
                  "Connection: close\r\n\r\n";
                  
  
//    Serial.println(dweetHttpGet);
  return dweetHttpGet;//this is our freshly made http string request
}

void postdweetdata(){//connects TCP,sends dweet,drops connection, prints the server response on the console 
  // Use WiFiClient class to create TCP connections
  WiFiClient client;
  const int httpPort = 80;
  if (!client.connect(host, httpPort)) {
    Serial.println("connection failed");
    return;
  }
  client.print(PostDweetStringGetHttpBuilder());
  //just display ending conection on the serial port
//  Serial.println();
//  Serial.println("closing POST connection");
   
}

String PostDweetStringGetHttpBuilder() {
  String dweetHttpGet="GET /dweet/for/";//initial empty get request
  dweetHttpGet=dweetHttpGet+String(thingName2)+"?";//start concatenating the thing name (dweet.io) 
  dweetHttpGet=dweetHttpGet+estados;
  dweetHttpGet=dweetHttpGet+" HTTP/1.1\r\n"+
                  "Host: " + 
                    host + 
                  "\r\n" + 
                  "Connection: close\r\n\r\n";
                  
  return dweetHttpGet;//this is our freshly made http string request
}
