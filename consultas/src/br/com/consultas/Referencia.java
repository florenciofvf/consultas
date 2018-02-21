package br.com.consultas;

import java.util.ArrayList;
import java.util.List;

import br.com.consultas.util.Util;

public class Referencia {
	private static final String QUEBRA_LINHA = "\n";
	private final List<Referencia> referencias;
	private final boolean inverso;
	private final String aliasAlt;
	private final String preJoin;
	private final String alias;
	private final String pkNome;
	private final String fkNome;
	private Referencia pai;
	private final int pk;
	private final int fk;

	public Referencia(String alias, String aliasAlt, boolean inverso, int pk, String pkNome, int fk, String fkNome, String preJoin) {
		Util.checarVazio(alias, "alias.invalido", true);
		referencias = new ArrayList<>();
		this.aliasAlt = aliasAlt;
		this.inverso = inverso;
		this.preJoin = preJoin;
		this.pkNome = pkNome;
		this.fkNome = fkNome;
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
		return !Util.ehVazio(aliasAlt) ? aliasAlt : alias;
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

	public String getAliasAlt() {
		return aliasAlt;
	}

	public String gerarConsultaDados(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);
		StringBuilder sb = new StringBuilder("SELECT " + getAlias() + ".* FROM " + tab.getNome() + " " + getAlias() + QUEBRA_LINHA);

		sb.append(" WHERE 1=1" + QUEBRA_LINHA);

		for (Campo c : tab.getCampos()) {
			if (!Util.ehVazio(c.getValor())) {
				sb.append(" AND " + getAlias() + "." + c.getNome() + "=" + c.getValor() + QUEBRA_LINHA);
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
		StringBuilder sb = new StringBuilder("SELECT " + getAlias() + ".* FROM");

		if (pai != null) {
			completarConsulta(sb, tabelas);
		} else {
			sb.append(" " + tab.getNome() + " " + getAlias() + QUEBRA_LINHA);
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
				sb.append(" AND " + getAlias() + "." + c.getNome() + "=" + c.getValor() + QUEBRA_LINHA);
			}
		}
	}

	private void completarConsulta(StringBuilder sb, Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);

		if (pai == null) {
			sb.append(" " + tab.getNome() + " " + getAlias() + QUEBRA_LINHA);
			return;
		}

		pai.completarConsulta(sb, tabelas);
		sb.append(" " + getPreJoin() + " JOIN " + tab.getNome() + " " + getAlias());

		Campo campoFK = null;
		Campo campoPK = null;
		Tabela tabPai = tabelas.get(pai.alias);

		if (!inverso) {
			campoPK = Util.ehVazio(pkNome) ? tabPai.get(pk) : tabPai.get(pkNome);
			campoFK = Util.ehVazio(fkNome) ? tab.get(fk) : tab.get(fkNome);
			sb.append(" ON " + pai.getAlias() + "." + campoPK.getNome() + " = " + getAlias() + "." + campoFK.getNome()
					+ QUEBRA_LINHA);
		} else {
			campoPK = Util.ehVazio(pkNome) ? tab.get(pk) : tab.get(pkNome);
			campoFK = Util.ehVazio(fkNome) ? tabPai.get(fk) : tabPai.get(fkNome);
			sb.append(" ON " + getAlias() + "." + campoPK.getNome() + " = " + pai.getAlias() + "." + campoFK.getNome()
					+ QUEBRA_LINHA);
		}
	}

	@Override
	public String toString() {
		return alias;
	}
}