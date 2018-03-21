package br.com.consultas.visao.modelo;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.consultas.Campo;
import br.com.consultas.Tabela;

public class ModeloCampo implements TableModel {
	private final Class<?>[] TIPOS = { String.class, Boolean.class, Boolean.class, String.class };
	private final String[] COLUNAS = { "NOME", "SELECIONADO", "SOMENTE LEITURA", "VALOR" };
	private final byte SOMENTE_LEITURA = 2;
	private final byte SELECIONADO = 1;
	private final byte VALOR = 3;
	private final byte NOME = 0;

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
		return TIPOS[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex != NOME;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Campo campo = tabela.get(rowIndex);

		if (columnIndex == NOME) {
			return campo.getNome();
		}

		if (columnIndex == SELECIONADO) {
			return campo.isSelecionado();
		}

		if (columnIndex == SOMENTE_LEITURA) {
			return campo.isSomenteLeitura();
		}

		if (columnIndex == VALOR) {
			return campo.getValor();
		}

		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Campo campo = tabela.get(rowIndex);

		if (aValue != null) {
			if (columnIndex == SOMENTE_LEITURA) {
				campo.setSomenteLeitura(Boolean.parseBoolean(aValue.toString()));

			} else if (columnIndex == SELECIONADO) {
				campo.setSelecionado(Boolean.parseBoolean(aValue.toString()));

			} else if (columnIndex == VALOR) {
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