package br.com.consultas;

import br.com.consultas.util.Util;

public class Campo {
	private final String nome;
	private String valor;

	public Campo(String nome, String valor) {
		Util.checarVazio(nome, "nome.campo.vazio", true);
		Util.checarVazio(valor, "valor.campo.invalido", false, nome);
		this.nome = nome.trim();
		this.valor = valor;
	}

	public boolean isAlias() {
		return "alias-f".equals(nome.toLowerCase());
	}

	public String getNome() {
		return nome;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Override
	public String toString() {
		return nome + "=" + valor;
	}
}