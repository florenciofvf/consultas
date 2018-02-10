package br.com.consultas;

import java.util.ArrayList;
import java.util.List;

import br.com.consultas.util.Util;

public class Tabela {
	private final List<Campo> campos;
	private final String nome;
	private Campo alias;

	public Tabela(String nome) {
		Util.checarVazio(nome, "nome.tabela.invalido", true);
		campos = new ArrayList<>();
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void add(Campo campo) {
		if (campo.isAlias()) {
			Util.checarVazio(campo.getValor(), "valor.campo.invalido", true, nome + "[" + campo.getNome() + "]");
			alias = campo;
		} else {
			campos.add(campo);
		}
	}

	public List<Campo> getCampos() {
		return campos;
	}

	public Campo getAlias() {
		return alias;
	}

	public void setAlias(Campo alias) {
		this.alias = alias;
	}

	public Campo get(int i) {
		return campos.get(i);
	}

	@Override
	public String toString() {
		return nome;
	}
}