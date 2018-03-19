package br.com.consultas.visao.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import br.com.consultas.Tabela;
import br.com.consultas.util.Util;
import br.com.consultas.visao.Formulario;
import br.com.consultas.visao.PainelReferencia;
import br.com.consultas.visao.comp.Button;
import br.com.consultas.visao.comp.PanelLeft;

public class ReferenciaDialog extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final Formulario formulario;

	public ReferenciaDialog(Formulario formulario, Tabela tabela) {
		setTitle(tabela.getNome() + " - " + tabela.getAlias().getValor());
		this.formulario = formulario;

		add(BorderLayout.SOUTH, new PainelControle());
		add(BorderLayout.CENTER, new PainelReferencia(formulario, tabela, null));

		setSize(600, 400);
		setLocationRelativeTo(formulario);

		cfg();
		setVisible(true);
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

	private class PainelControle extends PanelLeft {
		private static final long serialVersionUID = 1L;
		private final Button buttonFechar = new Button("label.fechar");

		PainelControle() {
			add(buttonFechar);

			buttonFechar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Util.fechar(ReferenciaDialog.this);
				}
			});
		}
	}
}