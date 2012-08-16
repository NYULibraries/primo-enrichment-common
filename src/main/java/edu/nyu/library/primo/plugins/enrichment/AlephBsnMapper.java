/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;


/**
 * @author Scot Dalton
 *
 */
public class AlephBsnMapper extends SingleTableMapper {
	private final static SectionTag mapFromSectionTag = 
		new SectionTag("control", "sourcerecordid");

	/**
	 * 
	 * @param mappingTableName
	 * @param mapToColumnName
	 * @param bsnColumnName
	 * @param dataWarehouse
	 * @param enrichmentSectionTags
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public AlephBsnMapper(String mappingTableName, String mapToColumnName, 
			String bsnColumnName, PropertiesConfiguration propertiesConfiguration, 
			List<SectionTag> enrichmentSectionTags) {
		super(mappingTableName, mapToColumnName, bsnColumnName, 
			mapFromSectionTag, propertiesConfiguration, enrichmentSectionTags);
	}
}