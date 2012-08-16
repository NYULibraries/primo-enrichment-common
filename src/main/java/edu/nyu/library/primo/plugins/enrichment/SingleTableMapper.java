/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.w3c.dom.Document;

import com.exlibris.primo.api.plugins.enrichment.IEnrichmentDocUtils;
import com.google.common.collect.Lists;

/**
 * @author Scot Dalton
 * SingleTableMapper represents a column to column enrichment based on
 * a give element in the Primo document and a mapped column in the 
 * DataWarehouse.
 */
public class SingleTableMapper extends DataWarehouseEnrichmentPlugin {
	private SectionTag mapFromSectionTag;
	private final String sqlQuery;

	/**
	 * Protected constructor.
	 * @param mappingTableName
	 * @param mappedToColumnName
	 * @param mappedFromColumnName
	 * @param mapFromSectionTag
	 * @param dataWarehouse
	 * @param enrichmentSectionTags
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	protected SingleTableMapper(String mappingTableName, String mappedToColumnName, 
			String mappedFromColumnName, SectionTag mapFromSectionTag, 
			PropertiesConfiguration propertiesConfiguration, List<SectionTag> enrichmentSectionTags) {
		this(sqlQuery(mappingTableName, mappedToColumnName, mappedFromColumnName), 
				mapFromSectionTag, propertiesConfiguration, enrichmentSectionTags);
	}

	/**
	 * Protected constructor. Used for testing.
	 * @param sqlQuery
	 * @param dataWarehouse
	 * @param enrichmentSectionTags
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	protected SingleTableMapper(String sqlQuery, SectionTag mapFromSectionTag, 
			PropertiesConfiguration propertiesConfiguration, List<SectionTag> enrichmentSectionTags) {
		super(propertiesConfiguration, enrichmentSectionTags);
		this.sqlQuery = sqlQuery;
		this.mapFromSectionTag = mapFromSectionTag;
	}

	/**
	 * Adds mappings provided by the NYU Libraries' Aleph DataWarehouse 
	 * from Aleph BSNs to result of the first column in the plugins' 
	 * configured SQL query.  Mappings are added to the Document based on
	 * the plugins' List of SectionTags.
	 */
	@Override
	public Document enrich(Document doc, IEnrichmentDocUtils docUtils) 
			throws Exception {
		String[] mapFromTags = docUtils.
			getValuesBySectionAndTag(doc, mapFromSectionTag.section, mapFromSectionTag.tag);
		List<String> mappings = Lists.newArrayList();
		for (String mapFromTag: mapFromTags) {
			ResultSet resultSet = 
				getResultSet(mapFromTag);
			while(resultSet.next()) {
				mappings.add(resultSet.getString(1));
			}
		}
		return addEnrichmentTags(doc, docUtils, mappings);
	}
	
	/**
	 * Get Result set based on the initial SQL query and the given BSN.
	 */
	@Override
	public ResultSet getResultSet(String bsn) throws SQLException {
		return super.getResultSet(sqlQuery + bsn);
	}

	private final static String sqlQuery(String mappingTableName, 
			String mappedToColumnName,  String mappedFromColumnName) {
		if (	mappingTableName == null) 
			throw new NullPointerException(
				"No property mapping table defined.");
		if (	mappedToColumnName == null) 
			throw new NullPointerException(
				"No property mapped to column defined.");
		if (	mappedFromColumnName == null) 
			throw new NullPointerException(
				"No property mapped from column defined.");
		return "SELECT " + mappedToColumnName + " FROM " + mappingTableName + 
			" WHERE " + mappedFromColumnName + " = ";
	}
}