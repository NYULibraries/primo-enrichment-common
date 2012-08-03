/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.exlibris.primo.api.common.IMappingTablesFetcher;
import com.exlibris.primo.api.common.IPrimoLogger;
import com.google.inject.Guice;
import com.google.inject.Inject;

import edu.nyu.library.datawarehouse.DataWarehouse;
import edu.nyu.library.datawarehouse.DataWarehouseModule;

/**
 * @author Scot Dalton
 * 
 * Abstract class for enriching Primo via the NYU Libraries Data Warehouse.
 * Subclasses should implement the abstract method getSqlStatement() with 
 * their specific query for simple queries or throw an 
 * UnsupportedOperationException and use the method 
 * getResultSet(String sql) for more complex needs
 */
public abstract class DataWarehouseEnrichmentPlugin extends NyuEnrichmentPlugin {
	private DataWarehouse dataWarehouse;
	
	/**
	 * Public constructor.
	 * @param dataWarehouse
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Inject
	public DataWarehouseEnrichmentPlugin(File datawarehousePropertiesFile) 
			throws FileNotFoundException, IOException {
		this(datawarehousePropertiesFile, null);
	}

	/**
	 * Public constructor.
	 * @param dataWarehouse
	 * @param enrichmentSectionTags
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Inject
	public DataWarehouseEnrichmentPlugin(File datawarehousePropertiesFile,
			List<SectionTag> enrichmentSectionTags) throws FileNotFoundException, IOException {
		super(enrichmentSectionTags);
		Properties properties = new Properties();
		if (datawarehousePropertiesFile.exists()) {
			properties.load(new FileReader(datawarehousePropertiesFile));
		} else {
			for(Entry<String,String> property : System.getenv().entrySet())
				properties.setProperty(property.getKey(), property.getValue());
		}
		this.dataWarehouse = 
			Guice.createInjector(new DataWarehouseModule(properties)).
				getInstance(DataWarehouse.class);
	}

	/**
	 * Subclasses can override this method for more complex querying needs.
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	protected ResultSet getResultSet(String sql) throws SQLException {
		return dataWarehouse.executeQuery(sql);
	}

	/**
	 * Returns the instance of DataWarehouse associated with the 
	 * DataWarehouseEnrichmentPlugin.
	 * @return
	 */
	protected DataWarehouse getDataWarehouse() {
		return dataWarehouse;
	}
	/**
	 * Initializes the NyuEnrichmentPlugin.  Subclasses should override as
	 * necessary.
	 */

	@Override
	public void init(IPrimoLogger primoLogger, 
			IMappingTablesFetcher tablesFetcher, 
			Map<String, Object> paramsMap) throws Exception {
		super.init(primoLogger, tablesFetcher, paramsMap);
		
	}
}