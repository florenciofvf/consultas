package br.com.consultas.xml;

import java.io.File;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.com.consultas.Campo;
import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.Tabelas;
import br.com.consultas.util.Util;

public class XML {
	public static void processar(File file, Tabelas tabelas, List<Referencia> referencias) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		parser.parse(file, new XMLHander(tabelas, referencias));
	}
}

class XMLHander extends DefaultHandler {
	private final List<Referencia> referencias;
	private boolean lendoReferencias;
	private Referencia selecionado;
	private final Tabelas tabelas;
	private boolean lendoTabelas;

	public XMLHander(Tabelas tabelas, List<Referencia> referencias) {
		this.referencias = referencias;
		this.tabelas = tabelas;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equals("tabelas")) {
			lendoTabelas = true;

		} else if(qName.equals("refs")) {
			lendoReferencias = true;

		} else {
			if(lendoTabelas) {

				Tabela tab = new Tabela(qName);
				List<Campo> campos = Util.criarCampos(attributes);
				for(Campo c : campos) {
					tab.add(c);
				}
				tabelas.add(tab);

			} else if(lendoReferencias) {
				if(qName.equals("obj")) {

					String alias = Util.getString(attributes, "alias", null);
					boolean invs = Util.getBoolean(attributes, "inverso", false);
					final int pk = Util.getInteger(attributes, "pk", 0);
					final int fk = Util.getInteger(attributes, "fk", 1);
					String preJn = Util.getString(attributes, "preJoin", null);

					Referencia ref = new Referencia(alias, invs, pk, fk, preJn);

					if(selecionado == null) {
						referencias.add(ref);
					} else {
						selecionado.add(ref);
					}

					selecionado = ref;

				}
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equals("tabelas")) {
			lendoTabelas = false;

		} else if(qName.equals("refs")) {
			lendoReferencias = false;

		} else if(lendoReferencias) {
			if(qName.equals("obj")) {
				selecionado = selecionado.getPai();
			}
		}
	}
}