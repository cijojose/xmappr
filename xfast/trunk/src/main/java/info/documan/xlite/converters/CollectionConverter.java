package info.documan.xlite.converters;

import info.documan.xlite.MappingContext;
import info.documan.xlite.XMLSimpleReader;
import info.documan.xlite.XMLSimpleWriter;
import info.documan.xlite.XliteException;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author peter
 */
public class CollectionConverter implements ElementConverter, CollectionConverting {

    public boolean canConvert(Class type) {
        return Collection.class.isAssignableFrom(type);
    }

    public Object fromElement(XMLSimpleReader reader, MappingContext mappingContext) {
//        ElementConverter converter = mappingContext.lookupElementConverter(targetType);
//        return converter.fromElement(reader, mappingContext);
        throw new XliteException("CollectionConverter.fromElement() method should not be called directly. " +
                "Rather for every object in a collection, a .fromElement() method should be called on it's assigned converter");
    }

    public void toElement(Object object, QName nodeName, XMLSimpleWriter writer, MappingContext mappingContext) {
        throw new XliteException("CollectionConverter.toElement() method should not be called directly. " +
                "Rather for every object in a collection, a .toElement() method should be called on it's assigned converter");
    }

    public Collection initializeCollection(Class targetType) {
        if (!isCollectionType(targetType)) {
            throw new XliteException("Error: Target class " + targetType.getName() + " can not be cast to java.util.Collection!");
        }
        Class<? extends Collection> concreteType = getConcreteCollectionType(targetType);
        try {
            return concreteType.newInstance();
        } catch (InstantiationException e) {
            throw new XliteException("Could not instantiate collection " + targetType.getName() + ". ", e);
        } catch (IllegalAccessException e) {
            throw new XliteException("Could not instantiate collection " + targetType.getName() + ". ", e);
        }
    }

    private Class<? extends Collection> getConcreteCollectionType(Class<? extends Collection> targetType) {
        if (targetType == List.class || targetType == Collection.class) {
            return ArrayList.class;
        }
        return targetType;
    }

    private boolean isCollectionType(Class type) {
        return Collection.class.isAssignableFrom(type);
    }

    public void addItem(Collection collection, Object object) {
        collection.add(object);
    }

    public Iterator getIterator(Collection collection) {
        return collection.iterator();
    }

}
