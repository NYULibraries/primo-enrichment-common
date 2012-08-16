/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import java.util.List;

import edu.nyu.library.datawarehouse.DataWarehouseProperties;


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
			String bsnColumnName, DataWarehouseProperties properties, 
			List<SectionTag> enrichmentSectionTags) {
		super(mappingTableName, mapToColumnName, bsnColumnName, 
			mapFromSectionTag, properties, enrichmentSectionTags);
	}
}