package br.com.consultas.visao.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.consultas.Referencia;

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
			campos.add(new CampoReferencia("especial"));
			campos.add(new CampoReferencia("pk"));
			campos.add(new CampoReferencia("fk"));
			campos.add(new CampoReferencia("pkNome"));
			campos.add(new CampoReferencia("fkNome"));
			campos.add(new CampoReferencia("cloneCompleto", true));
			campos.add(new CampoReferencia("preJoin", true));
			campos.add(new CampoReferencia("resumo", true));
			campos.add(new CampoReferencia("campoID", true));
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