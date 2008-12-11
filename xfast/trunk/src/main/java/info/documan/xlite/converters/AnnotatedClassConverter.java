package info.documan.xlite.converters;

import info.documan.xlite.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: peter
 * Date: Feb 28, 2008
 * Time: 10:19:19 PM
 */
public class AnnotatedClassConverter implements ElementConverter {

    private SubTreeStore elementStorage;

    private Class targetClass;

    private ValueMapper valueMapper;

    private Map<QName, ElementMapper> elementMappersByName = new HashMap<QName, ElementMapper>();

    private Map<QName, ValueMapper> attributeMappers = new HashMap<QName, ValueMapper>();

    private NsContext classNamespaces;

    public AnnotatedClassConverter(Class targetClass) {
        this.targetClass = targetClass;
    }

    public SubTreeStore getElementStorage() {
        return elementStorage;
    }

    public void setElementStorage(SubTreeStore elementStorage) {
        this.elementStorage = elementStorage;
    }

    public NsContext getClassNamespaces() {
        return classNamespaces;
    }

    public void setClassNamespaces(NsContext classNamespaces) {
        this.classNamespaces = classNamespaces;
    }

    public void setValueMapper(ValueMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    public ValueMapper getValueMapper() {
        return valueMapper;
    }

    public void addElementMapper(QName qName, ElementMapper elementMapper) {
        elementMappersByName.put(qName, elementMapper);
    }

    public void addAttributeConverter(QName attributeQName, ValueMapper valueMapper) {
        attributeMappers.put(attributeQName, valueMapper);
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * This is a default ElementConverter that tries to convert all classes.
     *
     * @param type
     * @return
     */
    public boolean canConvert(Class type) {
        return targetClass.equals(type);
    }

    public Object fromElement(XMLSimpleReader reader, MappingContext mappingContext) {

        // instantiate object that maps to the current XML element
        Object currentObject = null;
        try {
            currentObject = targetClass.newInstance();
        } catch (InstantiationException e) {
            throw new XliteException("Could not instantiate class " + targetClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new XliteException("Could not instantiate class " + targetClass.getName(), e);

        }

        // XML element value
        String value = reader.getText();
        if (valueMapper != null && value.length() != 0) {
            valueMapper.setValue(currentObject, value);
        }

        // XML element attributes
        Iterator<Map.Entry<QName, String>> attributeSet = reader.getAttributeIterator();
        while (attributeSet.hasNext()) {
            Map.Entry<QName, String> entry = attributeSet.next();
            QName attrQName = entry.getKey();
            String attrValue = entry.getValue();
            // find the attribute mapper
            ValueMapper attrMapper = attributeMappers.get(attrQName);
            // if mapper exists, use it to set field to attribute value
            if (attrMapper != null && attrValue.length() != 0) {
                attrMapper.setValue(currentObject, attrValue);
            }
//            System.out.println("ATTR: " + attrQName);
        }

        // XML subelements
        QName qname;
        while (reader.moveDown()) {
            qname = reader.getName();
//          String  name = qname.getPrefix().length() == 0 ? qname.getLocalPart() : (qname.getPrefix() + ":" + qname.getLocalPart());

            // find ElementMapper for converting XML element with given name
            ElementMapper subMapper = elementMappersByName.get(qname);
            if (subMapper != null) {  // converter is found
//                System.out.println("START:" + name + " thisConverter:" + this.toString() +
//                        " subConverter:" + subMapper.elementConverter);
                subMapper.setValue(qname, currentObject, reader);
            } else {  // unknown subMapper
                if (elementStorage != null) {
                    reader.saveSubTree(elementStorage, currentObject);
                }
            }
//            String nm = "null";
//            nm = (reader.reader.getEventType() == 1 || reader.reader.getEventType() == 2) ? reader.reader.getName().getLocalPart() : "";
//            System.out.println("BEFORE moveUp: "+reader.reader.getEventType()+" "+nm);
            reader.moveUp();
        }

        return currentObject;
    }

    public void toElement(Object object, QName elementName, XMLSimpleWriter writer, MappingContext mappingContext) {

        // write a start tag
        writer.startElement(elementName);

        // write attributes
        for (QName attrName : attributeMappers.keySet()) {
            ValueMapper mapper = attributeMappers.get(attrName);
            String value = mapper.getValue(object);
            writer.addAttribute(attrName, value);
        }

        // write element's value
        if (valueMapper != null && object != null) {
            writer.addText(valueMapper.getValue(object));
        }

        // write subelements
        Map<ElementMapper, Integer> alreadyProcessed = new IdentityHashMap<ElementMapper, Integer>();
        for (QName subName : elementMappersByName.keySet()) {
            ElementMapper elementMapper = elementMappersByName.get(subName);
            if (!alreadyProcessed.containsKey(elementMapper)) {
                elementMapper.writeElement(object, subName, writer);
                alreadyProcessed.put(elementMapper, 0);
            }
        }

        // write  unknown (stored) subelements
        if (elementStorage != null) {
            writer.restoreSubTrees(elementStorage, object);
        }

        // write end tag
        writer.endElement();

    }

    public void printContents(String prefix) {
        prefix += " ";
        for (Map.Entry<QName, ValueMapper> attrEntry : attributeMappers.entrySet()) {
            System.out.println(prefix + "attribute:" + attrEntry.getKey()
                    + " field:" + attrEntry.getValue().targetField.getName() + "(" + attrEntry.getValue().targetField.getType() + ")");
        }

        for (Map.Entry<QName, ElementMapper> elementEntry : elementMappersByName.entrySet()) {
            System.out.println(prefix + "element:" + elementEntry.getKey());
        }

    }
}
