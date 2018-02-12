package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
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
	private JCheckBox chkAreaTransferencia = new JCheckBox(Util.getString("label.area_transferencia"), true);
	private final List<Referencia> referencias = new ArrayList<>();
	private JMenuItem itemSQL = new JMenuItem("GERAR SQL");
	private JMenuItem itemMeuSQL = new JMenuItem("DADOS");
	private final Tabelas tabelas = new Tabelas();
	private JTextArea textArea = new JTextArea();
	private JPopupMenu popup = new JPopupMenu();
	private Referencia selecionado;
	private JTree arvore;

	public Formulario(File file) throws Exception {
		XML.processar(file, tabelas, referencias);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300, 300);
		setLocationRelativeTo(null);
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		arvore = new JTree(new ModeloArvore(referencias));
		//arvore.setRootVisible(false);
		arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		arvore.addMouseListener(new OuvinteArvore());
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, chkAreaTransferencia);
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
	}

	private void texto(String string) {
		textArea.setText(string);
		if(chkAreaTransferencia.isSelected()) {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			if(clipboard != null) {
				clipboard.setContents(new StringSelection(string), null);
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