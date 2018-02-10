package br.com.consultas;

import java.util.HashMap;
import java.util.Map;

public class Tabelas {
	private final Map<String, Tabela> tabelas;

	public Tabelas() {
		tabelas = new HashMap<>();
	}

	public void add(Tabela tabela) {
		tabelas.put(tabela.getAlias().getValor(), tabela);
	}

	public Tabela get(String alias) {
		Tabela tabela = tabelas.get(alias);

		if (tabela == null) {
			throw new IllegalArgumentException(alias);
		}

		return tabela;
	}

	@Override
	public String toString() {
		return tabelas.toString();
	}
}