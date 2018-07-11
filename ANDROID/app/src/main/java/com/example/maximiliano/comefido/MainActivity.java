package com.example.maximiliano.comefido;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity implements View.OnClickListener, SensorEventListener{


    ////variables globales/////////////////
    private SensorManager mSensorManager; //variable para manejar los sensores
    TextView texto; // caja de texto para mostrar los mensajes
    RadioButton boton1; //
    RadioButton boton2; //botones del radio button estan contenidos en un radiogroup para que solo se puedan seleccionar
    RadioButton boton3; //uno a la vez
    Button b1;
    Button b;// boton para mandar la orden de largar el alimento
    String dato;
    boolean prox=false;
    boolean lumino=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // metodo que dispara el programa, es el hilo principal
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );// para que no gire la pantalla

        texto = findViewById( R.id.textView2 );
        boton1 = findViewById( R.id.radioButton );
        boton2 = findViewById( R.id.radioButton2 );//traigo a este hilo todas las variables del activity_main.xml
        boton3 = findViewById( R.id.radioButton3 );
        b= findViewById( R.id.button );
        b1=findViewById(R.id.button2);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// variable para acceder a los sensores del celular

        b.setOnClickListener( this );// metodo que dispara el evento cuando se presiona el boton de "ALIMENTAR"


        texto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                texto.setText( "" );
            }

        } );
        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                prox=false;
                lumino=false;
            }

        } );


    }
    ////////////////////////////////////////////////////////////////////////////////////
    protected void Ini_Sensores() {// prepara los sensores para tomar los datos  ,   sensor_delay_normal es la cantidad de tiempo para tomar datos
        mSensorManager.registerListener( this, mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ), SensorManager.SENSOR_DELAY_NORMAL );
        mSensorManager.registerListener( this, mSensorManager.getDefaultSensor( Sensor.TYPE_PROXIMITY ), SensorManager.SENSOR_DELAY_NORMAL );
        mSensorManager.registerListener( this, mSensorManager.getDefaultSensor( Sensor.TYPE_LIGHT ), SensorManager.SENSOR_DELAY_NORMAL );
    }

    private void Parar_Sensores() {// cuando se toman datos este evento detiene el ciclo de vida del sensor

        mSensorManager.unregisterListener( this, mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ) );
        mSensorManager.unregisterListener( this, mSensorManager.getDefaultSensor( Sensor.TYPE_PROXIMITY ) );
        mSensorManager.unregisterListener( this, mSensorManager.getDefaultSensor( Sensor.TYPE_LIGHT ) );
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        //evento que dice que accion realizara cada sensor al tomar datos

        // Cada sensor puede lanzar un thread que pase por aqui
        // Para asegurarnos ante los accesos simultaneos sincronizamos esto

        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:

                    if ((event.values[0] > 15) || (event.values[1] > 15) || (event.values[2] > 15)) {// acelerometro tiene 3 direcciones
                        set();// cambia el radio button por el siguiente al que este seleccionado
                    }
                    break;

                case Sensor.TYPE_PROXIMITY:

                    if (event.values[0] == 0) {
                        if(prox==false)
                        {
                            prox=true;
                            hilo_obtener_historia h4=new hilo_obtener_historia();// muestratodo el historial de envios realizados al alimentador desde el celular
                            h4.set(texto);// muestra el historial por pantalla
                            h4.execute();//dispara el hilo

                        }



                    }
                    break;

                case Sensor.TYPE_LIGHT:
                    if(event.values[0]<5)
                    {
                        if(lumino==false)
                        {
                            lumino=true;
                            hilo_mandar_bus h3=new hilo_mandar_bus();//manda una orden para activar el buzzer del arduino
                            h3.set(texto);// muestra un mensaje en el celular que se envio la orden
                            h3.execute();// dispara el hilo/asyntaks para que se ejecute


                        }



                    }


                    break;


            }
        }
    }

    @Override
    protected void onStop()// metodo que se realiza si el ciclo de vida del sensor esta detenido
    {
        Parar_Sensores();
        super.onStop();
    }

    @Override
    protected void onDestroy()// metodo que se realiza si el ciclo de vida del sensor esta finalizado
    {
        Parar_Sensores();
        super.onDestroy();
    }

    @Override
    protected void onPause()// metodo que se realiza si el ciclo de vida del sensor esta pausado
    {
        Parar_Sensores();
        super.onPause();
    }

    @Override
    protected void onRestart()//// metodo que se realiza si el ciclo de vida del sensor esta restaurado
    {
        Ini_Sensores();
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Ini_Sensores();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {// permite cambiar la toma de muestras del sensor, es como mapear los datos

    }

    public void set()//determina que radiobutton esta seleccionado y lo cambia por el siguiente
    {

        if (boton1.isChecked()==true) {

            boton2.setChecked(true);
        }
        else{

            if(boton2.isChecked()==true)
                boton3.setChecked(true);
            else
                boton1.setChecked(true);
        }
    }



    ///////////////////////////////////////////////////////////
    @Override
    public void onClick(View view) {// evento del boton "alimentar"


        hilo_mandar_datos h2 = new hilo_mandar_datos();
        h2.set(boton1, boton2, boton3);
        h2.execute();

        // dispara un hilo asyntask que envia los datos del radiobutton seleccionado
        texto.setText("procesando...");





            hilo_obtener_datos h1 = new hilo_obtener_datos();// hilo que lee los datos enviados por el arduino al celular
            h1.set( texto,dato );//envio el textview donde quiero mostrar el mensaje
            h1.execute();


        }

    }
