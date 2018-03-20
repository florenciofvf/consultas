package br.com.consultas.visao.modelo;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.consultas.Campo;
import br.com.consultas.Tabela;

public class ModeloCampo implements TableModel {
	private final String[] COLUNAS = { "NOME", "SOMENTE LEITURA", "VALOR" };
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
		return columnIndex == 1 ? Boolean.class : String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex != 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Campo campo = tabela.get(rowIndex);

		if (columnIndex == 0) {
			return campo.getNome();
		}

		if (columnIndex == 1) {
			return campo.isSomenteLeitura();
		}

		return campo.getValor();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Campo campo = tabela.get(rowIndex);

		if (aValue != null) {
			if (columnIndex == 1) {
				campo.setSomenteLeitura(Boolean.parseBoolean(aValue.toString()));
			} else if (columnIndex == 2) {
				campo.setValor(aValue.toString());
			}
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}