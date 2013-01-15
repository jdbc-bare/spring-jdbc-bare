/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.jdbc.support;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.*;

/**
 * Naive implementation of Spring context using JAXB and bean descriptors.
 * Allows SQLErrorCodes tests to pass and that is all we need.
 */
class SQLErrorCodesParser {

    /**
     * Read bean definitions from an InputStream
     * @param is
     * @return map of errorCodes ID to SQLErrorCodes instance
     * @throws IllegalArgumentException if anything goes wrong with reading or
     *  interpreting the stream
     */
	static Map<String,SQLErrorCodes> fromStream(InputStream is) {
		Beans bs = readBeans(is);
		Map<String,SQLErrorCodes> result = new HashMap();
		if (bs.beans == null) {
            return result;
        }
		for (Bean b : bs.beans) {
			result.put(b.id, b.asErrorCodes());
		}
		return result;
	}

	static Beans readBeans(InputStream is) {
		try {
			return JAXB.unmarshal(is,Beans.class);
		} catch (DataBindingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@XmlRootElement
	public static class Beans {
		@XmlElement(name = "bean")
		List<Bean> beans;
	}

	public static class Bean {
		@XmlElement(name = "property")
		List<Property> properties;

		@XmlAttribute(name = "id")
		String id;

		@XmlAttribute(name = "class")
		String className;

		SQLErrorCodes asErrorCodes() {
			return (SQLErrorCodes) instantiate();
		}

		Object instantiate() {
			try {
				Class<?> instanceClass = Class.forName(className);
				Object o = instanceClass.newInstance();
				BeanInfo bi = Introspector.getBeanInfo(instanceClass);
				for(Property property : properties) {
					property.apply(bi, o);
				}
				return o;
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	static class Property {
		@XmlAttribute(name = "name")
		String name;

		@XmlElement
		String value;

		@XmlElementWrapper(name = "list")
		@XmlElements({
			@XmlElement(name = "value", type = String.class),
			@XmlElement(name = "bean", type = Bean.class)
		})
		List<Object> list;

		void apply(BeanInfo bi, Object o)
				throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
			for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
				if (name.equals(pd.getName())) {
					Class<?> type = pd.getPropertyType();
					Object propValue = getValue(type);
					pd.getWriteMethod().invoke(o,propValue);
				}
			}
		}

		private Object getValue(Class<?> type) throws ClassNotFoundException {
			if (String.class.equals(type)) {
				return value;
			} else if (type.isArray()) {
				if (String.class.equals(type.getComponentType())) {
					return toArray();
				} else {
					return toArray(type.getComponentType());
				}
			} else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
				return Boolean.valueOf(value);
			} else if (Class.class.equals(type)) {
				return Class.forName(value);
			}
			throw new IllegalArgumentException("Property type "+type+" not supported");
		}

		private <T> Object toArray(Class<T> componentType) {
			List<Object> result = new ArrayList<Object>();
			for (Object def : this.list) {
				if (def instanceof Bean) {
					result.add(((Bean) def).instantiate());
				} else {
					result.add(def);
				}
			}
			T[] resultArray = (T[]) Array.newInstance(componentType, result.size());
            return result.toArray(resultArray);
		}

		String[] toArray() {
			if (list != null) {
				return list.toArray(new String[0]);
			} else {
				return value.split(",");
			}
		}
	}
}
