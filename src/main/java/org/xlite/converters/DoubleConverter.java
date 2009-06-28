package org.xlite.converters;

/**
 * @author peter
 */
public class DoubleConverter extends ValueConverter{

    public boolean canConvert(Class type) {
        return type.equals(double.class) || Double.class.isAssignableFrom(type);
    }

    public Object fromValue(String value, String format) {
        return Double.valueOf(value);
    }

    public String toValue(Object object, String format) {
        return ((Double) object).toString();
    }

}