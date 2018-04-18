package br.com.consultas.visao.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import br.com.consultas.Tabela;
import br.com.consultas.util.Util;
import br.com.consultas.visao.Formulario;
import br.com.consultas.visao.PainelAbas;
import br.com.consultas.visao.PainelReferencia;

public class ReferenciaDialog extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final Formulario formulario;

	public ReferenciaDialog(Formulario formulario, Tabela tabela) {
		super(tabela.getNome() + " - " + tabela.getAlias().getValor());
		this.formulario = formulario;

		add(BorderLayout.CENTER, new PainelReferencia(formulario, tabela, null));
		add(BorderLayout.SOUTH, new PainelControle(this));

		setSize(600, 400);
		setLocationRelativeTo(formulario);

		cfg();
		setVisible(true);
		SwingUtilities.invokeLater(() -> toFront());
	}

	private void cfg() {
		Util.setActionESC((JComponent) getContentPane(), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Util.fechar(ReferenciaDialog.this);
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