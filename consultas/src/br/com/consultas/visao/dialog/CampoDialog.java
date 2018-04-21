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
import br.com.consultas.visao.comp.CheckBox;
import br.com.consultas.visao.comp.ScrollPane;
import br.com.consultas.visao.comp.Table;
import br.com.consultas.visao.modelo.ModeloCampo;
import br.com.consultas.visao.modelo.ModeloOrdenacao;

public class CampoDialog extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkDestaque = new CheckBox("label.destaque", "false");
	private final Table table;

	public CampoDialog(Formulario formulario, Tabela tabela) {
		ModeloCampo modelo = new ModeloCampo(tabela);
		table = new Table(new ModeloOrdenacao(modelo));
		table.ajustar(formulario.getGraphics());
		setTitle(tabela.getNome() + " - REGISTROS [" + modelo.getRowCount() + "]");

		add(BorderLayout.NORTH, chkDestaque);
		add(BorderLayout.CENTER, new ScrollPane(table));
		add(BorderLayout.SOUTH, new PainelControle(this));

		setSize(400, 400);
		setLocationRelativeTo(formulario);

		chkDestaque.setSelected(tabela.isDestaque());
		chkDestaque.addActionListener(e -> tabela.setDestaque(chkDestaque.isSelected()));

		cfg(formulario);
		setVisible(true);
		SwingUtilities.invokeLater(() -> toFront());
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