package br.com.consultas.visao.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import br.com.consultas.Tabela;
import br.com.consultas.util.Util;
import br.com.consultas.visao.Formulario;
import br.com.consultas.visao.comp.Button;
import br.com.consultas.visao.comp.PanelLeft;
import br.com.consultas.visao.comp.ScrollPane;
import br.com.consultas.visao.comp.Table;
import br.com.consultas.visao.modelo.ModeloCampo;
import br.com.consultas.visao.modelo.ModeloOrdenacao;

public class CampoDialog extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final Table table;

	public CampoDialog(Formulario formulario, Tabela tabela) {
		super();

		ModeloCampo modelo = new ModeloCampo(tabela);
		table = new Table(new ModeloOrdenacao(modelo));
		table.ajustar(formulario.getGraphics());
		setTitle(tabela.getNome() + " - REGISTROS [" + modelo.getRowCount() + "]");

		add(BorderLayout.CENTER, new ScrollPane(table));
		add(BorderLayout.SOUTH, new PainelControle());

		setSize(400, 400);
		setLocationRelativeTo(formulario);

		cfg(formulario);
		setVisible(true);
	}

	private void cfg(Formulario formulario) {
		Util.setActionESC((JComponent) getContentPane(), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Util.fechar(CampoDialog.this);
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
					Util.fechar(CampoDialog.this);
				}
			});
		}
	}
}