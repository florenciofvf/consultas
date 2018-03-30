package br.com.consultas.visao.modelo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.consultas.util.OrdenacaoRenderer;
import br.com.consultas.util.Util;

public class ModeloOrdenacao extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private Map<Integer, Boolean> mapaTipoColuna;
	private boolean ordenarNumero;
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

	public boolean isOrdenarNumero() {
		return ordenarNumero;
	}

	public void setOrdenarNumero(boolean ordenarNumero) {
		this.ordenarNumero = ordenarNumero;
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
			if (e.getClickCount() >= Util.DOIS) {
				int tableColuna = table.columnAtPoint(e.getPoint());
				int modelColuna = table.convertColumnIndexToModel(tableColuna);

				descendente = !descendente;

				TableColumnModel columnModel = table.getColumnModel();
				TableColumn coluna = columnModel.getColumn(tableColuna);
				TableCellRenderer headerRenderer = coluna.getHeaderRenderer();

				if (!(headerRenderer instanceof OrdenacaoRenderer)) {
					int largura = coluna.getPreferredWidth() + Util.LARGURA_ICONE_ORDENAR;
					coluna.setPreferredWidth(largura);
				}

				ordenarNumero = false;

				if (mapaTipoColuna != null) {
					ordenarNumero = isNumero(modelColuna);
				}

				coluna.setHeaderRenderer(new OrdenacaoRenderer(descendente, ordenarNumero));
				ordenar(modelColuna);
			}
		}
	}

	private boolean isNumero(int coluna) {
		Boolean b = mapaTipoColuna.get(coluna);

		if (b == null) {
			b = Boolean.FALSE;
		}

		return b;
	}

	public void setModel(TableModel model) {
		this.model = model;
		this.linhas = new Linha[model.getRowCount()];

		for (int i = 0; i < linhas.length; i++) {
			linhas[i] = new Linha(i);
		}
	}

	public void addColumn(Object columnName, Vector<?> columnData, Boolean ordenarNumero) {
		if (!(model instanceof DefaultTableModel)) {
			throw new IllegalArgumentException();
		}

		((DefaultTableModel) model).addColumn(columnName, columnData);
		fireTableStructureChanged();

		if (mapaTipoColuna != null) {
			mapaTipoColuna.put(model.getColumnCount() - 1, ordenarNumero);
		}
	}

	public void configMapaTipoColuna(Vector<String> vector) {
		Map<String, Boolean> map = new HashMap<>();
		map.put("java.math.BigDecimal", Boolean.TRUE);
		map.put("java.math.BigInteger", Boolean.TRUE);
		map.put("java.lang.Character", Boolean.FALSE);
		map.put("java.lang.Boolean", Boolean.FALSE);
		map.put("java.lang.Integer", Boolean.TRUE);
		map.put("java.lang.String", Boolean.FALSE);
		map.put("java.lang.Double", Boolean.TRUE);
		map.put("java.lang.Float", Boolean.TRUE);
		map.put("java.lang.Short", Boolean.TRUE);
		map.put("java.lang.Long", Boolean.TRUE);
		map.put("java.lang.Byte", Boolean.TRUE);

		mapaTipoColuna = new HashMap<>();

		for (int i = 0; i < vector.size(); i++) {
			Boolean b = map.get(vector.get(i));

			if (b == null) {
				b = Boolean.FALSE;
			}

			mapaTipoColuna.put(i, b);
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

	public Object getValueAt2(int rowIndex, int columnIndex) {
		return model.getValueAt(rowIndex, columnIndex);
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

			if (!ordenarNumero) {
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
			} else {
				Long valor = Util.ehVazio(string) ? 0 : Long.valueOf(string);
				Long outro = Util.ehVazio(outra) ? 0 : Long.valueOf(outra);

				if (descendente) {
					return valor.compareTo(outro);
				} else {
					return outro.compareTo(valor);
				}
			}

		}
	}
}