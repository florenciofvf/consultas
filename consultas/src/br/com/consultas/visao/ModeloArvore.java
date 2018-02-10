package br.com.consultas.visao;

import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import br.com.consultas.Referencia;

public class ModeloArvore implements TreeModel {
	private final List<Referencia> referencias;

	public ModeloArvore(List<Referencia> referencias) {
		this.referencias = referencias;
	}

	public List<Referencia> getReferencias() {
		return referencias;
	}

	@Override
	public Object getRoot() {
		return "Consultas";
	}

	@Override
	public Object getChild(Object parent, int index) {
		if(parent instanceof String) {
			return referencias.get(index);
		}

		return ((Referencia)parent).get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		if(parent instanceof String) {
			return referencias.size();
		}

		return ((Referencia)parent).getCount();
	}

	@Override
	public boolean isLeaf(Object parent) {
		if(parent instanceof String) {
			return referencias.isEmpty();
		}

		return ((Referencia)parent).getCount() == 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent instanceof String) {
			return referencias.indexOf((Referencia)child);
		}

		return ((Referencia)parent).getReferencias().indexOf((Referencia)child);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
	}
}