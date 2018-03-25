package br.com.consultas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.consultas.util.Util;

public class Referencia {
	private final List<Referencia> referencias;
	private static final String QL = "\r\n";
	private boolean exibirTotalRegistros;
	private boolean cloneCompleto;
	private final boolean inverso;
	private final String prefixo;
	private final String pkNome;
	private final String fkNome;
	private int totalRegistros;
	private final String alias;
	private boolean especial;
	private String aliasAlt;
	private String preJoin;
	private String campoID;
	private Referencia pai;
	private String resumo;
	private final int pk;
	private final int fk;

	public Referencia(String alias, String aliasAlt, boolean inverso, int pk, String pkNome, int fk, String fkNome,
			String preJoin, String resumo, boolean cloneCompleto) {
		Util.checarVazio(alias, "alias.invalido", true);
		prefixo = alias.substring(0, 1).toUpperCase();
		this.cloneCompleto = cloneCompleto;
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
		Referencia r = new Referencia(alias, aliasAlt, inverso, pk, pkNome, fk, fkNome, preJoin, resumo, cloneCompleto);
		r.especial = especial;

		return r;
	}

	public Referencia clonarCompleto() {
		Referencia r = new Referencia(alias, aliasAlt, inverso, pk, pkNome, fk, fkNome, preJoin, resumo, cloneCompleto);
		r.especial = especial;

		for (Referencia ref : referencias) {
			r.add(ref.clonarCompleto());
		}

		return r;
	}

	public void especial(boolean b) {
		especial = b;

		for (Referencia r : referencias) {
			r.especial(b);
		}
	}

