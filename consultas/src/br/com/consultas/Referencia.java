package br.com.consultas;

import java.util.ArrayList;
import java.util.List;

import br.com.consultas.util.Util;

public class Referencia {
	private static final String QUEBRA_LINHA = "\n";
	private final List<Referencia> referencias;
	private boolean exibirTotalRegistros;
	private final boolean inverso;
	private final String aliasAlt;
	private final String preJoin;
	private final String prefixo;
	private final String pkNome;
	private final String fkNome;
	private int totalRegistros;
	private final String alias;
	private Referencia pai;
	private final int pk;
	private final int fk;

	public Referencia(String alias, String aliasAlt, boolean inverso, int pk, String pkNome, int fk, String fkNome,
			String preJoin) {
		Util.checarVazio(alias, "alias.invalido", true);
		prefixo = alias.substring(0, 1).toUpperCase();
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

	public static Referencia criarReferenciaDados(Tabela tabela) {
		return new Referencia(tabela.getAlias().getValor(), null, false, -1, null, -1, null, null);
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

	public Tabela getTabela(Tabelas tabelas) {
		return tabelas.get(alias);
	}

	public String getConsultaCount(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);
		return "SELECT COUNT(*) AS total FROM " + tab.getNome();
	}

	public String gerarConsultaDados(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);
		StringBuilder sb = new StringBuilder(
				"SELECT " + getAlias() + ".* FROM " + tab.getNome() + " " + getAlias() + QUEBRA_LINHA);

		sb.append(" WHERE 1=1" + QUEBRA_LINHA);

		for (Campo c : tab.getCampos()) {
			if (!Util.ehVazio(c.getValor())) {
				sb.append(" AND " + getAlias() + "." + c.getNome() + "=" + c.getValor() + QUEBRA_LINHA);
			}
		}

		sb.append(" ORDER BY " + getAlias() + "." + tab.get(0).getNome() + aux(Util.getStringConfig("order_by"))
				+ QUEBRA_LINHA);
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

		sb.append(" ORDER BY " + getAlias() + "." + tab.get(0).getNome() + aux(Util.getStringConfig("order_by"))
				+ QUEBRA_LINHA);
		sb.delete(sb.length() - QUEBRA_LINHA.length(), sb.length()).append(";").append(QUEBRA_LINHA);
		return sb.toString();
	}

	private String aux(String s) {
		return Util.ehVazio(s) ? "" : " " + s;
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
		return prefixo + " - " + alias + (exibirTotalRegistros ? " (" + totalRegistros + ")" : "");
	}

	public boolean isExibirTotalRegistros() {
		return exibirTotalRegistros;
	}

	public void setExibirTotalRegistros(boolean exibirTotalRegistros) {
		this.exibirTotalRegistros = exibirTotalRegistros;
	}

	public int getTotalRegistros() {
		return totalRegistros;
	}

	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}
}