package com.example.app_mqtt_conexion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    MqttAndroidClient client;
    TextView mOxigeno, mPulsacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPulsacion = (TextView)findViewById(R.id.Pulsacion);
        mOxigeno = (TextView)findViewById(R.id.oxigeno);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://68.183.119.177",clientId);


        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"Conectado ",Toast.LENGTH_LONG).show();
                    setSubscription();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"conexion perdida !!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.100.31/sensor/insertar.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.isEmpty()){
                            Toast.makeText(MainActivity.this, "Dato guardado", Toast.LENGTH_LONG).show();
                        }

                        else{
                            Toast.makeText(MainActivity.this, "Dato no guardado", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @NonNull
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String,String>();

                        if (Objects.equals(topic, "ps/ldr")){
                            mPulsacion.setText(new String (message.getPayload()));  // Recibimos el mensaje desde el MQTT en un recurso de texto tipo Texview para ser mostrado por pantalla al usuario
                            params.put("pulsacion", mPulsacion.getText().toString());
                        }



                        if (Objects.equals(topic, "ps/ldr")){
                            mOxigeno.setText((new String(message.getPayload())));
                            params.put("oxigeno", mOxigeno.getText().toString());
                        }

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(stringRequest);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


    }//Termino del onCreate


    private void setSubscription(){

        try{

            //registrarUsuario();
            client.subscribe("ps/ldr",0);
            client.subscribe("ps/ldr",0);



        }catch (MqttException e){
            e.printStackTrace();
        }
    }


    /*
    private void registrarUsuario(){


    }

     */




    public void conn(View v){

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"CONECTADO AL SERVIDOR MQTT",Toast.LENGTH_LONG).show();
                    setSubscription();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"connection failed!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void disconn(View v){

        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"DESCONECTADO DEL SERVIDOR MQTT",Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"Could not diconnect!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}