	public Referencia clonarCaminho() {
		Referencia clone = null;

		if (cloneCompleto) {
			clone = clonarCompleto();

		} else {
			clone = clonar();

			for (Referencia r : referencias) {
				clone.add(r.clonar());
			}
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
		return new Referencia(tabela.getAlias().getValor(), null, false, -1, null, -1, null, null, null, false);
	}

	public void setCampoID(String campoID) {
		if (!Util.ehVazio(campoID)) {
			this.campoID = campoID;
		} else {
			campoID = null;
		}
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

	public void validar(Tabelas tabelas) {
		tabelas.get(alias);

		gerarConsulta(tabelas, null);

		for (Referencia r : referencias) {
			r.validar(tabelas);
		}
	}

	public void atualizarTodaEstrutura(List<Referencia> referencias) {
		referencias.add(this);

		for (Referencia r : getReferencias()) {
			r.atualizarTodaEstrutura(referencias);
		}
	}

	public void atualizar(List<Referencia> referencias, Tabelas tabelas, Tabela tabela) {
		Tabela tab = tabelas.get(alias);

		if (tab.equals(tabela)) {
			referencias.add(this);
		}

		for (Referencia r : getReferencias()) {
			r.atualizar(referencias, tabelas, tabela);
		}
	}

	public void atualizar(List<Referencia> referencias, Tabelas tabelas, boolean comID) {
		Tabela tab = tabelas.get(alias);
		Campo campo = tab.get(0);

		if (comID && !Util.ehVazio(campo.getValor())) {
			referencias.add(this);
		} else {
			referencias.add(this);
		}

		for (Referencia r : getReferencias()) {
			r.atualizar(referencias, tabelas, comID);
		}
	}

	public void addFolha(List<Referencia> referencias) {
		if (getTotalFilhos() == 0) {
			referencias.add(this);
		} else {
			for (Referencia r : getReferencias()) {
				r.addFolha(referencias);
			}
		}
	}

	public void todos(List<Referencia> referencias) {
		for (Referencia r : getReferencias()) {
			r.todos(referencias);
		}

		referencias.add(this);
	}

	public void caminho(List<Object> referencias) {
		Referencia pai = this.pai;

		while (pai != null) {
			referencias.add(0, pai);
			pai = pai.pai;
		}
	}

	public List<String> getCaminho() {
		List<String> lista = new ArrayList<>();

		Referencia pai = this.pai;

		while (pai != null) {
			lista.add(0, pai.getAlias());
			pai = pai.pai;
		}

		lista.add(getAlias());

		return lista;
	}

	public Object[] getCaminhoArray() {
		return getCaminho().toArray();
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

	public int getTotalFilhos() {
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

	public void setCloneCompleto(boolean cloneCompleto) {
		this.cloneCompleto = cloneCompleto;
	}

	public boolean isCloneCompleto() {
		return cloneCompleto;
	}

	public int getPk() {
		return pk;
	}

	public int getFk() {
		return fk;
	}

	public String getPkNome() {
		return pkNome;
	}

	public String getFkNome() {
		return fkNome;
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

		StringBuilder sb = new StringBuilder("DELETE FROM " + tab.getNome() + QL);
		sb.append(" WHERE " + Util.fragmentoFiltroCampo(tab.get(0)) + QL);

		return sb.toString();
	}

	public String gerarUpdate(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);

		Iterator<Campo> it = tab.getCampos().iterator();
		if (it.hasNext()) {
			it.next();
		}

		StringBuilder set = new StringBuilder();

		while (it.hasNext()) {
			Campo c = it.next();

			if (set.length() > 0) {
				set.append(", ");
			}

			set.append(Util.fragmentoFiltroCampo(c));
		}

		StringBuilder sb = new StringBuilder("UPDATE " + tab.getNome() + QL);
		sb.append(" SET " + set.toString().trim() + QL);
		sb.append(" WHERE " + Util.fragmentoFiltroCampo(tab.get(0)) + QL);

		return sb.toString();
	}

	public String gerarConsultaDados(Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);

		StringBuilder sb = new StringBuilder(
				"SELECT " + getAlias() + ".* FROM " + tab.getNome() + " " + getAlias() + QL);

		sb.append(" WHERE 1=1" + QL);

		for (Campo c : tab.getCampos()) {
			if (!Util.ehVazio(c.getValor())) {
				sb.append(" AND " + getAlias() + "." + Util.fragmentoFiltroCampo(c) + QL);
			}
		}

		sb.append(" ORDER BY " + getAlias() + "." + tab.get(0).getNome() + aux(Util.getStringConfig("order_by")) + QL);
		sb.delete(sb.length() - QL.length(), sb.length()).append(";").append(QL);

		return sb.toString();
	}

	public String gerarConsulta(Tabelas tabelas, String aliasTemp) {
		if (inverso && pai == null) {
			try {
				throw new Exception("ALIAS INVERSO INVALIDO: " + alias);
			} catch (Exception ex) {
				String msg = Util.getStackTrace("Referencia.gerarConsulta()", ex);
				Util.mensagem(null, msg);
				System.exit(0);
			}
		}

		Tabela tab = tabelas.get(alias);

		StringBuilder sb = new StringBuilder(
				"SELECT " + (Util.ehVazio(aliasTemp) ? getAlias() : aliasTemp) + ".* FROM");
		completarConsulta(sb, tabelas);

		sb.append(" WHERE 1=1" + QL);
		filtros(sb, tabelas);

		sb.append(" ORDER BY " + getAlias() + "." + tab.get(0).getNome() + aux(Util.getStringConfig("order_by")) + QL);
		sb.delete(sb.length() - QL.length(), sb.length()).append(";").append(QL);

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
				sb.append(" AND " + getAlias() + "." + Util.fragmentoFiltroCampo(c) + QL);
			}
		}
	}

	private void completarCampos(StringBuilder sb, Tabelas tabelas) {
		if (pai != null) {
			pai.completarCampos(sb, tabelas);
		}

		Tabela tab = tabelas.get(alias);
		List<Campo> campos = tab.getCamposSelecionados();

		for (Campo c : campos) {
			if (sb.length() > 0) {
				sb.append(",");
			}

			sb.append(" " + getAlias() + "." + c.getNome());
		}
	}

