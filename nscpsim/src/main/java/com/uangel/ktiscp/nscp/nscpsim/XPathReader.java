package com.uangel.ktiscp.nscp.nscpsim;

import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.uangel.utms.uTMS_Util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XPathReader {
	private String xml;
	private Document xmlDocument;
	private XPath xPath;
	private boolean strFlag;
	
	public XPathReader(String xml, boolean strFlag) {
		this.xml = xml;
		this.strFlag = strFlag;
		initObjects();
	}
	
	private void initObjects(){
		try {
			if ( strFlag == false ) {
				xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml);
				xPath =  XPathFactory.newInstance().newXPath();
			} else {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            xmlDocument = builder.parse(new InputSource(new StringReader(xml)));
	            xPath =  XPathFactory.newInstance().newXPath();
			}
		} catch (Exception e) {
			log.error("Exception in initObjects()", e);
		}
	}
 
	public String read(String expression, boolean logFlag){
		try {
			XPathExpression xPathExpression = xPath.compile(expression);
			String value = (String)xPathExpression.evaluate(xmlDocument, XPathConstants.STRING);
			if ( logFlag ) {
				log.info("File:{}, Path:{}, value:{}", xml, expression, value);
			}
			return value;
		} catch (XPathExpressionException e) {
			log.error("Not found xml data. path :"+ expression, e);
			return null;
		}
	}
	
	public NodeList readNodeList(String expression){
		try {
			XPathExpression xPathExpression = xPath.compile(expression);
			NodeList value = (NodeList)xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);
			return value;
		} catch (XPathExpressionException e) {
			log.error("Not found xml data. path :"+expression, e);
			return null;
		}
	}
	
	public int getCount(String expression) {
		try {
			String countExpression = StringUtil.sprintf("count(%s)", expression);
			XPathExpression xPathExpression = xPath.compile(countExpression);
			Double count = (Double)xPathExpression.evaluate(xmlDocument, XPathConstants.NUMBER);
			return count.intValue();
		} catch (XPathExpressionException e) {
			log.error("Exception in getCount()", e);
			return -1;
		}
	}
	
	public void modifyXmlFile(String path, String value) {
		try {
			// 파일이 변경되었을 지도 모르니 새로 로딩한다.
			xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml);
	        xmlDocument.getDocumentElement().normalize();
	        Element el = xmlDocument.getDocumentElement();
	
	        String[] pathElement = path.split("/");
	        for ( int i = 0; i < pathElement.length; i++ ) {
	            NodeList nl = el.getElementsByTagName(pathElement[i]);
	            el = (Element)nl.item(0);
	        }
	
	        Text text = (Text) el.getFirstChild();
	        text.setData(value);
	
	        TransformerFactory factory = TransformerFactory.newInstance();
	        Transformer transformer = factory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
	        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
	        //DOMSource 객체 생성
	        DOMSource source = new DOMSource(xmlDocument.getDocumentElement());
	        //StreamResult 객체 생성
	        StreamResult result = new StreamResult(new File(xml));
	        //파일로 저장하기
	        transformer.transform(source, result);
		} catch (Exception e) {
			log.error("Exception in modifyXmlFile()", e);
		}
	}
}