//////////////////////////////////////////////////////////////////////////////


class hilo_mandar_datos extends AsyncTask<Void, String, String>///hilo para enviar mensajes para activar el buzzer
{
    //variables globales
    String respuesta=null;
    TextView t=null;
    RadioButton r1,r2,r3;



    ///metodo que se encarga de enviar los datos al arduino y devuelve si se pudo enviar o no esos datos
    public String POST (String uri, String cant)
    {
        HttpURLConnection urlConnection = null;//

        try
        {

            //Se almacena la URL de la request del servicio web
            URL mUrl = new URL(uri);


            //Se arma el request con el formato correcto
            urlConnection = (HttpURLConnection) mUrl.openConnection();//abre la conexion
            urlConnection.setDoOutput(true);//
            urlConnection.setDoInput(true);//permiten la subida y bajada de datos
            urlConnection.setRequestMethod("POST");//establezco que el pedido de conexion al servidor sera para subir o enviar datos


            //Se crea un paquete JSON que envia (cantidad,4),es decir, variable y dato que contiene
            // Este paquete JSON se escribe en el campo body(dato a enviar) del mensaje POST
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());//objeto para enviar flujo de datos

            JSONObject obj = new JSONObject();// creo el JSON para enviar el mensaje

            obj.put("cantidad", cant ); // escribo el mensaje cantidad y su valor que sera el valor del radio button en el JSON

            wr.writeBytes(obj.toString());//lo transforma en flujo de datos para enviarlos a la URL

            wr.flush();// limpio el buffer de datos
            wr.close();// cierro el flujo de datos

            //se envia el request al Servidor
            urlConnection.connect();

            //Se obtiene la respuesta que envio el Servidor ante el request
            int responseCode = urlConnection.getResponseCode();

            urlConnection.disconnect();

            //se analiza si la respuesta fue correcta
            if(responseCode != HttpURLConnection.HTTP_OK)
            {
                return "ERROR AL ENVIAR LOS DATOS...";
            }
        } catch (Exception e)
        {
            return "ERROR AL ENVIAR LOS DATOS...";
        }
        return null;
    }



    @Override
    protected String doInBackground(Void... voids) {// metodo que el hilo debe hacer


        respuesta=POST("http://dweet.io/dweet/for/ComeFido1",String.valueOf( set() ));//le envio la url
        // y el valor almacenado en el radiobutton
        publishProgress(respuesta);// este metodo envia los datos del hilo que trabaja en segundo plano al hilo que
        // esta en primer plano o hilo principal



        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {// este hilo es el principal quien recibe los datos del metodo anterior

        super.onProgressUpdate( values );


    }

    public int set()// obtengo el valor del radiobutton que esta seleccionado
    {
        if (r1.isChecked()==true) {

            return 1;
        }
        else{
            if(r2.isChecked()==true)
                return 2;
            else
                return 3;
        }
    }



    public void set(RadioButton r1,RadioButton r2,RadioButton r3) {//traigo las variables del hilo principal

        this.r1=r1;
        this.r2=r2;
        this.r3=r3;
    }
}


