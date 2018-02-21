package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import br.com.consultas.Referencia;
import br.com.consultas.Tabelas;
import br.com.consultas.util.Util;
import br.com.consultas.xml.XML;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JCheckBox chkAreaTransferencia = new JCheckBox(Util.getString("label.area_transferencia"), Util.getBooleanConfig("area_transferencia"));
	private final JCheckBox chkAbrirDialog = new JCheckBox(Util.getString("label.abrir_dialog"), Util.getBooleanConfig("abrir_dialog"));
	private final JCheckBox chkRaizVisivel = new JCheckBox(Util.getString("label.raiz_visivel"), Util.getBooleanConfig("raiz_visivel"));
	private final JCheckBox chkLinhaRaiz = new JCheckBox(Util.getString("label.raiz_linha"), Util.getBooleanConfig("raiz_linha"));
	private final JMenuItem itemMeuSQL = new JMenuItem(Util.getString("label.gerar_dados"));
	private final JMenuItem itemSQL = new JMenuItem(Util.getString("label.gerar_sql"));
	private final List<Referencia> referencias = new ArrayList<>();
	private final JTextArea textArea = new JTextArea();
	private final JPopupMenu popup = new JPopupMenu();
	private final Tabelas tabelas = new Tabelas();
	private Referencia selecionado;
	private JTree arvore;
	private int janelas;

	public Formulario(File file) throws Exception {
		XML.processar(file, tabelas, referencias);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		setSize(800, 1000);
		setLocationRelativeTo(null);
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		arvore = new JTree(new ModeloArvore(referencias));
		arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		arvore.addMouseListener(new OuvinteArvore());
		setLayout(new BorderLayout());
		JPanel panelNorte = new JPanel();
		panelNorte.add(chkAreaTransferencia);
		panelNorte.add(chkAbrirDialog);
		panelNorte.add(chkRaizVisivel);
		panelNorte.add(chkLinhaRaiz);
		add(BorderLayout.NORTH, panelNorte);
		add(BorderLayout.CENTER, new JScrollPane(arvore));
		add(BorderLayout.SOUTH, textArea);
		popup.add(itemMeuSQL);
		popup.addSeparator();
		popup.add(itemSQL);
		itemMeuSQL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				texto(selecionado.gerarConsultaDados(tabelas));
			}
		});
		itemSQL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				texto(selecionado.gerarConsulta(tabelas));
			}
		});
		chkRaizVisivel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				arvore.setRootVisible(chkRaizVisivel.isSelected());
			}
		});
		chkLinhaRaiz.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				arvore.setRootVisible(chkRaizVisivel.isSelected());
				arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
			}
		});
	}

	void abrirJanela() {
		janelas++;
		setAlwaysOnTop(janelas <= 0);
	}

	void fecharJanela() {
		janelas--;
		setAlwaysOnTop(janelas <= 0);
		if(janelas < 0) {
			janelas = 0;
		}
	}
	
	private void texto(String string) {
		textArea.setText(string);
		if(chkAreaTransferencia.isSelected()) {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			if(clipboard != null) {
				clipboard.setContents(new StringSelection(string), null);
			}
		}
		if(chkAbrirDialog.isSelected()) {
			try {
				new DadosDialog(this, string.substring(0, string.length()-2));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class OuvinteArvore extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}
		private void processar(MouseEvent e) {
			if(!e.isPopupTrigger()) {
				return;
			}

			TreePath path = arvore.getSelectionPath();
			if(path == null) {
				return;
			}

			selecionado = (Referencia) path.getLastPathComponent();
			popup.show(arvore, e.getX(), e.getY());
		}
	}
}