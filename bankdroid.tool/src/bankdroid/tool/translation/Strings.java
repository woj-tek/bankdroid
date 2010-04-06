package bankdroid.tool.translation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Strings
{
	private static final String E_STRING = "string";
	public final static String FILE_TYPE = "strings";
	private final Map<String, String> values = new HashMap<String, String>();

	private final File source;

	public Strings( final File source ) throws ParserConfigurationException, SAXException, IOException
	{
		if ( source == null || !source.exists() || !source.isFile() )
			throw new IllegalArgumentException("Missing or invalid strings file: " + source);

		this.source = source;

		final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(source, new StringHandler(this));
	}

	public void set( final String key, final String value )
	{
		values.put(key, value);
	}

	public String get( final String key )
	{
		return values.get(key);
	}

	public Set<String> keySet()
	{
		return Collections.unmodifiableSet(values.keySet());
	}

	public void writeToFile( final File template ) throws ParserConfigurationException, SAXException, IOException,
			TransformerFactoryConfigurationError, TransformerException
	{
		//FIXME

		final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		final Document document = builder.parse(template);

		//merge template values and values in the current strings list
		final Map<String, String> vs = new HashMap<String, String>(values);
		final List<String> itemsToRemove = new ArrayList<String>();

		//FIXME the values are not overwritten correctly in the document
		final NodeList strings = document.getElementsByTagName(E_STRING);
		final int itemCount = strings.getLength();
		for ( int i = 0; i < itemCount; i++ )
		{
			final Node item = strings.item(i);
			final String key = item.getAttributes().getNamedItem("name").getNodeValue();
			if ( vs.containsKey(key) )
			{
				final String original = item.getTextContent();

				final String value = vs.remove(key);

				//restore whitespaces in value
				final StringBuilder correctValue = new StringBuilder(value);

				//leading spaces
				int c = 0;
				while ( c < original.length() && Character.isWhitespace(original.charAt(c)) )
				{
					correctValue.insert(c, original.charAt(c));
					c++;
				}
				//trailing spaces
				final int insertPos = correctValue.length();
				if ( c < original.length() )
				{
					c = original.length() - 1;
					while ( c >= 0 && Character.isWhitespace(original.charAt(c)) )
					{
						correctValue.insert(insertPos, original.charAt(c));
						c--;
					}
				}

				item.setTextContent(correctValue.toString());
				System.out.println("Setting value: " + key + " -> " + item.getTextContent());
			}
			else
			{
				itemsToRemove.add(key);
			}
		}

		//FIXME remove unnecessary strings

		//FIXME add remaining strings

		//write the document to the original file
		writeXmlFile(document, source);
	}

	// This method writes a DOM document to a file
	public static void writeXmlFile( final Document doc, final File file ) throws TransformerFactoryConfigurationError,
			TransformerException
	{
		// Prepare the DOM document for writing
		final Source source = new DOMSource(doc);

		// Prepare the output file
		final Result result = new StreamResult(file);

		// Write the DOM document to the file
		final Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform(source, result);
	}
}
