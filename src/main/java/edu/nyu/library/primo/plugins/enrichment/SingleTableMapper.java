/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.exlibris.primo.api.plugins.enrichment.IEnrichmentDocUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.nyu.library.datawarehouse.DataWarehouseProperties;

/**
 * SingleTableMapper represents a column to column enrichment based on
 * a give element in the Primo document and a mapped column in the 
 * DataWarehouse.
 * 
 * @author Scot Dalton
 */
public class SingleTableMapper extends DataWarehouseEnrichmentPlugin {
	private SectionTag mapFromSectionTag;
	private final String sqlQuery;

	/**
	 * Protected constructor.
	 * @param table
	 * @param selections
	 * @param whereColumn
	 * @param mapFromSectionTag
	 * @param properties
	 * @param enrichmentSectionTags
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	protected SingleTableMapper(String table, List<String> selections, 
			String whereColumn, SectionTag mapFromSectionTag, 
			DataWarehouseProperties properties, List<SectionTag> enrichmentSectionTags) {
		this(sqlQuery(table, selections, whereColumn), 
				mapFromSectionTag, properties, enrichmentSectionTags);
	}

	/**
	 * Protected constructor. Used for testing.
	 * @param sqlQuery
	 * @param properties
	 * @param enrichmentSectionTags
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	protected SingleTableMapper(String sqlQuery, SectionTag mapFromSectionTag, 
			DataWarehouseProperties properties, List<SectionTag> enrichmentSectionTags) {
		super(properties, enrichmentSectionTags);
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
		String key = "getting 'where' conditional value from PNX";
		startStopWatch(key);
		String[] mappingValues = getMappingValues(doc, docUtils);
		stopStopWatch(key);
		List<String> values = Lists.newArrayList();
		for (String mappingValue: mappingValues) {
			key = "getting result set from datawarehouse for value " + mappingValue;
			startStopWatch(key);
			ResultSet resultSet = getResultSet(mappingValue);
			stopStopWatch(key);
			key = "adding results from datawarehouse to values list";
			startStopWatch(key);
			while(resultSet.next())
				values.add(resultSet.getString(1));
			stopStopWatch(key);
		}
		key = "adding values list to SectionTag map";
		startStopWatch(key);
		Map<SectionTag, List<String>> mappings = Maps.newHashMap();
		for(SectionTag sectionTag: getEnrichmentSectionTags())
			mappings.put(sectionTag, values);
		stopStopWatch(key);
		return addEnrichmentTags(doc, docUtils, mappings);
	}
	
	/**
	 * Get Result set based on the initial SQL query and the given BSN.
	 */
	@Override
	public ResultSet getResultSet(String bsn) throws SQLException {
		return super.getResultSet(sqlQuery + bsn);
	}

	private final static String sqlQuery(String table, 
			List<String> selections,  String whereColumn) {
		if (	table == null) 
			throw new NullPointerException(
				"No property mapping table defined.");
		if (selections == null) 
			throw new NullPointerException(
				"No select columns defined.");
		if (selections.isEmpty()) 
			throw new IllegalArgumentException(
				"Select columns are empty.");
		if (	whereColumn == null) 
			throw new NullPointerException(
				"No 'WHERE' column defined.");
		return "SELECT " + Joiner.on(", ").join(selections) + " FROM " + table + 
			" WHERE " + whereColumn + " = ";
	}
	
	protected String[] getMappingValues(Document doc, IEnrichmentDocUtils docUtils) {
		return docUtils.getValuesBySectionAndTag(doc, 
			mapFromSectionTag.section, mapFromSectionTag.tag);
	}
}