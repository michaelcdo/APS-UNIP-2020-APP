package br.unip.aps.eiaapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.unip.aps.eiaapp.DTO.MessageDTO;

public class Util {

    public static String getTimeStamp(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        String hr = "";
        String mn = "";

        hr = calendar.get(Calendar.HOUR_OF_DAY) + "";
        mn = calendar.get(Calendar.MINUTE) + "";
        if(hr.length()==1)
            hr = "0" + hr;

        if(mn.length()==1)
            mn = "0" + mn;

        return hr + ":" + mn;

    }
    public static String getData(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        String dia = "";
        String mes = "";
        String ano = "";

        dia = calendar.get(Calendar.DAY_OF_MONTH) + "";
        mes = calendar.get(Calendar.MONTH) + "";
        ano = calendar.get(Calendar.YEAR) + "";
        if(dia.length()==1)
            dia = "0" + dia;

        if(mes.length()==1)
            mes = "0" + mes;

        return dia + "/" + mes+ "/" + ano;

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void salvaHistorico(MessageDTO message, Context ctx){
        String messageString = message.getText()+";"+message.getTimestamp()+";"+message.isWatson()+";"+message.getData();

        String historico = getHistoricoString(ctx);
        historico+=messageString + "¬";

        setHistoricoString(ctx,historico);

    }
    public static ArrayList<MessageDTO> carregaMensagens(Context ctx){
        String historico = getHistoricoString(ctx);
        ArrayList<MessageDTO> messages = new ArrayList<>();
        for (String msg: historico.split("¬")){
            MessageDTO message = fromString(msg);
            if(message!=null){
                messages.add(message);
            }
        }
        return messages;
    }
    private static MessageDTO fromString(String msg){
        MessageDTO message = new MessageDTO();

        if(msg.length()>0){
            try {
                message.setText(msg.split(";")[0]);
                message.setTimestamp(msg.split(";")[1]);
                message.setWatson(Boolean.parseBoolean(msg.split(";")[2]));
                message.setData(msg.split(";")[3]);
                return message;
            }catch (Exception e){
                return null;
            }
        }

        return null;
    }
    private static String getHistoricoString(Context ctx){
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return myPreferences.getString("historicoChat","");
    }

    public static void setHistoricoString(Context ctx, String historico){
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putString("historicoChat", historico);
        myEditor.commit();
    }

}

