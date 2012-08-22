/**
 * 
 */
package edu.nyu.library.primo.plugins.enrichment;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import com.exlibris.primo.api.common.IMappingTablesFetcher;
import com.exlibris.primo.api.common.IPrimoLogger;

/**
 * Abstract class for enriching Primo via an HTTP Response.
 * Subclasses should implement enrich based on the Content response from
 * getResponse() or postResponse() with their specific logic for 
 * processing responses.
 * 
 * @author Scot Dalton
 * 
 */
public abstract class HttpEnrichmentPlugin extends NyuEnrichmentPlugin {
	
	/**
	 * Public constructor.
	 * @param enrichmentSectionTags
	 */
	public HttpEnrichmentPlugin(List<SectionTag> enrichmentSectionTags) {
		super(enrichmentSectionTags);
	}

	/**
	 * Returns the string representing the HTTP GET response content of 
	 * the given URL.
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getResponse(URL url) throws ClientProtocolException, IOException {
		return Request.Get(url.toString()).execute().returnContent().asString();
	}

	/**
	 * Returns the string representing the HTTP POST response content of 
	 * the given URL.
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String postResponse(URL url) throws ClientProtocolException, IOException {
		return Request.Post(url.toString()).execute().returnContent().asString();
	}

	/**
	 * Initializes the HttpEnrichmentPlugin.  Subclasses should override as
	 * necessary.
	 */
	@Override
	public void init(IPrimoLogger primoLogger, 
			IMappingTablesFetcher tablesFetcher, 
			Map<String, Object> paramsMap) throws Exception {
		super.init(primoLogger, tablesFetcher, paramsMap);
	}
}