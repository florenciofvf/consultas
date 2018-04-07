package br.com.consultas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.consultas.util.Util;

public class Tabelas {
	private final Map<String, Tabela> tabelas;

	public Tabelas() {
		tabelas = new HashMap<>();
	}

	public void add(Tabela tabela) {
		if (tabela.getAlias() == null) {
			try {
				throw new Exception("ALIAS INEXISTENTE PARA A TABELA: " + tabela.getNome());
			} catch (Exception ex) {
				String msg = Util.getStackTrace("Tabelas.add()", ex);
				Util.mensagem(null, msg);
				System.exit(0);
			}
		}

		String alias = tabela.getAlias().getValor();

		Tabela tmp = tabelas.get(alias);

		if (tmp != null) {
			try {
				throw new Exception("ALIAS DUPLICADO: " + alias);
			} catch (Exception ex) {
				String msg = Util.getStackTrace("Tabelas.add()", ex);
				Util.mensagem(null, msg);
				System.exit(0);
			}
		}

		tabelas.put(alias, tabela);

		if (tabela.getIcone() == null) {
			tabela.add(new Campo("icone-f", "tabela"));
		}
	}

	public Tabela get(String alias) {
		Tabela tabela = tabelas.get(alias);

		if (tabela == null) {
			try {
				throw new Exception("TABELA NAO ENCONTRADA PARA O ALIAS: " + alias);
			} catch (Exception ex) {
				String msg = Util.getStackTrace("Tabelas.get()", ex);
				Util.mensagem(null, msg);
				System.exit(0);
			}
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