class hilo_obtener_datos extends AsyncTask<Void,String,Void>//obtengo las respuestas del arduino
{
    String datos=null;
    TextView tex=null;
    int res,pla,tan;

    String d=null;


    public String leerDatos(InputStream stream) throws IOException {// metodo que lee datos y me los tranforma a string
        Reader reader = null;
        reader = new InputStreamReader( stream, "UTF-8" );
        char[] buffer = new char[1024];
        StringBuffer bufferDatos = new StringBuffer();
        int cantCaracteresLeidos;

        while ((cantCaracteresLeidos = reader.read( buffer )) != -1) {
            bufferDatos.append( buffer, 0, cantCaracteresLeidos );

        }
        return bufferDatos.toString();
    }

    public String url() throws IOException {// metodo que obtiene los datos del arduino

        InputStream is= null;

        try
        {
            URL url= new URL("https://dweet.io/get/latest/dweet/for/ComeFido2");// pagina donde obtengo los datos
            HttpsURLConnection conexion = (HttpsURLConnection) url.openConnection();
            conexion.setReadTimeout( 10000 );
            conexion.setConnectTimeout( 15000 );
            conexion.setRequestMethod( "GET" );// establezco que la comunicacion sera para leer datos
            conexion.setDoInput( true );//
            conexion.connect();

            try {
                is = conexion.getInputStream();

                String contenidos = leerDatos( is );
                return contenidos;
            }catch (Exception e)
            {
                Log.d( "mi","error en los contenidos" );
            }
        }catch(IOException e)
        {
            Log.d( "mi","error en los contenidos" );
        }

        finally {
            if(is != null)
            {
                is.close();
            }
        }

        return null;
    }

    @Override
    protected Void doInBackground(Void... voids) {


        try {
            d=url();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        datos=d;

        while(d.equals(datos)) {

            try {

                datos = url();


            } catch (IOException e) {

                datos = "ERROR AL CONECTARSE CON EL ALIMENTADOR...";
            }



            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        publishProgress(datos);
            return null;
    }

    protected void onProgressUpdate(String... values)
    {
        if(datos.indexOf("respuesta")!=-1 )
        {


            this.res= Integer.parseInt(datos.substring( datos.indexOf( "respuesta" )+11,datos.indexOf( "respuesta" )+12));
            this.pla=Integer.parseInt(datos.substring( datos.indexOf( "plato" )+7,datos.indexOf( "plato" )+8));
            this.tan=Integer.parseInt(datos.substring( datos.indexOf( "tanque" )+8,datos.indexOf( "tanque" )+9));
            //parseo los datos  me ubico donde esta cada palabra y me corro lo necesario

            if(res==1)
            {
                this.tex.setText( "ORDEN EXITOSA");
            }
            else {
                this.tex.setText( "ORDEN CON ERRORES" );
            }
        }
        else
        {
            this.tex.setText( "ERROR AL CONECTARSE CON EL ALIMENTADOR..." );// sino hay respuesta es decir 0 hay problemas
        }





    }

    public void set(TextView v,String d) {
        this.tex = v;
        this.d=d;
    }
}




class hilo_mandar_bus extends AsyncTask<Void, String, String>///metodo para mandar el dato que encienda el buzzer
{
    String respuesta=null;
    TextView t=null;


    public String POST (String uri, String cant)
    {
        HttpURLConnection urlConnection = null;

        try
        {

            //Se alamacena la URI del request del servicio web
            URL mUrl = new URL(uri);


            //Se arma el request con el formato correcto
            urlConnection = (HttpURLConnection) mUrl.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");

            //Se crea un paquete JSON que indica el estado(encendido o apagado) del led que se desea
            //modificar. Este paquete JSON se escribe en el campo body del mensaje POST
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());

            JSONObject obj = new JSONObject();
            //JSONArray o=new JSONArray( );

            obj.put("cantidad", cant );
            wr.writeBytes(obj.toString());


            wr.flush();
            wr.close();

            //se envia el request al Servidor
            urlConnection.connect();

            //Se obtiene la respuesta que envio el Servidor ante el request
            int responseCode = urlConnection.getResponseCode();

            urlConnection.disconnect();

            //se analiza si la respuesta fue correcta
            if(responseCode != HttpURLConnection.HTTP_OK)
            {
                return "ERROR AL ENVIAR LOS DATOS...";
            }
        } catch (Exception e)
        {
            return "ERROR AL ENVIAR LOS DATOS...";
        }
        return "datos para activar el buzzeer enviados...";
    }



    @Override
    protected String doInBackground(Void... voids) {


        respuesta=POST("http://dweet.io/dweet/for/ComeFido1",String.valueOf(4));//envio el dato 4
        publishProgress(respuesta);


        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate( values );

        this.t.setText( respuesta);

    }

    public void set(TextView v) {
        this.t = v;

    }
}

class hilo_obtener_historia extends AsyncTask<Void,String,Void>//leo el registro de consultas de dweet
{
    String datos=null;
    TextView tex=null;



