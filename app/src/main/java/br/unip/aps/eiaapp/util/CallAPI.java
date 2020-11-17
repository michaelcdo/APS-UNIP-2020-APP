package br.unip.aps.eiaapp.util;

import br.unip.aps.eiaapp.DTO.MensagemWatsonDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CallAPI {
    @POST("watson")
    Call<MensagemWatsonDTO> enviaMensagem(@Body MensagemWatsonDTO mensagemWatsonDTO);
}
