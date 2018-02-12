package br.com.consultas;

import java.util.ArrayList;
import java.util.List;

import br.com.consultas.util.Util;

public class Referencia {
	private static final String QUEBRA_LINHA = "\n";
	private final List<Referencia> referencias;
	private final boolean inverso;
	private final String preJoin;
	private final String alias;
	private Referencia pai;
	private final int pk;
	private final int fk;

	public Referencia(String alias, boolean inverso, int pk, int fk, String preJoin) {
		Util.checarVazio(alias, "alias.invalido", true);
		referencias = new ArrayList<>();
		this.inverso = inverso;
		this.preJoin = preJoin;
		this.alias = alias;
		this.pk = pk;
		this.fk = fk;
	}

	public List<Referencia> getReferencias() {
		return referencias;
	}

	public void add(Referencia ref) {
		ref.pai = this;
		referencias.add(ref);
	}

	public Referencia get(int i) {
		return referencias.get(i);
	}

	public int getCount() {
		return referencias.size();
	}

	public String getPreJoin() {
		return preJoin == null ? "INNER" : preJoin;
	}

	public String getAlias() {
		return alias;
	}

	public Referencia getPai() {
		return pai;
	}

	public boolean isInverso() {
		return inverso;
	}

	public int getPk() {
		return pk;
	}

	public int getFk() {
		return fk;
	}

	public String gerarConsultaDados(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);
		StringBuilder sb = new StringBuilder("SELECT " + alias + ".* FROM " + tab.getNome() + " " + alias + QUEBRA_LINHA);

		sb.append(" WHERE 1=1" + QUEBRA_LINHA);

		for (Campo c : tab.getCampos()) {
			if (!Util.ehVazio(c.getValor())) {
				sb.append(" AND " + alias + "." + c.getNome() + "=" + c.getValor() + QUEBRA_LINHA);
			}
		}

		sb.delete(sb.length() - QUEBRA_LINHA.length(), sb.length()).append(";").append(QUEBRA_LINHA);
		return sb.toString();
	}

	public String gerarConsulta(Tabelas tabelas) {
		if (inverso && pai == null) {
			throw new IllegalStateException(alias + " INVERSO");
		}

		Tabela tab = tabelas.get(alias);
		StringBuilder sb = new StringBuilder("SELECT " + alias + ".* FROM");

		if (pai != null) {
			completarConsulta(sb, tabelas);
		} else {
			sb.append(" " + tab.getNome() + " " + alias + QUEBRA_LINHA);
		}

		sb.append(" WHERE 1=1" + QUEBRA_LINHA);

		filtros(sb, tabelas);

		sb.delete(sb.length() - QUEBRA_LINHA.length(), sb.length()).append(";").append(QUEBRA_LINHA);
		return sb.toString();
	}

	private void filtros(StringBuilder sb, Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);

		if (pai != null) {
			pai.filtros(sb, tabelas);
		}

		for (Campo c : tab.getCampos()) {
			if (!Util.ehVazio(c.getValor())) {
				sb.append(" AND " + alias + "." + c.getNome() + "=" + c.getValor() + QUEBRA_LINHA);
			}
		}
	}

	private void completarConsulta(StringBuilder sb, Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);

		if (pai == null) {
			sb.append(" " + tab.getNome() + " " + alias + QUEBRA_LINHA);
			return;
		}

		pai.completarConsulta(sb, tabelas);
		sb.append(" " + getPreJoin() + " JOIN " + tab.getNome() + " " + alias);

		Campo campoFK = null;
		Campo campoPK = null;
		Tabela tabPai = tabelas.get(pai.alias);

		if (!inverso) {
			campoPK = tabPai.get(pk);
			campoFK = tab.get(fk);
			sb.append(" ON " + pai.alias + "." + campoPK.getNome() + " = " + alias + "." + campoFK.getNome()
					+ QUEBRA_LINHA);
		} else {
			campoPK = tab.get(pk);
			campoFK = tabPai.get(fk);
			sb.append(" ON " + alias + "." + campoPK.getNome() + " = " + pai.alias + "." + campoFK.getNome()
					+ QUEBRA_LINHA);
		}
	}

	@Override
	public String toString() {
		return alias;
	}
}