Primo Enrichment Common
========================
Primo Enrichment plugins are hooks into the Primo normalization process that allow PNX records
to be modified.
> Just before the PNX records are stored in the database, Primo allows you to enrich the PNX 
records via the enrichment set assigned to the pipe. 
These sets include a series of modifications that Primo applies to each PNX record. 
The modifications may include one or more system-defined enrichments and/or a single enrichment 
plugin that you may have created using the old EnrichmentPlugin interface. [1]

The Primo Enrichment Common classes are shared classes that can be extended for a variety of use cases.
    
For more information, see the [API documentation](http://nyulibraries.github.com/primo-enrichment-common/apidocs)

The project leverages [Apache Maven](http://maven.apache.org/) for managing dependencies, building, packaging and generating Javadocs.

To install the package locally, run:

    $ mvn install

Enrichment plugins requires the Primo common-api library. To install the library in your local repository

    Download jar from back office server at: $primo_dev/ng/primo/home/system/publish/client/primo_common-api.jar
    $ mvn install:install-file -Dfile=primo_common-api.jar \
    -Dpackaging=jar -DgroupId=com.exlibrisgroup.primo \ 
    -DartifactId=primo_common-api -Dversion=1.0
    
The NYU Libraries uses [Capistrano](https://github.com/capistrano/capistrano) as its deploy tool. 
The deploy mechanism assumes [rvm](https://rvm.io/ "Ruby Version Manager") and 
[Ruby 1.9.3-p125](http://www.ruby-lang.org/en/news/2012/02/16/ruby-1-9-3-p125-is-released/) 
on the local deploy host but they may not be necessary. 
In order to deploy: 

    $ cap -S branch=<branch-name> [staging|production] deploy

[1] [EL Commons](http://exlibrisgroup.org/display/PrimoOI/Enrichment+Plug-In+%28new+version%29, "Enrichment Plug-In (new version)")