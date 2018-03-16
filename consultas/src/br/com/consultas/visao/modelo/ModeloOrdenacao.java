package br.com.consultas.visao.modelo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.consultas.util.OrdenacaoRenderer;
import br.com.consultas.util.Util;

public class ModeloOrdenacao extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private int colunaOrdenacao;
	private boolean descendente;
	private Listener listener;
	private TableModel model;
	private Linha[] linhas;

	public ModeloOrdenacao(TableModel model) {
		setModel(model);
	}

	private void ordenar(int coluna) {
		colunaOrdenacao = coluna;
		Arrays.sort(linhas);
		fireTableDataChanged();
	}

	public void configurar(JTable table) {
		if (table == null) {
			return;
		}

		if (listener == null) {
			listener = new Listener(table);
		}

		JTableHeader tableHeader = table.getTableHeader();

		if (tableHeader != null) {
			tableHeader.removeMouseListener(listener);
			tableHeader.addMouseListener(listener);
		}
	}

	public void desconfigurar(JTable table) {
		table.getTableHeader().removeMouseListener(listener);
	}

	private class Listener extends MouseAdapter {
		private final JTable table;

		Listener(JTable table) {
			this.table = table;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				int tableColuna = table.columnAtPoint(e.getPoint());
				int modelColuna = table.convertColumnIndexToModel(tableColuna);

				descendente = !descendente;

				TableColumnModel columnModel = table.getColumnModel();
				TableColumn coluna = columnModel.getColumn(tableColuna);
				coluna.setHeaderRenderer(new OrdenacaoRenderer(descendente));

				ordenar(modelColuna);
			}
		}
	}

	public void setModel(TableModel model) {
		this.model = model;
		this.linhas = new Linha[model.getRowCount()];

		for (int i = 0; i < linhas.length; i++) {
			linhas[i] = new Linha(i);
		}
	}

	public TableModel getModel() {
		return model;
	}

	@Override
	public int getRowCount() {
		return model.getRowCount();
	}

	@Override
	public int getColumnCount() {
		return model.getColumnCount();
	}

	@Override
	public String getColumnName(int column) {
		return model.getColumnName(column);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return model.getColumnClass(columnIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return model.getValueAt(linhas[rowIndex].indice, columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return model.isCellEditable(linhas[rowIndex].indice, columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		model.setValueAt(aValue, linhas[rowIndex].indice, columnIndex);
	}

	private class Linha implements Comparable<Linha> {
		private final int indice;

		public Linha(int indice) {
			this.indice = indice;
		}

		@Override
		public int compareTo(Linha o) {
			String string = (String) model.getValueAt(indice, colunaOrdenacao);
			String outra = (String) model.getValueAt(o.indice, colunaOrdenacao);

			if (Util.ehVazio(string)) {
				string = "";
			}

			if (Util.ehVazio(outra)) {
				outra = "";
			}

			if (descendente) {
				return string.compareTo(outra);
			} else {
				return outra.compareTo(string);
			}
		}
	}
}