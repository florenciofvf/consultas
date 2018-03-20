package br.com.consultas.visao.comp;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import br.com.consultas.util.Util;
import br.com.consultas.visao.modelo.ModeloOrdenacao;

public class Table extends JTable {
	private static final long serialVersionUID = 1L;

	public Table(ModeloOrdenacao modelo) {
		super(modelo);
		modelo.configurar(this);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	@Override
	public void setModel(TableModel modelo) {
		if (!(modelo instanceof ModeloOrdenacao)) {
			throw new IllegalArgumentException();
		}

		ModeloOrdenacao atual = (ModeloOrdenacao) getModel();

		if (atual != null) {
			atual.desconfigurar(this);
		}

		((ModeloOrdenacao) modelo).configurar(this);
		super.setModel(modelo);
	}

	public void addColuna(String titulo, Vector<Object> dados, Boolean ordenarNumero) {
		ModeloOrdenacao atual = (ModeloOrdenacao) getModel();
		TableModel model = atual.getModel();

		if (model instanceof DefaultTableModel) {
			atual.addColumn(titulo, dados, ordenarNumero);
		}
	}

	public void ajustar(Graphics graphics) {
		Util.ajustar(this, graphics);
	}

	public void ajustar(Graphics graphics, int ajuste) {
		Util.ajustar(this, graphics, ajuste);
	}

	public List<String> getIds(int coluna) {
		List<String> resposta = new ArrayList<>();

		ModeloOrdenacao atual = (ModeloOrdenacao) getModel();
		TableModel model = atual.getModel();
		int total = model.getRowCount();

		for (int i = 0; i < total; i++) {
			Object obj = model.getValueAt(i, coluna);

			if (obj != null && !Util.ehVazio(obj.toString())) {
				resposta.add(obj.toString());
			}
		}

		return resposta;
	}
}