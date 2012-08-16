/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileReader;
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
public class SingleTableMapperTest {
	private final static String datawarehousePropertiesFilename = 
		"./src/test/resources/META-INF/datawarehouse.properties";
	private final static String nyuAlephXmlFile = 
		"./src/test/resources/META-INF/nyu_aleph.xml";
	private IPrimoLogger primoLogger;
	private IMappingTablesFetcher mappingTableFetcher;
	private Map<String, Object> enrichmentPluginParams;
	private IEnrichmentDocUtils enrichmentDocUtils;
	private Document doc;
	private DataWarehouseProperties properties;
	private String mappingTableName;
	private String mapToColumnName;
	private String mapFromColumnName;
	private SectionTag mapFromSectionTag;
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
		properties = 
			new DataWarehouseProperties.Builder(new FileReader(datawarehousePropertiesFilename)).build();
		mappingTableName = "HARVARD_PROJECT_OCLC_KEYS";
		mapToColumnName = "OCLC_MASTER";
		mapFromColumnName = "ALEPH_BSN";
		mapFromSectionTag = new SectionTag("control", "sourcerecordid");
		enrichmentSectionTags = Lists.newArrayList(
			new SectionTag("addata", "oclcid"), 
			new SectionTag("search", "oclc"));
	}

	/**
	 * Test instatiation of the BsnToOclc enrichment plugin
	 * @throws Exception 
	 */
	@Test
	public void testNew() throws Exception {
		SingleTableMapper stm = 
			new SingleTableMapper(mappingTableName, mapToColumnName, 
				mapFromColumnName, mapFromSectionTag, properties, 
					enrichmentSectionTags);
		assertNotNull(stm);
	}
	
	@Test
	public void testInit() throws Exception {
		SingleTableMapper stm = 
			new SingleTableMapper(mappingTableName, mapToColumnName, 
				mapFromColumnName, mapFromSectionTag, properties, 
					enrichmentSectionTags);
		stm.init(primoLogger, mappingTableFetcher, 
			enrichmentPluginParams);
	}
	
	@Test
	public void testGetResultSet() throws Exception {
		SingleTableMapper stm = 
			new SingleTableMapper(mappingTableName, mapToColumnName, 
				mapFromColumnName, mapFromSectionTag, properties, 
					enrichmentSectionTags);
		ResultSet resultSet = stm.getResultSet("001969478");
		resultSet.next();
		assertEquals("22983279", resultSet.getString(1));
	}
	
	@Test
	public void testEnrich() throws Exception {
		SingleTableMapper stm = 
			new SingleTableMapper(mappingTableName, mapToColumnName, 
				mapFromColumnName, mapFromSectionTag, properties, 
					enrichmentSectionTags);
		stm.init(primoLogger, mappingTableFetcher, enrichmentPluginParams);
		assertNotNull(doc.getElementsByTagName("isbn").item(0));
		assertNull(doc.getElementsByTagName("oclcid").item(0));
		stm.enrich(doc, enrichmentDocUtils);
		assertNotNull(doc.getElementsByTagName("oclcid").item(0));
		assertEquals("22983279", 
			doc.getElementsByTagName("oclcid").item(0).getTextContent());
	}
}