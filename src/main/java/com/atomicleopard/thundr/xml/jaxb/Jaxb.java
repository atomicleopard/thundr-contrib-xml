package com.atomicleopard.thundr.xml.jaxb;

import java.beans.Introspector;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.atomicleopard.thundr.xml.XmlException;

/**
 * This class provides a centralised and consistent way of handling Xml to Bean and Bean to Xml conversion.
 * As well as managing standard JAXB conversions in a convenient way, it can also be used to read and write xml
 * in a streaming fashion, which allows processing of large amounts of xml without requiring the full document or model to
 * be in memory.
 * 
 * <h4>Reading</h4> Basic XML to Javabean conversions can be achieved as in the following examples:
 * 
 * <pre>
 * <code>
 * Javabean bean = jaxb.read(Javabean.class).from(data).one(); // where data is an InputStream, byte[] or String
 * List<Javabean> beans = jaxb.read(Javabean.class).from(data).list(); // where data is an InputStream, byte[] or String
 * </code>
 * </pre>
 * 
 * Streamed reading (i.e. limiting memory consumption) can be achieved as in the following example:
 * 
 * <pre>
 * <code>
 * MyResult result = jaxb.read(Javabean.class).from(data).stream(new Batch&lt;JavaBean, MyResult&gt;(){
 * 		public MyResult process(MyResult result, List&lt;JavaBean&gt; batch);
 * 			// process batch - @see {@link Batch}
 * 			service.save(batch);
 * 			return result == null ? new MyResult() : result.mutate(); 
 * 		}
 * }); 
 * </code>
 * </pre>
 * 
 * To understand how to take advantage of the Map/Reduce interface, check out the {@link Batch} javadoc
 * 
 * <h4>Writing</h4> Basic Javabean to XML conversions can be achieved as in the following examples:
 * 
 * <pre>
 * <code>
 * jaxb.write(javabean).to(outputstream);
 * jaxb.write(javabean).to(writer);
 * String xml = jaxb.write(javabean).string();
 * </code>
 * </pre>
 * 
 * To write many objects or to stream xml creation you can do the following:
 * 
 * <pre>
 * <code>
 * jaxb.write(listOfBeans).to(outputstream);
 * jaxb.write(iteratorOfBeans).to(writer);
 * String xml = jaxb.write(iterableOfBeans).string();
 * </code>
 * </pre>
 * 
 * To stream large amounts of xml, use the {@link #write(Iterable)} or {@link #write(Iterator)} methods, and supply the javabeans in batches, like so:
 * 
 * <pre>
 * <code>
 * Iterator&lt;MyBean&gt; iterator = new Iterator&lt;MyBean&gt;() {
 * 		List&lt;MyBean&gt; batch;
 * 
 * 		public MyBean next() {
 * 			return batch.remove(0);
 * 		}
 * 
 * 		public boolean hasNext() {
 * 			if(batch.isEmpty()){
 * 				batch = service.nextBatch(); // acquire another batch
 * 			}
 * 			return !batch.isEmpty();
 *		}
 * 
 * 		public void remove() {
 * 		}
 * };
 * jaxb.write(iterator).to(outputstream);
 * </code>
 * </pre>
 * 
 * <h4>Resource consumption</h4>
 * {@link Jaxb} retains a {@link JAXBContext} for each type it transforms, so they should be considered expensive to throw away and recreate.
 * They are threadsafe and beyond creation of {@link JAXBContext} objects stateless, so you can create one and share it amongst many consumers.  
 */
public class Jaxb {
	private ConcurrentHashMap<Class<?>, JAXBContext> contextMap = new ConcurrentHashMap<>();

	public <T> JaxbReader<T> read(Class<T> type) {
		return new JaxbReader<T>(this, type);
	}

	public <T> JaxbWriter<T> write(T output) {
		return new JaxbWriter<T>(this, output);
	}

	public <T> JaxbBatchWriter<T> write(Iterable<T> output) {
		return new JaxbBatchWriter<T>(this, output.iterator());
	}

	public <T> JaxbBatchWriter<T> write(Iterator<T> output) {
		return new JaxbBatchWriter<T>(this, output);
	}

	@SuppressWarnings("unchecked")
	protected <T> T readOne(InputStream stream, Class<T> type, boolean validate) {
		T value = null;
		try {
			XMLInputFactory inputFactory = newXmlInputFactory(validate);
			XMLStreamReader xmlr = inputFactory.createXMLStreamReader(stream);

			JAXBContext jaxbContext = getJaxbContext(type);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			value = (T) unmarshaller.unmarshal(xmlr);

			return value;
		} catch (JAXBException | XMLStreamException e) {
			throw new XmlException(e, "Failed to read into type '%s': %s", type, e.getMessage());
		}
	}

