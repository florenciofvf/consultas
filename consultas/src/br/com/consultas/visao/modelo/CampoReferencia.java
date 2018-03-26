package br.com.consultas.visao.modelo;

import br.com.consultas.Referencia;
import br.com.consultas.util.Util;

public class CampoReferencia {
	public final boolean editavel;
	final String nome;

	public CampoReferencia(String nome, boolean editavel) {
		Util.checarVazio(nome, "alias.invalido", true);
		this.editavel = editavel;
		this.nome = nome;
	}

	public CampoReferencia(String nome) {
		this(nome, false);
	}

	Object getValor(Referencia referencia) {
		if ("alias".equals(nome)) {
			return referencia.getAlias2();

		} else if ("prefixo".equals(nome)) {
			return referencia.getPrefixo();

		} else if ("aliasAlt".equals(nome)) {
			return referencia.getAliasAlt();

		} else if ("inverso".equals(nome)) {
			return referencia.isInverso();

		} else if ("especial".equals(nome)) {
			return referencia.isEspecial();

		} else if ("cloneCompleto".equals(nome)) {
			return referencia.isCloneCompleto();

		} else if ("pk".equals(nome)) {
			return referencia.getPk();

		} else if ("pkNome".equals(nome)) {
			return referencia.getPkNome();

		} else if ("fk".equals(nome)) {
			return referencia.getFk();

		} else if ("fkNome".equals(nome)) {
			return referencia.getFkNome();

		} else if ("preJoin".equals(nome)) {
			return referencia.getPreJoin();

		} else if ("resumo".equals(nome)) {
			return referencia.getResumo();

		} else if ("campoID".equals(nome)) {
			return referencia.getCampoID();
		}

		return null;
	}

	void setValor(Referencia referencia, String valor) {
		if ("alias".equals(nome)) {

		} else if ("prefixo".equals(nome)) {

		} else if ("aliasAlt".equals(nome)) {
			referencia.setAliasAlt(valor);

		} else if ("inverso".equals(nome)) {

		} else if ("especial".equals(nome)) {

		} else if ("cloneCompleto".equals(nome)) {
			referencia.setCloneCompleto(Boolean.parseBoolean(valor));

		} else if ("pk".equals(nome)) {

		} else if ("pkNome".equals(nome)) {

		} else if ("fk".equals(nome)) {

		} else if ("fkNome".equals(nome)) {

		} else if ("preJoin".equals(nome)) {
			referencia.setPreJoin(valor);

		} else if ("resumo".equals(nome)) {
			referencia.setResumo(valor);

		} else if ("campoID".equals(nome)) {
			referencia.setCampoID(valor);
		}
	}

	int getInt(String s, int padrao) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return padrao;
		}
	}
}