package br.com.consultas.visao.comp;

import java.awt.Graphics;

import javax.swing.JTable;
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

	public void ajustar(Graphics graphics) {
		Util.ajustar(this, graphics);
	}

	public void ajustar(Graphics graphics, int ajuste) {
		Util.ajustar(this, graphics, ajuste);
	}
}