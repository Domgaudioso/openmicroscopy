/*
 *   $Id$
 *
 *   Copyright 2007 Glencoe Software, Inc. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 *
 */

#ifndef OMERO_CONSTANTS_ICE
#define OMERO_CONSTANTS_ICE

module omero {

  /**
   * Most client-intended constants are provided in this module.
   **/
  module constants {

    /**
     * Key in the ImplicitContext which must be filled
     * by all omero.client implementations.
     **/
    const string CLIENTUUID = "omero.client.uuid";

    /**
     * Default Ice.GC.Interval for OmeroCpp (60 seconds)
     **/
    const int GCINTERVAL = 60;

    /**
     * Default Glacier2 port. Used to define '@omero.port@' if not set.
     **/
    const int GLACIER2PORT = 4064;

    /**
     * Default Ice.MessageSizeMax (65536kb). Not strictly necessary, but helps to
     * curb memory issues. Must be set before communicator initialization.
     **/
    const int MESSAGESIZEMAX = 65536;

    /**
     * Default omero.ClientCallback.ThreadPool.Size (5).
     * Must be set before communicator initialization.
     **/
    const int CLIENTTHREADPOOLSIZE = 5;

    /**
     * Default Ice.Override.ConnectTimeout (5000). Also not strictly necessary,
     * but prevents clients being blocked by failed servers. -1 disables.
     **/
     const int CONNECTTIMEOUT = 5000;

    /**
     * Default connection string for connecting to Glacier2
     * (Ice.Default.Router). The '@omero.port@' and '@omero.host@' values will
     * be replaced by the properties with those names from the context.
     **/
    const string DEFAULTROUTER = "OMERO.Glacier2/router:ssl -p @omero.port@ -h @omero.host@";

    /**
     * Server-side names used for each of the services
     * defined in API.ice. These names can be used in
     * the ServiceFactory.getByName() and createByName()
     * methods.
     **/
    const string ADMINSERVICE     = "omero.api.IAdmin";
    const string ANALYSISSERVICE  = "omero.api.IAnalysis";
    const string CONFIGSERVICE    = "omero.api.IConfig";
    const string CONTAINERSERVICE = "omero.api.IContainer";
    const string GATEWAYSERVICE   = "omero.api.Gateway";
    const string EXPORTERSERVICE  = "omero.api.Exporter";
    const string DELETESERVICE    = "omero.api.IDelete";
    const string LDAPSERVICE      = "omero.api.ILdap";
    const string PIXELSSERVICE    = "omero.api.IPixels";
    const string PROJECTIONSERVICE= "omero.api.IProjection";
    const string QUERYSERVICE     = "omero.api.IQuery";
    const string SESSIONSERVICE   = "omero.api.ISession";
    const string SHARESERVICE     = "omero.api.IShare";
    const string TIMELINESERVICE  = "omero.api.ITimeline";
    const string TYPESSERVICE     = "omero.api.ITypes";
    const string UPDATESERVICE    = "omero.api.IUpdate";
    const string JOBHANDLE        = "omero.api.JobHandle";
    const string RAWFILESTORE     = "omero.api.RawFileStore";
    const string RAWPIXELSSTORE   = "omero.api.RawPixelsStore";
    const string RENDERINGENGINE  = "omero.api.RenderingEngine";
    const string ROISERVICE       = "omero.api.IRoi";
    const string SCRIPTSERVICE    = "omero.api.IScript";
    const string SEARCH           = "omero.api.Search";
    const string THUMBNAILSTORE   = "omero.api.ThumbnailStore";
    const string REPOSITORYINFO   = "omero.api.IRepositoryInfo";
    const string RENDERINGSETTINGS= "omero.api.IRenderingSettings";
    const string METADATASERVICE  = "omero.api.IMetadata";
    const string SHAREDRESOURCES  = "omero.grid.SharedResources";

    // User context for logging in
    const string USERNAME = "omero.user";
    const string PASSWORD = "omero.pass";
    const string GROUP    = "omero.group";
    const string EVENT    = "omero.event";
    const string AGENT    = "omero.agent";

    module cluster {
        // config string used by the ConfigRedirector
        const string REDIRECT = "omero.cluster.redirect";
    };

    /**
     * Namespaces for the [omero::api::IMetadata] interface.
     **/
    module metadata {
        const string NSINSIGHTTAGSET = "openmicroscopy.org/omero/insight/tagset";
        const string NSINSIGHTRATING = "openmicroscopy.org/omero/insight/rating";
        const string NSEDITORPROTOCOL = "openmicroscopy.org/omero/editor/protocol";
        const string NSEDITOREXPERIMENT = "openmicroscopy.org/omero/editor/experiment";
        const string NSMOVIEMPEG = "openmicroscopy.org/omero/movie/mpeg";
        const string NSMOVIEQT = "openmicroscopy.org/omero/movie/qt";
        const string NSMOVIEWMV = "openmicroscopy.org/omero/movie/wmv";
    };

    /**
     * General namespaces for <a href="http://trac.openmicroscopy.org.uk/omero/wiki/StructuredAnnotations">StructuredAnnotations</a>
     **/
    module namespaces {
        const string NSMEASUREMENT = "openmicroscopy.org/omero/measurement";
        const string NSCOMPANIONFILE = "openmicroscopy.org/omero/import/companionFile";
        const string NSEXPERIMENTERPHOTO = "openmicroscopy.org/omero/experimenter/photo";

        //
        // omero.grid.Param.namespaces in Scripts.ice
        //
        const string NSCREATED = "openmicroscopy.org/omero/scripts/results/created";
        const string NSDOWNLOAD = "openmicroscopy.org/omero/scripts/results/download";
        const string NSVIEW = "openmicroscopy.org/omero/scripts/results/view";
    };

    module jobs {

      /**
       * Used by JobHandle as the status of jobs
       **/
      const string SUBMITTED = "Submitted";
      const string RESUBMITTED = "Resubmitted";
      const string QUEUED = "Queued";
      const string REQUEUED = "Requeued";
      const string RUNNING = "Running";
      const string ERRORX = "Error"; // Can't be 'ERROR' or C++ won't compile
      const string WAITING = "Waiting";
      const string FINISHED = "Finished";
      const string CANCELLED = "Cancelled";

    };

    module projection {
      /**
       * Methodology strings
       **/
      const string MAXIMUMINTENSITYMETHODOLOGY = "MAXIMUM_INTENSITY_PROJECTION";
      const string MEANINTENSITYMETHODOLOGY = "MEAN_INTENSITY_PROJECTION";
      const string SUMINTENSITYMETHODOLOGY = "SUM_INTENSITY_PROJECTION";

      /**
       * Used by the IProjection methods to declare which projection to perform.
       **/
      enum ProjectionType {
        MAXIMUMINTENSITY,
        MEANINTENSITY,
        SUMINTENSITY
      };
    };

    module topics {
        const string PROCESSORACCEPTS = "/internal/ProcessorAccept";
        const string HEARTBEAT = "/public/HeartBeat";
    };

    module categories {
        const string PROCESSORCALLBACK = "ProcessorCallback";
        const string PROCESSCALLBACK = "ProcessCallback";
    };
  };
};

#endif