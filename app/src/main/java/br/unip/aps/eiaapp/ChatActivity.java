package br.unip.aps.eiaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.GroupieViewHolder;

import java.util.ArrayList;

import br.unip.aps.eiaapp.DTO.MensagemWatsonDTO;
import br.unip.aps.eiaapp.DTO.MessageDTO;
import br.unip.aps.eiaapp.util.CallAPI;
import br.unip.aps.eiaapp.util.Constants;
import br.unip.aps.eiaapp.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    private Toast toast;
    private long lastBackPressTime = 0;
    private EditText inputMensagem;
    private GroupAdapter adapter = new GroupAdapter();
    private CallAPI callApi;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rv = findViewById(R.id.recycler_chat);

        inputMensagem = findViewById(R.id.input_mensagem);
        Button btnChat = findViewById(R.id.btnEnviarChat);

        inputMensagem.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    Util.hideKeyboard(ChatActivity.this);
                    sendMessage();

                    return true;
                }
                return false;
            }
        });

        btnChat.setOnClickListener(view -> {
            sendMessage();
            Util.hideKeyboard(ChatActivity.this);
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        carregaChat();
        moveToBottom();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        callApi = retrofit.create(CallAPI.class);

    }

    public void carregaChat(){
        ArrayList<MessageDTO> messages = Util.carregaMensagens(ChatActivity.this);
        for (MessageDTO msg: messages) {
            adapter.add(new ChatActivity.MessageItem(msg));
        }
    }
    private void moveToBottom(){
        if(adapter.getGroupCount()>0){
            rv.scrollToPosition(adapter.getGroupCount() - 1);
        }
    }
    private void sendMessage(){
        String text = inputMensagem.getText().toString();
        if(text.trim().length()>0) {
            MessageDTO messageEntrada = new MessageDTO();
            messageEntrada.setWatson(false);
            messageEntrada.setText(text);
            messageEntrada.setTimestamp(Util.getTimeStamp());
            messageEntrada.setData(Util.getData());

            adapter.add(new MessageItem(messageEntrada));
            Util.salvaHistorico(messageEntrada, ChatActivity.this);

            inputMensagem.setText(null);

            MensagemWatsonDTO msg = new MensagemWatsonDTO(text);

            enviaMensagem(msg);
            moveToBottom();
        }
    }

    private class MessageItem extends Item<GroupieViewHolder>{
        private final MessageDTO message;

        private MessageItem(MessageDTO message){
            this.message = message;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            TextView txtMsg = viewHolder.itemView.findViewById(R.id.txt_message);
            TextView txtTime = viewHolder.itemView.findViewById(R.id.txt_time);

            txtMsg.setText(message.getText());
            txtTime.setText(message.getTimestamp());
        }

        @Override
        public int getLayout() {
            return message.isWatson() ? R.layout.item_from_layout : R.layout.item_to_layout;
        }
    }

    private void adicionaMensagemWatson(MensagemWatsonDTO mensagemWatsonDTO){
        if(mensagemWatsonDTO.getMensagemRetorno() != null && mensagemWatsonDTO.getMensagemRetorno().size()>0) {
            for (String msg: mensagemWatsonDTO.getMensagemRetorno()) {
                MessageDTO messageRetorno = new MessageDTO();
                messageRetorno.setWatson(true);
                messageRetorno.setText(msg);
                messageRetorno.setTimestamp(Util.getTimeStamp());
                messageRetorno.setData(Util.getData());
                adapter.add(new MessageItem(messageRetorno));
                moveToBottom();
                Util.salvaHistorico(messageRetorno, ChatActivity.this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast = Toast.makeText(this, "Pressione o Botão Voltar novamente para fechar o Aplicativo.", Toast.LENGTH_SHORT);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            Intent intent = new Intent(this, PrimeiraTelaActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Exit me", true);
            startActivity(intent);
            finish();
        }
        return;
    }

    private void enviaMensagem(MensagemWatsonDTO mensagem){
        Call<MensagemWatsonDTO> call = callApi.enviaMensagem(mensagem);

        call.enqueue(new Callback<MensagemWatsonDTO>() {
            @Override
            public void onResponse(Call<MensagemWatsonDTO> call, Response<MensagemWatsonDTO> response) {
                if(!response.isSuccessful()){
                    adicionaMensagemErro();
                    return;
                }
                MensagemWatsonDTO mensagemResposta = response.body();
                adicionaMensagemWatson(mensagemResposta);
            }

            @Override
            public void onFailure(Call<MensagemWatsonDTO> call, Throwable t) {
                adicionaMensagemErro();
                return;
            }
        });
    }
    private void adicionaMensagemErro(){
        MensagemWatsonDTO mensagemResposta = new MensagemWatsonDTO("");
        ArrayList<String> msgRet = new ArrayList<>();
        msgRet.add("Desculpe!");
        mensagemResposta.setMensagemRetorno(msgRet);
        adicionaMensagemWatson(mensagemResposta);
    }
}