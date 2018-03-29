package br.com.consultas.util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import br.com.consultas.Referencia;

public class TreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	private final Icon icon;

	public TreeCellRenderer(Icon icon) {
		this.icon = icon;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		setIcon(icon);

		if (value instanceof Referencia) {
			Referencia ref = (Referencia) value;

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