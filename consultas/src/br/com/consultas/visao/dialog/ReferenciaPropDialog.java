package br.com.consultas.visao.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import br.com.consultas.Referencia;
import br.com.consultas.util.Util;
import br.com.consultas.visao.Formulario;
import br.com.consultas.visao.PainelAbas;
import br.com.consultas.visao.PainelConsultas.CellRenderer;
import br.com.consultas.visao.comp.ScrollPane;
import br.com.consultas.visao.comp.Table;
import br.com.consultas.visao.modelo.ModeloOrdenacao;
import br.com.consultas.visao.modelo.ModeloReferencia;

public class ReferenciaPropDialog extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final Table table;

	public ReferenciaPropDialog(Formulario formulario, Referencia referencia) {
		super(referencia.getAlias());

		ModeloReferencia modelo = new ModeloReferencia(referencia);
		table = new Table(new ModeloOrdenacao(modelo));
		table.getColumnModel().getColumn(ModeloReferencia.COLUNAS.length - 1).setCellRenderer(new CellRenderer());
		table.ajustar(formulario.getGraphics());

		add(BorderLayout.CENTER, new ScrollPane(table));
		add(BorderLayout.SOUTH, new PainelControle(this));

		setSize(400, 400);
		setLocationRelativeTo(formulario);

		cfg(formulario);
		setVisible(true);
		SwingUtilities.invokeLater(() -> toFront());
	}

	private void cfg(Formulario formulario) {
		Util.setActionESC((JComponent) getContentPane(), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Util.fechar(ReferenciaPropDialog.this);
			}
		});

		Util.setWindowListener(this, formulario);
	}

	private class PainelControle extends PainelAbas {
		private static final long serialVersionUID = 1L;

		PainelControle(Dialogo dialogo) {
			super(dialogo, false);
		}

		@Override
		public void executar() {
		}
	}
}