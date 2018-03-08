package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import br.com.consultas.Tabela;
import br.com.consultas.util.Util;
import br.com.consultas.visao.modelo.ModeloCampo;

public class CampoDialog extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JTable table;

	public CampoDialog(Formulario formulario, Tabela tabela) {
		ModeloCampo modelo = new ModeloCampo(tabela);
		table = new JTable(modelo);
		setTitle(tabela.getNome() + " - REGISTROS [" + modelo.getRowCount() + "]");
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, new JScrollPane(table));
		add(BorderLayout.SOUTH, new PainelControle());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
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

	class PainelControle extends JPanel {
		private static final long serialVersionUID = 1L;
		JButton buttonFechar = new JButton(Util.getString("label.fechar"));

		PainelControle() {
			setLayout(new FlowLayout(FlowLayout.LEFT));
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