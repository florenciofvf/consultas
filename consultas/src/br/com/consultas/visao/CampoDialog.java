package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import br.com.consultas.Tabela;
import br.com.consultas.util.Util;

public class CampoDialog extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JTable table;

	public CampoDialog(final Formulario formulario, Tabela tabela) {
		ModeloCampo modelo = new ModeloCampo(tabela);
		table = new JTable(modelo);
		setTitle(tabela.getNome() + " - REGISTROS [" + modelo.getRowCount() + "]");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, new JScrollPane(table));
		add(BorderLayout.SOUTH, new PainelControle());
		setAlwaysOnTop(true);
		setSize(400, 400);
		setLocationRelativeTo(formulario);
		cfg(formulario);
		setVisible(true);
	}

	private void cfg(final Formulario formulario) {
		((JComponent) getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");
		((JComponent) getContentPane()).getActionMap().put("esc", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				fechar();
			}
		});

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

	void fechar() {
		WindowEvent event = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		EventQueue systemEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		systemEventQueue.postEvent(event);
	}

	class PainelControle extends JPanel {
		private static final long serialVersionUID = 1L;
		JButton buttonFechar = new JButton(Util.getString("label.fechar"));

		public PainelControle() {
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(buttonFechar);

			buttonFechar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					fechar();
				}
			});
		}
	}

}