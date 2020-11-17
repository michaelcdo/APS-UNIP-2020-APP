package br.unip.aps.eiaapp.DTO;

import java.util.List;

public class MensagemWatsonDTO {
	private String mensagemEntrada;
	private List<String> mensagemRetorno;
	private int codRetorno;

	public MensagemWatsonDTO(String mensagemEntrada){
		this.mensagemEntrada = mensagemEntrada;
	}

	public String  getMensagemEntrada() {
		return mensagemEntrada;
	}
	public void setMensagemEntrada(String mensagemEntrada) {
		this.mensagemEntrada = mensagemEntrada;
	}
	public List<String> getMensagemRetorno() {
		return mensagemRetorno;
	}
	public void setMensagemRetorno(List<String> mensagemRetorno) {
		this.mensagemRetorno = mensagemRetorno;
	}
	public int getCodRetorno() {
		return codRetorno;
	}
	public void setCodRetorno(int codRetorno) {
		this.codRetorno = codRetorno;
	}
}

