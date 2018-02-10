package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import br.com.consultas.Referencia;
import br.com.consultas.Tabelas;
import br.com.consultas.xml.XML;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final List<Referencia> referencias = new ArrayList<>();
	private JMenuItem itemSQL = new JMenuItem("GERAR SQL");
	private JMenuItem itemMeuSQL = new JMenuItem("DADOS");
	private final Tabelas tabelas = new Tabelas();
	private JPopupMenu popup = new JPopupMenu();
	private Referencia selecionado;
	private JTree arvore;

	public Formulario(File file) throws Exception {
		XML.processar(file, tabelas, referencias);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500, 300);
		setLocationRelativeTo(null);
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		arvore = new JTree(new ModeloArvore(referencias));
		arvore.setRootVisible(false);
		arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		arvore.addMouseListener(new OuvinteArvore());
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, new JScrollPane(arvore));
		popup.add(itemMeuSQL);
		popup.addSeparator();
		popup.add(itemSQL);
		itemMeuSQL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(selecionado.gerarConsultaDados(tabelas));
			}
		});
		itemSQL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(selecionado.gerarConsulta(tabelas));
			}
		});
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
