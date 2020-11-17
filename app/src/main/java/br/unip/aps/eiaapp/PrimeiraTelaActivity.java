package br.unip.aps.eiaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

public class PrimeiraTelaActivity extends AppCompatActivity {
    public boolean enviouPage = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primeira_tela);

        if( getIntent().getBooleanExtra("Exit me", false)){
            finish();
            return;
        }


    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        try {
            TimeUnit.SECONDS.sleep(2);
            if(!enviouPage){
                Intent intent = new Intent(this, ChatActivity.class);
                startActivity(intent);
                enviouPage = true;
            }
        }catch (Exception e){

        }
    }
}