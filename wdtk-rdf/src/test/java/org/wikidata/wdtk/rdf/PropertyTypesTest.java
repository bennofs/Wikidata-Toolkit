package org.wikidata.wdtk.rdf;

/*
 * #%L
 * Wikidata Toolkit RDF
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
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
 * #L%
 */

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.SitesImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;

public class PropertyTypesTest {

	final DataObjectFactory factory = new DataObjectFactoryImpl();

	SitesImpl sites = new SitesImpl();

	final PropertyTypes propertyTypes = new PropertyTypes(
			"http://www.wikidata.org/w/api.php");

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetPropertyType() throws IOException, URISyntaxException {
		assertEquals(propertyTypes.getPropertyType(factory.getPropertyIdValue(
				"P1245", "base/")), DatatypeIdValue.DT_STRING);
		assertEquals(propertyTypes.getPropertyType(factory.getPropertyIdValue(
				"P10", "base/")), DatatypeIdValue.DT_COMMONS_MEDIA);

	}

	@Test
	public void testFetchPropertyType() throws IOException, URISyntaxException {
		assertEquals(propertyTypes.fetchPropertyType(factory
				.getPropertyIdValue("P10", "base/")),
				DatatypeIdValue.DT_COMMONS_MEDIA);

	}

	@Test
	public void testSetPropertyTypeFromEntityIdValue(){
		assertEquals(propertyTypes.setPropertyTypeFromEntityIdValue(factory.getPropertyIdValue("P1001", "http://www.wikidata.org/property"), factory.getItemIdValue("Q20", "http://www.wikidata.org/entity/")), DatatypeIdValue.DT_ITEM);
	}

	@Test
	public void testSetPropertyTypeFromStringValue() {
		assertEquals(propertyTypes.setPropertyTypeFromStringValue(
				factory.getPropertyIdValue("P1245",
						"http://www.wikidata.org/property"), factory
						.getStringValue("6763")),
				"http://www.wikidata.org/ontology#propertyTypeString");
	}

}