	private void completarConsulta(StringBuilder sb, Tabelas tabelas) {
		Tabela tab = tabelas.get(alias);

		if (pai == null) {
			sb.append(" " + tab.getNome() + " " + getAlias() + QL);
			return;
		}

		pai.completarConsulta(sb, tabelas);
		sb.append(" " + getPreJoin() + " JOIN " + tab.getNome() + " " + getAlias());

		Campo campoPK = null;
		Campo campoFK = null;
		Tabela tabPai = tabelas.get(pai.alias);

		if (!inverso) {
			campoPK = Util.ehVazio(pkNome) ? tabPai.get(pk) : tabPai.get(pkNome);
			campoFK = Util.ehVazio(fkNome) ? tab.get(fk) : tab.get(fkNome);

			sb.append(" ON " + pai.getAlias() + "." + campoPK.getNome() + " = " + getAlias() + "." + campoFK.getNome()
					+ QL);
		} else {
			campoPK = Util.ehVazio(pkNome) ? tab.get(pk) : tab.get(pkNome);
			campoFK = Util.ehVazio(fkNome) ? tabPai.get(fk) : tabPai.get(fkNome);

			sb.append(" ON " + getAlias() + "." + campoPK.getNome() + " = " + pai.getAlias() + "." + campoFK.getNome()
					+ QL);
		}
	}

	public String getConsultaGroupByCount(Tabelas tabelas) {
		Tabela tabPai = tabelas.get(pai.alias);
		Tabela tab = tabelas.get(alias);

		Campo campoPK = Util.ehVazio(pkNome) ? tabPai.get(pk) : tabPai.get(pkNome);
		Campo campoFK = Util.ehVazio(fkNome) ? tab.get(fk) : tab.get(fkNome);

		StringBuilder sb = new StringBuilder("SELECT " + pai.getAlias() + "." + campoPK.getNome() + ", COUNT("
				+ getAlias() + "." + campoFK.getNome() + ") AS total FROM");
		completarConsulta(sb, tabelas);

		sb.append(" WHERE 1=1" + QL);
		filtros(sb, tabelas);

		sb.append(" GROUP BY " + pai.getAlias() + "." + campoPK.getNome() + QL);
		sb.append(" ORDER BY " + pai.getAlias() + "." + campoPK.getNome() + aux(Util.getStringConfig("order_by")) + QL);

		return sb.toString();
	}

	public String getConsultaAgregada(Tabelas tabelas, Campo campo) {
		Tabela tabPai = tabelas.get(pai.alias);

		Campo campoPK = Util.ehVazio(pkNome) ? tabPai.get(pk) : tabPai.get(pkNome);

		StringBuilder sb = new StringBuilder("SELECT " + pai.getAlias() + "." + campoPK.getNome() + ", " + getAlias()
				+ "." + campo.getNome() + " FROM");
		completarConsulta(sb, tabelas);

		sb.append(" WHERE 1=1" + QL);
		filtros(sb, tabelas);

		sb.append(" ORDER BY " + pai.getAlias() + "." + campoPK.getNome() + aux(Util.getStringConfig("order_by")) + QL);

		return sb.toString();
	}

	public String getConsultaSelecionados(Tabelas tabelas) {
		StringBuilder sbCampos = new StringBuilder();
		completarCampos(sbCampos, tabelas);

		if (sbCampos.length() == 0) {
			return "NENHUM CAMPO SELECIONADO!";
		}

		StringBuilder sb = new StringBuilder("SELECT " + sbCampos.toString() + " FROM");
		completarConsulta(sb, tabelas);

		sb.append(" WHERE 1=1" + QL);
		filtros(sb, tabelas);

		sb.delete(sb.length() - QL.length(), sb.length()).append(";").append(QL);

		return sb.toString();
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

	public String getCampoID() {
		return campoID;
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

	public void ordenar() {
		Util.ordenar(referencias);
	}
}