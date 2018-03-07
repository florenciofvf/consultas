package br.com.consultas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tabelas {
	private final Map<String, Tabela> tabelas;

	public Tabelas() {
		tabelas = new HashMap<>();
	}

	public void add(Tabela tabela) {
		String alias = tabela.getAlias().getValor();

		Tabela tmp = tabelas.get(alias);
		if (tmp != null) {
			throw new IllegalArgumentException("ALIAS EXISTENTE! " + alias);
		}

		tabelas.put(alias, tabela);
	}

	public Tabela get(String alias) {
		Tabela tabela = tabelas.get(alias);

		if (tabela == null) {
			throw new IllegalArgumentException(alias);
		}

		return tabela;
	}

	public List<Tabela> getTabelas() {
		return new ArrayList<>(tabelas.values());
	}

	public int getTotalTabelas() {
		return tabelas.size();
	}

	@Override
	public String toString() {
		return tabelas.toString();
	}
}