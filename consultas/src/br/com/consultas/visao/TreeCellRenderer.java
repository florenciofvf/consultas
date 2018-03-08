package br.com.consultas.visao;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import br.com.consultas.Referencia;

public class TreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		Object objeto = value;

		if (objeto instanceof Referencia) {
			Referencia ref = (Referencia) objeto;
			if (ref.isEspecial()) {
				setForeground(sel ? Color.WHITE : Color.BLUE);
			} else if (ref.getCampoID() != null) {
				setForeground(sel ? Color.WHITE : Color.LIGHT_GRAY);
			} else {
				setForeground(sel ? Color.WHITE : Color.BLACK);
			}
		}

		return this;
	}
}