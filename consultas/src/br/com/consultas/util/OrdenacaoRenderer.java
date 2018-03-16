package br.com.consultas.util;

import java.awt.Component;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import br.com.consultas.visao.comp.Label;

public class OrdenacaoRenderer extends Label implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	private final boolean descendente;
	private final Icon iconeDesc;
	private final Icon iconeAsc;

	public OrdenacaoRenderer(boolean descendente) {
		iconeDesc = criarImagem("desc.png");
		iconeAsc = criarImagem("asc.png");
		this.descendente = descendente;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int rowIndex, int vColIndex) {

		setText(value.toString());
		setToolTipText(value.toString());
		setIcon(descendente ? iconeDesc : iconeAsc);

		return this;
	}

	public ImageIcon criarImagem(String nome) {
		URL url = getClass().getResource("/resources/" + nome);
		return new ImageIcon(url);
	}
}