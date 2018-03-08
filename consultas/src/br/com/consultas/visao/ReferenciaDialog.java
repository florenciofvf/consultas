package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.util.Util;

public class ReferenciaDialog extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Formulario formulario;

	public ReferenciaDialog(Formulario formulario, List<Referencia> referencias, Tabela tabela) {
		setTitle(tabela.getNome() + " - " + tabela.getAlias().getValor());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		this.formulario = formulario;

		add(BorderLayout.SOUTH, new PainelControle());
		add(BorderLayout.CENTER, new PainelReferencia(formulario, formulario.referencias, tabela));
		setAlwaysOnTop(true);
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

	class PainelControle extends JPanel {
		private static final long serialVersionUID = 1L;
		JButton buttonFechar = new JButton(Util.getString("label.fechar"));

		PainelControle() {
			setLayout(new FlowLayout(FlowLayout.LEFT));
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