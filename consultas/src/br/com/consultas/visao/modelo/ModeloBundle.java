package br.com.consultas.visao.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.consultas.util.Persistencia;
import br.com.consultas.util.Util;

public class ModeloBundle implements TableModel {
	private final String[] COLUNAS = { "NOME", "VALOR" };
	private final List<ChaveValor> listagem;
	private final boolean atualizarBundle;

	public ModeloBundle(ResourceBundle bundle, boolean atualizarBundle) {
		this.atualizarBundle = atualizarBundle;
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
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ChaveValor cv = listagem.get(rowIndex);
		return columnIndex == 0 ? cv.chave : cv.valor;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		ChaveValor cv = listagem.get(rowIndex);

		if (columnIndex == 0) {
			cv.chave = aValue.toString();
		} else if (columnIndex == 1) {
			cv.valor = aValue.toString();
		}

		if (atualizarBundle) {
			Util.bundleConfig = new Recurso(listagem);
			try {
				Persistencia.close();
			} catch (Exception ex) {
				ex.printStackTrace();
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

class ChaveValor {
	String chave;
	String valor;

	public ChaveValor(String chave, String valor) {
		this.chave = chave;
		this.valor = valor;
	}
}