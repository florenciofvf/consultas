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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

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

		PainelControle() {
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