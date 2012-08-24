/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.exlibris.primo.api.common.IMappingTablesFetcher;
import com.exlibris.primo.api.common.IPrimoLogger;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import edu.nyu.library.datawarehouse.DataWarehouse;
import edu.nyu.library.datawarehouse.DataWarehouseModule;
import edu.nyu.library.datawarehouse.DataWarehouseProperties;

/**
 * Abstract class for enriching Primo via the NYU Libraries Data Warehouse.
 * Subclasses should implement the abstract method getSqlStatement() with 
 * their specific query for simple queries or throw an 
 * UnsupportedOperationException and use the method 
 * getResultSet(String sql) for more complex needs
 * 
 * @author Scot Dalton
 * 
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
	public DataWarehouseEnrichmentPlugin(DataWarehouseProperties properties) {
		this(properties, null);
	}

	/**
	 * Public constructor.
	 * @param dataWarehouse
	 * @param enrichmentSectionTags
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Inject
	public DataWarehouseEnrichmentPlugin(DataWarehouseProperties properties,
			List<SectionTag> enrichmentSectionTags) {
		super(enrichmentSectionTags);
		AbstractModule module = new DataWarehouseModule(properties);
		Injector injector = Guice.createInjector(module);
		dataWarehouse = injector.getInstance(DataWarehouse.class);
	}

	/**
	 * Subclasses can override this method for more complex querying needs.
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	protected Entry<Connection, ResultSet> getResultSet(String sql) throws SQLException {
		return dataWarehouse.executeQuery(sql);
	}
	
	protected void closeConnection() {
		dataWarehouse.close();
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
	 * Initializes the DataWarehouseEnrichmentPlugin.  Subclasses should override as
	 * necessary.
	 */
	@Override
	public void init(IPrimoLogger primoLogger, 
			IMappingTablesFetcher tablesFetcher, 
			Map<String, Object> paramsMap) throws Exception {
		super.init(primoLogger, tablesFetcher, paramsMap);
	}
}