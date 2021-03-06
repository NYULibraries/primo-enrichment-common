/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;


import java.util.List;
import java.util.Map;


import org.w3c.dom.Document;

import com.exlibris.primo.api.common.IMappingTablesFetcher;
import com.exlibris.primo.api.common.IPrimoLogger;
import com.exlibris.primo.api.plugins.enrichment.EnrichmentPlugin;
import com.exlibris.primo.api.plugins.enrichment.IEnrichmentDocUtils;

import edu.nyu.library.primo.plugins.NyuPlugin;

/**
 * Base class for enrichment plugins.  Extends NyuPlugin
 * and implements the enrichment plugin interface provided 
 * by ExLibris.
 *
 * @author Scot Dalton
 * 
 */
public abstract class NyuEnrichmentPlugin extends NyuPlugin implements EnrichmentPlugin {
	private IMappingTablesFetcher tablesFetcher;
	private Map<String, Object> paramsMap;
	private final List<SectionTag> enrichmentSectionTags;

	/**
	 * Public constructor for NYU Enrichments.
	 */
	public NyuEnrichmentPlugin() {
		this(null);
	}
	
	/**
	 * Public constructor for NYU Enrichments.
	 */
	public NyuEnrichmentPlugin(List<SectionTag> enrichmentSectionTags) {
		super("NyuEnrichments");
		this.enrichmentSectionTags = enrichmentSectionTags;
	}
	
	/**
	 * Initializes the NyuEnrichmentPlugin.  Subclasses should override as
	 * necessary.
	 */
	@Override
	public void init(IPrimoLogger primoLogger, 
			IMappingTablesFetcher tablesFetcher, 
			Map<String, Object> paramsMap) throws Exception {
		this.tablesFetcher = tablesFetcher;
		this.paramsMap = paramsMap;
		registerLogger(primoLogger);
	}
	
	/**
	 * Logs the exception and returns false.
	 * @see com.exlibris.primo.api.plugins.enrichment.EnrichmentPlugin#
	 * shouldSkipFailedRecord(org.w3c.dom.Document, java.lang.Exception)
	 */
	@Override
	public boolean shouldSkipFailedRecord(Document doc, Exception e) {
		logError("Enrichment failed for record " + 
			doc.getElementsByTagName("recordid").item(0).getTextContent() +
			"\n" + e.getMessage(), e);
		return false;
	}
	
	/**
	 * Adds the given values to the Primo document and returns the 
	 * document.
	 * @param doc
	 * @param docUtil
	 * @param values
	 * @return
	 */
	protected Document addEnrichmentTags(Document doc, 
			IEnrichmentDocUtils docUtil, Map<SectionTag, List<String>> values) {
		String key = "enriching PNX with values list";
		startStopWatch(key);
		for(SectionTag sectionTag: getEnrichmentSectionTags())
			if (!values.isEmpty())
				docUtil.addTags(doc, sectionTag.section, sectionTag.tag, 
					values.get(sectionTag).toArray(new String[0]));
		stopStopWatch(key);
		return doc;
	}
	
	/**
	 * @return the enrichmentSectionTags
	 */
	public List<SectionTag> getEnrichmentSectionTags() {
		return enrichmentSectionTags;
	}

	/**
	 * Returns an IMappingTablesFetcher.
	 * Returns the IMappingTablesFetcher initialized with this NyuEnrichmentPlugin.
	 * @return
	 */
	protected IMappingTablesFetcher getTablesFetcher() {
		return tablesFetcher;
	}

	/**
	 * Returns the parameter map associated with this NyuEnrichmentPlugin
	 * @return
	 */
	protected Map<String, Object> getParamsMap() {
		return paramsMap;
	}
}