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

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import static junit.framework.Assert.*;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA. User: Patrik Date: 15.1.2013 Time: 22:26 To change this template use File | Settings |
 * File Templates.
 */
public class SQLErrorCodesParserTests {
	@Test
	public void readDefaultErrorCodes() {
		SQLErrorCodesParser.Beans b = SQLErrorCodesParser.readBeans(readDefaults());
		assertNotNull(b);
		assertEquals(10, b.beans.size());
	}

	private InputStream readDefaults() {
		return SQLErrorCodesParser.class.getClassLoader().getResourceAsStream(SQLErrorCodesFactory.SQL_ERROR_CODE_DEFAULT_PATH);
	}

	@Test
	public void parseDefaultErrorCodes() {
		Map<String, SQLErrorCodes> map = SQLErrorCodesParser.fromStream(readDefaults());
		assertNotNull(map);
		assertEquals(10, map.size());
		SQLErrorCodes sec = map.get("Derby");
		assertTrue(sec.isUseSqlStateForTranslation());
		assertTrue("Arrays equal", Arrays.equals(new String[] {"23505"},sec.getDuplicateKeyCodes()));
	}

}
