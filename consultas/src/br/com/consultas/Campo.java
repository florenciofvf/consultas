package br.com.consultas;

import br.com.consultas.util.Util;

public class Campo {
	private boolean somenteLeitura;
	private boolean selecionado;
	private final String nome;
	private String valor;

	public Campo(String nome, String valor) {
		Util.checarVazio(nome, "nome.campo.vazio", true);
		Util.checarVazio(valor, "valor.campo.invalido", false, nome);
		this.nome = nome.trim();
		this.valor = valor;
	}

	public boolean isDestaque() {
		return "destaque-f".equals(nome.toLowerCase());
	}

	public boolean isAlias() {
		return "alias-f".equals(nome.toLowerCase());
	}

	public boolean isIcone() {
		return "icone-f".equals(nome.toLowerCase());
	}

	public String getNome() {
		return nome;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		if (somenteLeitura) {
			return;
		}

		this.valor = valor;
	}

	public boolean isSomenteLeitura() {
		return somenteLeitura;
	}

	public void setSomenteLeitura(boolean somenteLeitura) {
		this.somenteLeitura = somenteLeitura;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public String toString() {
		return nome + "=" + valor;
	}
}