	protected <T, R> R readMany(InputStream stream, Class<T> type, boolean validate, int batchSize, Batch<T, R> batchProcessor) {
		try {
			XMLInputFactory inputFactory = newXmlInputFactory(validate);
			XMLStreamReader xmlr = inputFactory.createXMLStreamReader(stream);
			JAXBContext jaxbContext = getJaxbContext(type);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			while (xmlr.getEventType() != XMLStreamConstants.START_ELEMENT) {
				// skip headers etc
				xmlr.nextTag();
			}

			// skip the root element
			xmlr.nextTag();

			R result = null;
			List<T> batch = new ArrayList<T>();
			while (xmlr.getEventType() == XMLStreamConstants.START_ELEMENT) {
				JAXBElement<T> element = unmarshaller.unmarshal(xmlr, type);
				batch.add(element.getValue());

				if (batch.size() >= batchSize) {
					result = batchProcessor.process(result, batch);
					batch = new ArrayList<T>();
				}
			}
			if (batch.size() > 0) {
				result = batchProcessor.process(result, batch);
			}

			return result;
		} catch (JAXBException | XMLStreamException e) {
			throw new XmlException(e, "Failed to batch process xml: %s", e.getMessage());
		}
	}

	/**
	 * Create an XmlInputFactory. Override if you have specific feature requirements.
	 * 
	 * @param validate
	 * @return
	 * @throws FactoryConfigurationError
	 */
	protected XMLInputFactory newXmlInputFactory(boolean validate) throws FactoryConfigurationError {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		if (!validate) {
			inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
			inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
		}
		return inputFactory;
	}

	public <T, O extends OutputStream> O writeOne(T output, O os, String encoding, boolean format) {
		try {
			marshaller(output, encoding, format).marshal(output, os);
			return os;
		} catch (JAXBException e) {
			throw new XmlException(e, "Failed to generate XML output: %s", e.getMessage());
		}
	}

	public <T, W extends Writer> W writeOne(T output, W writer, String encoding, boolean format) {
		try {
			marshaller(output, encoding, format).marshal(output, writer);
			return writer;
		} catch (JAXBException e) {
			throw new XmlException(e, "Failed to generate XML output: %s", e.getMessage());
		}
	}

	public <T, O extends OutputStream> O writeMany(Iterator<T> output, O os, String rootElement, String encoding, boolean format) {
		try {
			XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(os, encoding);
			writeStreamedObjects(output, rootElement, encoding, format, writer);
			return os;
		} catch (JAXBException | XMLStreamException e) {
			throw new XmlException(e, "Failed to generate XML output: %s", e.getMessage());
		}
	}

	public <T, W extends Writer> W writeMany(Iterator<T> output, W writer, String rootElement, String encoding, boolean format) {
		try {
			XMLStreamWriter xmlWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(writer);
			writeStreamedObjects(output, rootElement, encoding, format, xmlWriter);
			return writer;
		} catch (JAXBException | XMLStreamException e) {
			throw new XmlException(e, "Failed to generate XML output: %s", e.getMessage());
		}
	}

	public <T> String writeMany(Iterator<T> output, String rootElement, String encoding, boolean format) {
		ByteArrayOutputStream baos = writeMany(output, new ByteArrayOutputStream(), rootElement, encoding, format);
		return string(baos, encoding);
	}

	public <T> String writeOne(T output, String encoding, boolean format) {
		ByteArrayOutputStream baos = writeOne(output, new ByteArrayOutputStream(), encoding, format);
		return string(baos, encoding);
	}

	protected <T> Marshaller marshaller(T output, String encoding, boolean format) throws JAXBException, PropertyException {
		Class<?> type = output.getClass();
		JAXBContext jaxbContext = JAXBContext.newInstance(type);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, format);
		return marshaller;
	}

	protected <T> JAXBContext getJaxbContext(Class<T> type) throws JAXBException {
		JAXBContext context = contextMap.get(type);

		if (context == null) {
			try {
				context = JAXBContext.newInstance(type);
				contextMap.putIfAbsent(type, context);
			} catch (Exception e) {
				throw new XmlException(e, "Failed to create a JAXBContext for type %s", type.getName());
			}
		}

		return context;
	}

	@SuppressWarnings("unchecked")
	protected <T> void writeStreamedObjects(Iterator<T> output, String rootElement, String encoding, boolean format, XMLStreamWriter writer) throws XMLStreamException, JAXBException,
			PropertyException {
		writer.writeStartDocument(encoding, "1.0");
		writer.writeStartElement(rootElement);

		Marshaller marshaller = null;
		Class<T> type = null;
		while (output.hasNext()) {
			T object = output.next();
			if (marshaller == null) {
				type = (Class<T>) object.getClass();
				marshaller = marshaller(object, encoding, format);
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			}

			String jaxbElementName = generateJaxbElementName(type);
			JAXBElement<T> element = new JAXBElement<T>(QName.valueOf(jaxbElementName), type, object);
			marshaller.marshal(element, writer);
		}
		writer.writeEndDocument();
		writer.flush();
	}

	// This is a duplication of the logic found in {@link JAXB#_marshal}
	protected <T> String generateJaxbElementName(Class<T> type) {
		XmlRootElement r = type.getAnnotation(XmlRootElement.class);
		return r == null || r.name() == null || r.name().equalsIgnoreCase("##default") ? Introspector.decapitalize(type.getSimpleName()) : r.name();
	}

	private String string(ByteArrayOutputStream baos, String encoding) {
		try {
			return new String(baos.toByteArray(), encoding);
		} catch (UnsupportedEncodingException e) {
			throw new XmlException(e, "Failed to write xml to string, unsupported character encoding: %s", encoding);
		}
	}

}
