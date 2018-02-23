package br.com.consultas.visao;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import br.com.consultas.Tabela;

public class CampoDialog extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JTable table;

	public CampoDialog(final Formulario formulario, Tabela tabela) {
		table = new JTable(new ModeloCampo(tabela));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(new JScrollPane(table));
		setTitle(tabela.getNome());
		setAlwaysOnTop(true);
		setSize(400, 400);
		setLocationRelativeTo(formulario);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent e) {
				setState(NORMAL);
			}

			public void windowOpened(WindowEvent e) {
				formulario.abrirJanela();
			}

			public void windowClosing(WindowEvent e) {
				formulario.fecharJanela();
			}
		});
	}
}