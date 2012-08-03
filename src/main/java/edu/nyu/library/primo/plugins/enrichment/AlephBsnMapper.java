/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


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
			String bsnColumnName, File datawarehouseProperties, 
			List<SectionTag> enrichmentSectionTags) throws FileNotFoundException, IOException {
		super(mappingTableName, mapToColumnName, bsnColumnName, 
			mapFromSectionTag, datawarehouseProperties, enrichmentSectionTags);
	}
}