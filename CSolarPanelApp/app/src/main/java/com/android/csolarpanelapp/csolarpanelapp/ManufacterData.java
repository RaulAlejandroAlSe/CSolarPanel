package com.android.csolarpanelapp.csolarpanelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ManufacterData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manufacter_data);
    }
    public void sendData(View viwe){
        Toast.makeText(this,"Espere un momento",Toast.LENGTH_LONG).show();
        String mensaje;
        //Requerimientos de Energía.
        double Q = 1000; // en watts
        double H = 6; //Horas promedio anual de luz diurna
        double GAH[] = {6.09, 6.84, 7.36, 6.98, 6.37, 6.25, 5.94, 5.64, 5.55, 5.94, 6.38, 5.72};
        double Ep = 0.75; //Eficiencia del panel solar
        double CW = 4.46; //Costo de producción de 1 watt incluyendo materiales e instalación.
        /////////////////////////////////////////7
        // Datos del fabricante
        double Iscn = 8.21; //Corriente nominal de cortocircuito [A]
        double Vocn = 32.9; //Tensión nominal de circuito abierto [V]
        double Imp = 7.61; //Corriente del punto de máxima potencia [A]
        double Vmp = 26.3; //Tensión del punto de máxima potencia [V]
        double Pmax_e = Vmp * Imp; //Potencia máxima de salida [W]
        double kv = -0.1230;
        double Kv = -0.1230 * Vmp / 100; //Coeficiente de temperatura/Tensión [V/K]
        double ki = 0.0032;
        double Ki = 0.0032 * Imp / 100; //Coeficiente de temperatura/Corriente [A/K]
        double Ns = 54; //Nº de células en serie
        // Constantes
        double k = 1.3806503e-23; //Boltzmann (J/K)
        double q = 1.60217646e-19; //Carga del electrón [C]
        double a = 1.3; //Constante del diodo
        //Valores nominales
        double Gn = 1000; //Irradiancia nominal [W/m^2] a 25ºC
        double Tn = 25 + 273.15; //Temperatura de operación nominal [K
        //El modelo se ajusta a las condiciones nominales
        double T = 25 + 273.15;
        double G = 1000;
        double Vtn = k * Tn / q; //Tensión térmica nominal
        double Ion = Iscn / (Math.exp(Vocn / a / Ns / Vtn) - 1); //Corriente de saturaci�n nominal del diodo
        //Valores de referencia de Rs y Rp
        double Rs_max = (Vocn - Vmp) / Imp;
        double Rp_min = Vmp / (Iscn - Imp) - Rs_max;
        //Condiciones iniciales de Rp y Rs
        double Rp = Rp_min;
        double Rs = 0;
        double tol = 0.001; //Tolerancia para el error de la potencia calculada
        double error = 1000000000; //Valor por defecto
        // Proceso iterativo para Rs y Rp hasta que la Pmax del modelo=Pmax experimental
        int s = 0;
        double Ipvn=0;
        double[] V = new double[1000];
        //I=zeros(1,size(V,2));
        double[] P = new double[1000];
        double Pmax_m;
        double[] I = new double[1000];
        double[] I_ = new double[1000];
        double[] g = new double[1000];
        double[] glin = new double[1000];
        while (error > tol){
            s = s + 1;
            Ipvn = (Rs + Rp) / Rp * Iscn; //Corriente fotogenerada nominal
            //Incremento de Rs
            Rs = Rs + .01;
            Rp = Vmp * (Vmp + Imp * Rs) / (Vmp * Ipvn - Vmp * Ion * Math.exp((Vmp + Imp * Rs) / Vtn / Ns / a) + Vmp * Ion - Pmax_e);
            //Resoluci�n
            //clear I
            //clear V
            //V=0:Vocn/1000:Vocn; %se cambio 50 por Vocn.
            for (int i = 0; i < 1000; i++) {
                V[i] = i * Vocn / 1000;
                I[i] = 0; //zeros
            }
            for (int j = 0; j < 1000; j++ ){
                g[j] = Ipvn - Ion * (Math.exp((V[j] + I[j] * Rs) / Vtn / Ns / a) - 1) - (V[j] + I[j] * Rs) / Rp - I[j];
                while (Math.abs(g[j]) > 0.001) {
                    g[j] = Ipvn - Ion * (Math.exp((V[j] + I[j] * Rs) / Vtn / Ns / a) - 1) - (V[j] + I[j] * Rs) / Rp - I[j];
                    glin[j] = -Ion * Rs / Vtn / Ns / a * Math.exp((V[j] + I[j] * Rs) / Vtn / Ns / a) - Rs / Rp - 1;
                    I_[j] = I[j] - g[j] / glin[j];
                    I[j] = I_[j];
                }
            }
            //Calcular la potencia usando la ecuaci�n I-V
            for(int i=0;i<1000;i++) {
                P[i] = (Ipvn - Ion * (Math.exp((V[i] + I[i]*Rs) / Vtn / Ns / a) - 1) - (V[i] + I[i]* Rs) / Rp)*V[i];
            }
            Pmax_m=0;
            for(int i=0; i<1000; i++) {
                if (P[i] > Pmax_m)
                    Pmax_m = P[i];
            }
            //%Pmax_2=max(V.*I);% funcion alterna
            error=Math.abs(Pmax_m-Pmax_e);
        }
        mensaje = "Informacion del modelo: \n";
        mensaje = mensaje + "Rp_min = " + Double.toString(Rp_min)+"\n";
        mensaje = mensaje + "Rp= " + Double.toString(Rp)+"\n";
        mensaje = mensaje + "Rs_max = " + Double.toString(Rs_max)+"\n";
        mensaje = mensaje + "Rs = " + Double.toString(Rs)+"\n";
        mensaje = mensaje + "a = " + Double.toString(a)+"\n";
        mensaje = mensaje + "T = " + Double.toString(T-273.15)+"\n";
        mensaje = mensaje + "G = " + Double.toString(G)+"\n";
        mensaje = mensaje + "P_max = " + Double.toString(Pmax_e)+"\n";
        mensaje = mensaje + "tol = " + Double.toString(tol)+"\n";
        mensaje = mensaje + "P_error = " + Double.toString(error)+"\n";
        mensaje = mensaje + "Ipv = " + Double.toString(Ipvn)+"\n";
        mensaje = mensaje + "Isc = " + Double.toString(Iscn)+"\n";
        mensaje = mensaje + "Ion = " + Double.toString(Ion)+"\n";
        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();
        finish();
    }
}
