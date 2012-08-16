/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.exlibris.primo.api.common.IMappingTablesFetcher;
import com.exlibris.primo.api.common.IPrimoLogger;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

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
	public DataWarehouseEnrichmentPlugin(PropertiesConfiguration propertiesConfiguration) {
		this(propertiesConfiguration, null);
	}

	/**
	 * Public constructor.
	 * @param dataWarehouse
	 * @param enrichmentSectionTags
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Inject
	public DataWarehouseEnrichmentPlugin(PropertiesConfiguration propertiesConfiguration,
			List<SectionTag> enrichmentSectionTags) {
		super(enrichmentSectionTags);
		this.logInfo("DataWarehouse file: " + propertiesConfiguration.getFileName());
		this.logInfo("DataWarehouse Properties: ");
		Iterator<String> iter = propertiesConfiguration.getKeys();
		while(iter.hasNext()) {
			String key = iter.next();
			this.logInfo("\t" + key + ": " + propertiesConfiguration.getProperty(key));
		}
		if (propertiesConfiguration.isEmpty()) {
			for(Entry<String,String> property : System.getenv().entrySet())
				propertiesConfiguration.setProperty(property.getKey(), property.getValue());
		}
		this.logInfo("Before try");
		try {
			this.logInfo("Before guice");
			this.logInfo("DataWarehouse: " + DataWarehouse.class.getCanonicalName());
			this.logInfo("DataWarehouseModule: " + DataWarehouseModule.class.getCanonicalName());
			this.logInfo("Guice: " + Guice.class.getCanonicalName());
			AbstractModule module = new DataWarehouseModule(propertiesConfiguration);
			this.logInfo("After module");
			Injector injector = Guice.createInjector(module);
			this.logInfo("After injector");
			dataWarehouse = injector.getInstance(DataWarehouse.class);
			this.logInfo("After dataWarehouse");
		} catch (Exception e) {
			this.logInfo("Caught exception");
			this.logInfo(e.getMessage());
			for(StackTraceElement trace: e.getStackTrace())
				this.logInfo(trace.toString());
		}
		this.logInfo("After guice");
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