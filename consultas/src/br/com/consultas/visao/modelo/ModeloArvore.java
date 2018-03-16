package br.com.consultas.visao.modelo;

import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import br.com.consultas.Referencia;

public class ModeloArvore implements TreeModel {
	private final EventListenerList listenerList = new EventListenerList();
	private final List<Referencia> referencias;
	private final String raiz;

	public ModeloArvore(List<Referencia> referencias, String raiz) {
		this.raiz = raiz + " (" + referencias.size() + ")";
		this.referencias = referencias;
	}

	public List<Referencia> getReferencias() {
		return referencias;
	}

	@Override
	public Object getRoot() {
		return raiz;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof String) {
			return referencias.get(index);
		}

		return ((Referencia) parent).get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof String) {
			return referencias.size();
		}

		return ((Referencia) parent).getTotalFilhos();
	}

	@Override
	public boolean isLeaf(Object parent) {
		if (parent instanceof String) {
			return referencias.isEmpty();
		}

		return ((Referencia) parent).getTotalFilhos() == 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof String) {
			return referencias.indexOf((Referencia) child);
		}

		return ((Referencia) parent).getReferencias().indexOf((Referencia) child);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

	public void treeNodesChanged(TreeModelEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				((TreeModelListener) listeners[i + 1]).treeNodesChanged(event);
			}
		}
	}
}