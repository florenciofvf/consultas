package br.com.consultas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.consultas.util.Util;

public class Referencia {
	private static final String QUEBRA_LINHA = "\n";
	private final List<Referencia> referencias;
	private boolean exibirTotalRegistros;
	private final String prefixo;
	private int totalRegistros;
	private final String alias;
	private boolean especial;
	private boolean inverso;
	private String aliasAlt;
	private String preJoin;
	private String campoID;
	private Referencia pai;
	private String resumo;
	private String pkNome;
	private String fkNome;
	private int pk;
	private int fk;

	public Referencia(String alias, String aliasAlt, boolean inverso, int pk, String pkNome, int fk, String fkNome,
			String preJoin, String resumo) {
		Util.checarVazio(alias, "alias.invalido", true);
		prefixo = alias.substring(0, 1).toUpperCase();
		referencias = new ArrayList<>();
		this.aliasAlt = aliasAlt;
		this.inverso = inverso;
		this.preJoin = preJoin;
		this.resumo = resumo;
		this.pkNome = pkNome;
		this.fkNome = fkNome;
		this.alias = alias;
		this.pk = pk;
		this.fk = fk;
	}

	public Referencia clonar() {
		Referencia r = new Referencia(alias, aliasAlt, inverso, pk, pkNome, fk, fkNome, preJoin, resumo);
		r.especial = especial;
		return r;
	}

	public void especial(boolean b) {
		especial = b;
		for (Referencia r : referencias) {
			r.especial(b);
		}
	}

	public Referencia clonarCaminho() {
		Referencia clone = clonar();
		for (Referencia r : referencias) {
			clone.add(r.clonar());
		}

		Referencia pai = this.pai;

		while (pai != null) {
			Referencia clonePai = pai.clonar();
			clonePai.add(clone);

			clone = clonePai;
			pai = pai.pai;
		}

		return clone;
	}

	public static Referencia criarReferenciaDados(Tabela tabela) {
		return new Referencia(tabela.getAlias().getValor(), null, false, -1, null, -1, null, null, null);
	}

	public void setCampoID(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);
		Campo campo = tab.get(0);

		if (!Util.ehVazio(campo.getValor())) {
			campoID = campo.getValor();
		} else {
			campoID = null;
		}

		for (Referencia r : referencias) {
			r.setCampoID(tabelas);
		}
	}

	public void addFolha(List<Referencia> referencias) {
		if (getCount() == 0) {
			referencias.add(this);
		} else {
			for (Referencia r : getReferencias()) {
				r.addFolha(referencias);
			}
		}
	}

	public void caminho(List<Object> referencias) {
		Referencia pai = this.pai;
		while (pai != null) {
			referencias.add(0, pai);
			pai = pai.pai;
		}
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
		return Util.ehVazio(preJoin) ? "INNER" : preJoin.trim();
	}

	public void setPreJoin(String preJoin) {
		this.preJoin = preJoin;
	}

	public String getAlias() {
		return !Util.ehVazio(aliasAlt) ? aliasAlt : alias;
	}

	public String getAlias2() {
		return alias;
	}

	public Referencia getPai() {
		return pai;
	}

	public boolean isInverso() {
		return inverso;
	}

	public void setInverso(boolean inverso) {
		this.inverso = inverso;
	}

	public int getPk() {
		return pk;
	}

	public void setPk(int pk) {
		this.pk = pk;
	}

	public int getFk() {
		return fk;
	}

	public void setFk(int fk) {
		this.fk = fk;
	}

	public String getPkNome() {
		return pkNome;
	}

	public void setPkNome(String pkNome) {
		this.pkNome = pkNome;
	}

	public String getFkNome() {
		return fkNome;
	}

	public void setFkNome(String fkNome) {
		this.fkNome = fkNome;
	}

	public String getAliasAlt() {
		return aliasAlt;
	}

	public void setAliasAlt(String aliasAlt) {
		this.aliasAlt = aliasAlt;
	}

	public Tabela getTabela(Tabelas tabelas) {
		return tabelas.get(alias);
	}

	public boolean isDestaque(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);
		return tab.isDestaque();
	}

	public String getConsultaCount(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);
		return "SELECT COUNT(*) AS total FROM " + tab.getNome();
	}

	public String gerarDelete(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);

		StringBuilder sb = new StringBuilder("DELETE FROM " + tab.getNome() + QUEBRA_LINHA);
		sb.append(" WHERE " + Util.fragmentoFiltroCampo(tab.get(0)) + QUEBRA_LINHA);

		return sb.toString();
	}

	public String gerarUpdate(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);

		Iterator<Campo> it = tab.getCampos().iterator();
		if (it.hasNext()) {
			it.next();
		}

		StringBuilder set = new StringBuilder();
		boolean ativado = false;
		while (it.hasNext()) {
			Campo c = it.next();
			if (ativado) {
				set.append(", ");
			}
			set.append(Util.fragmentoFiltroCampo(c));
			ativado = true;
		}

		StringBuilder sb = new StringBuilder("UPDATE " + tab.getNome() + QUEBRA_LINHA);
		sb.append(" SET " + set.toString().trim() + QUEBRA_LINHA);
		sb.append(" WHERE " + Util.fragmentoFiltroCampo(tab.get(0)) + QUEBRA_LINHA);

		return sb.toString();
	}

	public String gerarConsultaDados(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);
		StringBuilder sb = new StringBuilder(
				"SELECT " + getAlias() + ".* FROM " + tab.getNome() + " " + getAlias() + QUEBRA_LINHA);

		sb.append(" WHERE 1=1" + QUEBRA_LINHA);

		for (Campo c : tab.getCampos()) {
			if (!Util.ehVazio(c.getValor())) {
				sb.append(" AND " + getAlias() + "." + Util.fragmentoFiltroCampo(c) + QUEBRA_LINHA);
			}
		}

		sb.append(" ORDER BY " + getAlias() + "." + tab.get(0).getNome() + aux(Util.getStringConfig("order_by"))
				+ QUEBRA_LINHA);
		sb.delete(sb.length() - QUEBRA_LINHA.length(), sb.length()).append(";").append(QUEBRA_LINHA);
		return sb.toString();
	}

	public String gerarConsulta(Tabelas tabelas) {
		if (inverso && pai == null) {
			throw new IllegalStateException(alias + ": INVERSO");
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
				sb.append(" AND " + getAlias() + "." + Util.fragmentoFiltroCampo(c) + QUEBRA_LINHA);
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

	public String getPrefixo() {
		return prefixo;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(prefixo + " - " + alias);

		if (exibirTotalRegistros) {
			sb.append(" (" + totalRegistros + ")");
		}

		if (!Util.ehVazio(campoID)) {
			sb.append(" [" + campoID + "]");
		}

		if (!Util.ehVazio(resumo)) {
			sb.append(" <<" + resumo + ">>");
		}

		return sb.toString();
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

	public boolean isEspecial() {
		return especial;
	}

	public void setEspecial(boolean especial) {
		this.especial = especial;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}
}