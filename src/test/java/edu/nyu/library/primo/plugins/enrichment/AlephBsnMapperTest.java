/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;

import com.exlibris.primo.api.common.IMappingTablesFetcher;
import com.exlibris.primo.api.common.IPrimoLogger;
import com.exlibris.primo.api.plugins.enrichment.IEnrichmentDocUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.nyu.library.datawarehouse.DataWarehouseProperties;
import edu.nyu.library.primo.plugins.test.util.EnrichmentDocUtils;
import edu.nyu.library.primo.plugins.test.util.MappingTableFetcher;
import edu.nyu.library.primo.plugins.test.util.PrimoLogger;

/**
 * @author Scot Dalton
 *
 */
public class AlephBsnMapperTest {
	private final static String propertiesFilename = 
		"./src/test/resources/META-INF/datawarehouse.properties";
	private final static String nyuAlephXmlFile = 
		"./src/test/resources/META-INF/nyu_aleph.xml";
	private IPrimoLogger primoLogger;
	private IMappingTablesFetcher mappingTableFetcher;
	private Map<String, Object> enrichmentPluginParams;
	private IEnrichmentDocUtils enrichmentDocUtils;
	private Document doc;
	private DataWarehouseProperties properties;
	private String table;
	private List<String> selections;
	private String whereColumn;
	private List<SectionTag> enrichmentSectionTags;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() throws Exception {
		enrichmentDocUtils = new EnrichmentDocUtils();
		primoLogger = new PrimoLogger();
		mappingTableFetcher = new MappingTableFetcher();
		enrichmentPluginParams = Maps.newHashMap();
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().
			parse(new File(nyuAlephXmlFile));
		File propertiesFile = new File(propertiesFilename);
		properties = new DataWarehouseProperties.Builder().build();
		if(propertiesFile.exists())
			properties = new DataWarehouseProperties.Builder(new FileReader(propertiesFile)).build();
		table = "HARVARD_PROJECT_OCLC_KEYS";
		selections = Lists.newArrayList("OCLC_MASTER");
		whereColumn = "ALEPH_BSN";
		enrichmentSectionTags = Lists.newArrayList(
			new SectionTag("addata", "oclcid"), 
			new SectionTag("search", "general"));
	}

	/**
	 * Test instatiation of the BsnToOclc enrichment plugin
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws Exception 
	 */
	@Test
	public void testNew() throws Exception {
		AlephBsnMapper abm = 
			new AlephBsnMapper(table, selections, 
				whereColumn, properties, 
					enrichmentSectionTags);
		assertNotNull(abm);
	}
	
	@Test
	public void testInit() throws Exception {
		AlephBsnMapper abm = 
			new AlephBsnMapper(table, selections, 
				whereColumn, properties, 
					enrichmentSectionTags);
		abm.init(primoLogger, mappingTableFetcher, 
			enrichmentPluginParams);
	}
	
	@Test
	public void testGetResultSet() throws Exception {
		AlephBsnMapper abm = 
			new AlephBsnMapper(table, selections, 
				whereColumn, properties, 
					enrichmentSectionTags);
		ResultSet resultSet = abm.getResultSet("001969478");
		resultSet.next();
		assertEquals("22983279", resultSet.getString(1));
		resultSet.close();
		resultSet.getStatement().getConnection().close();
	}
	
	@Test
	public void testEnrich() throws Exception {
		AlephBsnMapper abm = 
			new AlephBsnMapper(table, selections, 
				whereColumn, properties, 
					enrichmentSectionTags);
		abm.init(primoLogger, mappingTableFetcher, enrichmentPluginParams);
		assertNotNull(doc.getElementsByTagName("isbn").item(0));
		assertNull(doc.getElementsByTagName("oclcid").item(0));
		abm.enrich(doc, enrichmentDocUtils);
		assertNotNull(doc.getElementsByTagName("oclcid").item(0));
		assertEquals("22983279", 
			doc.getElementsByTagName("oclcid").item(0).getTextContent());
	}
}