package br.com.consultas.visao;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.consultas.Referencia;
import br.com.consultas.util.Util;

public class ModeloReferencia implements TableModel {
	public static final String[] COLUNAS = { "NOME", "VALOR" };
	private final List<CampoReferencia> campos;
	private final Referencia referencia;

	public ModeloReferencia(Referencia referencia) {
		campos = new ArrayList<CampoReferencia>();
		this.referencia = referencia;

		if (referencia != null) {
			campos.add(new CampoReferencia("alias"));
			campos.add(new CampoReferencia("aliasAlt", true));
			campos.add(new CampoReferencia("prefixo"));
			campos.add(new CampoReferencia("inverso"));
			campos.add(new CampoReferencia("pk"));
			campos.add(new CampoReferencia("fk"));
			campos.add(new CampoReferencia("pkNome"));
			campos.add(new CampoReferencia("fkNome"));
			campos.add(new CampoReferencia("preJoin", true));
		}
	}

	public CampoReferencia getCampoReferencia(int indice) {
		return campos.get(indice);
	}

	@Override
	public int getRowCount() {
		return campos.size();
	}

	@Override
	public int getColumnCount() {
		return COLUNAS.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUNAS[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return false;
		}
		CampoReferencia campo = campos.get(rowIndex);
		return campo.editavel;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		CampoReferencia campo = campos.get(rowIndex);
		return columnIndex == 0 ? campo.nome : campo.getValor(referencia);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		CampoReferencia campo = campos.get(rowIndex);
		if (columnIndex == COLUNAS.length - 1 && aValue != null) {
			campo.setValor(referencia, aValue.toString());
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}

class CampoReferencia {
	final boolean editavel;
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
		}

		return null;
	}

	void setValor(Referencia referencia, String valor) {
		if ("alias".equals(nome)) {
		} else if ("prefixo".equals(nome)) {
		} else if ("aliasAlt".equals(nome)) {
			referencia.setAliasAlt(valor);

		} else if ("inverso".equals(nome)) {
			referencia.setInverso(Boolean.parseBoolean(valor));

		} else if ("pk".equals(nome)) {
			referencia.setPk(getInt(valor, referencia.getPk()));

		} else if ("pkNome".equals(nome)) {
			referencia.setPkNome(valor);

		} else if ("fk".equals(nome)) {
			referencia.setFk(getInt(valor, referencia.getFk()));

		} else if ("fkNome".equals(nome)) {
			referencia.setFkNome(valor);

		} else if ("preJoin".equals(nome)) {
			referencia.setPreJoin(valor);
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