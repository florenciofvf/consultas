package br.com.consultas.visao.modelo;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.consultas.Campo;
import br.com.consultas.Tabela;

public class ModeloCampo implements TableModel {
	private final String[] COLUNAS = { "NOME", "VALOR" };
	private final Tabela tabela;

	public ModeloCampo(Tabela tabela) {
		this.tabela = tabela;
	}

	@Override
	public int getRowCount() {
		return tabela.getCampos().size();
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
		return columnIndex == COLUNAS.length - 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Campo campo = tabela.get(rowIndex);

		return columnIndex == 0 ? campo.getNome() : campo.getValor();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Campo campo = tabela.get(rowIndex);

		if (columnIndex == COLUNAS.length - 1 && aValue != null) {
			campo.setValor(aValue.toString());
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}