    public String leerDatos(InputStream stream) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader( stream, "UTF-8" );
        char[] buffer = new char[1024];
        StringBuffer bufferDatos = new StringBuffer();
        int cantCaracteresLeidos;

        while ((cantCaracteresLeidos = reader.read( buffer )) != -1) {
            bufferDatos.append( buffer, 0, cantCaracteresLeidos );

        }
        return bufferDatos.toString();
    }

    public String url() throws IOException {

        InputStream is= null;

        try
        {
            URL url= new URL("https://dweet.io/get/dweets/for/ComeFido1");
            HttpsURLConnection conexion = (HttpsURLConnection) url.openConnection();
            conexion.setReadTimeout( 10000 );
            conexion.setConnectTimeout( 15000 );
            conexion.setRequestMethod( "GET" );
            conexion.setDoInput( true );
            conexion.connect();

            try {
                is = conexion.getInputStream();

                String contenidos = leerDatos( is );
                return contenidos;
            }catch (Exception e)
            {
                Log.d( "mi","error en los contenidos" );
            }
        }catch(IOException e)
        {
            Log.d( "mi","error en los contenidos" );
        }

        finally {
            if(is != null)
            {
                is.close();
            }
        }

        return null;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {

            datos= url();


        } catch (IOException e) {

            datos="ERROR AL CONECTARSE CON EL ALIMENTADOR...";
        }

        publishProgress(datos);


        return null;
    }

    protected void onProgressUpdate(String...values)
    {
        int i = 0,p=0;
        String fecha="";
        String dato="";
        String linea="";



        while(p<5)//datos.indexOf("created",i)!=-1)//busco solo la fecha de creacion y cantidad
        {

            fecha=(datos.substring(datos.indexOf("created",i)+10,datos.indexOf("created",i)+33));
            dato=(datos.substring(datos.indexOf("created",i)+64,datos.indexOf("created",i)+65));//datos.substring( datos.indexOf( ":\\\"",datos.indexOf("created",i)+35),4 ));//
            i+=datos.indexOf("created",i)+35-i;

            if(dato.equals( "1" ))
            {
                dato="poco";
            }
            else{
                if(dato.equals("2"))
                {
                    dato="medio";
                }
                else
                {
                    if(dato.equals("3"))
                    {
                        dato="mucho";
                    }
                    else
                        dato="buzzer";
                }
            }

            linea+="cantidad: " + dato + " " + "fecha: " +fecha + "\n";
            p++;


        }

        tex.setText(linea);




    }



    public void set(TextView v) {
        this.tex = v;
    }
}
