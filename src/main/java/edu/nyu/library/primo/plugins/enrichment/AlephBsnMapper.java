/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import java.io.FileNotFoundException;
import java.io.IOException;
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
	 * @param table
	 * @param selections
	 * @param bsnColumnName
	 * @param dataWarehouse
	 * @param enrichmentSectionTags
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public AlephBsnMapper(String table, List<String> selections, 
			String bsnColumnName, DataWarehouseProperties properties, 
			List<SectionTag> enrichmentSectionTags) {
		super(table, selections, bsnColumnName, 
			mapFromSectionTag, properties, enrichmentSectionTags);
	}
}