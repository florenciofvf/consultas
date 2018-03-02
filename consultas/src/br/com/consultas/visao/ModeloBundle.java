package br.com.consultas.visao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ModeloBundle implements TableModel {
	private final String[] COLUNAS = { "NOME", "VALOR" };
	private final List<ChaveValor> listagem;

	public ModeloBundle(ResourceBundle bundle) {
		listagem = new ArrayList<ChaveValor>();

		Enumeration<String> keys = bundle.getKeys();

		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String val = bundle.getString(key);
			listagem.add(new ChaveValor(key, val));
		}

		Collections.sort(listagem, new Comparator<ChaveValor>() {
			@Override
			public int compare(ChaveValor o1, ChaveValor o2) {
				return o1.chave.compareTo(o2.chave);
			}
		});
	}

	@Override
	public int getRowCount() {
		return listagem.size();
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
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ChaveValor cv = listagem.get(rowIndex);
		return columnIndex == 0 ? cv.chave : cv.valor;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}

class ChaveValor {
	final String chave;
	final String valor;

	public ChaveValor(String chave, String valor) {
		this.chave = chave;
		this.valor = valor;
	}
}