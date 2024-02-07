package com.legyver.logmire.ui.util;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PackageAbbreviatorTest {
	PackageAbbreviator abbreviator = PackageAbbreviator.INSTANCE;

	@Test
	public void singleComponentPackage() {
		assertEquals("j", abbreviator.createUniqueAbbreviation("java"));
		assertEquals("j", abbreviator.createUniqueAbbreviation("java"));//check again to make sure a second call does not create a new abbreviation
		assertEquals("j", abbreviator.createUniqueAbbreviation("java."));
		assertEquals("jx", abbreviator.createUniqueAbbreviation("javax"));
		assertEquals("jx", abbreviator.createUniqueAbbreviation("javax."));
		assertEquals("jfx", abbreviator.createUniqueAbbreviation("javafx"));
		assertEquals("jfx", abbreviator.createUniqueAbbreviation("javafx."));
		assertEquals("s", abbreviator.createUniqueAbbreviation("sun"));
		assertEquals("s", abbreviator.createUniqueAbbreviation("sun."));
	}

	@Test
	public void multiComponentPackage() {
		assertEquals("c.o", abbreviator.createUniqueAbbreviation("com.oracle"));
		assertEquals("c.o", abbreviator.createUniqueAbbreviation("com.oracle"));//check again to make sure a second call does not create a new abbreviation
		assertEquals("c.o", abbreviator.createUniqueAbbreviation("com.oracle."));
		assertEquals("o.s", abbreviator.createUniqueAbbreviation("org.springframework"));
		assertEquals("o.s", abbreviator.createUniqueAbbreviation("org.springframework."));
		assertEquals("o.j", abbreviator.createUniqueAbbreviation("org.jboss"));
		assertEquals("o.j", abbreviator.createUniqueAbbreviation("org.jboss."));
		assertEquals("o.a", abbreviator.createUniqueAbbreviation("org.apache"));
		assertEquals("o.a", abbreviator.createUniqueAbbreviation("org.apache."));
	}


}
