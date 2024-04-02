# Data Pipeline for SpaceX Launch Data 

It is a compact solution for Spacex Launch data.

### raw data:
Most important part of the project because raw data has no schema.
First of all I generate initial schema using sample set of data with https://transform.tools/json-to-scala-case-class
This tool gives initial schema for raw data. Of course initial schema doesn't fit with data when fetchs from spacex api with http client. I used circe library to parse json to object(schema). Logging helps to clean non-parsed 	 data and refactor schema. In production It would be better to save it in another table instead of logging.

	     prepared <- ZIO.fromEither(decode[List[Launches]](raw)).mapError{k =>
            //log errors
            log.error(k.getMessage)
            ParsingError(k.getMessage())
         }
After a couple of iteration final raw data schema;

        //raw data
        case class Fairings (
        reused: Option[Boolean],
        recovery_attempt: Option[Boolean],
        recovered: Option[Boolean],
        ships: Seq[String] = Seq.empty
        )
        
        case class Patch (
        small: Option[String] = None,
        large: Option[String] = None
        )
        
        case class Flickr (
        small: Seq[String] = Seq.empty,
        original: Seq[String] = Seq.empty
        )
        
        case class Reddit (
        campaign: Option[String] = None,
        launch: Option[String] = None,
        media: Option[String] = None,
        recovery: Option[String] = None
        )
        
        case class Links (
        patch: Patch,
        reddit: Reddit,
        flickr: Flickr,
        presskit: Option[String] = None,
        webcast: Option[String] = None,
        youtube_id: Option[String] = None,
        article: Option[String] = None,
        wikipedia: Option[String] = None
        )
        
        case class Failures (
        time: Option[Long] = None,
        altitude: Option[Long]   = None,
        reason: Option[String] = None
        )
        
        case class Crew (
        crew: Option[String] = None,
        role: Option[String] = None
        )
        
        case class Cores (
        core: Option[String] = None,
        flight: Option[Long] = None,
        gridfins: Option[Boolean] = None,
        legs: Option[Boolean] = None,
        reused: Option[Boolean] = None,
        landing_attempt: Option[Boolean] = None,
        landing_success: Option[Boolean] = None,
        landing_type: Option[String] = None,
        landpad: Option[String] = None
        )
        
        case class Launches (
        id : String,
        flight_number : Int,
        name : String,
        date_utc : Option[LocalDate],
        date_unix : Option[Long],
        date_local : LocalDate,
        date_precision : Option[String],
        upcoming : Option[Boolean] = None,
        success : Option[Boolean] = None,
        details : Option[String],
        rocket : String,
        launchpad : Option[String],
        static_fire_date_utc : Option[LocalDate],
        static_fire_date_unix : Option[Long],
        net : Option[Boolean],
        window : Option[Int],
        auto_update : Option[Boolean],
        tbd : Option[Boolean],
        launch_library_id : Option[String],
        fairings : Option[Fairings] = None,
        links : Option[Links],
        failures : Seq[Failures] = Seq.empty,
        crew : Seq[Crew] = Seq.empty,
        ships : Seq[String] = Seq.empty,
        capsules : Seq[String] = Seq.empty,
        payloads : Seq[String] = Seq.empty,
        cores : Seq[Cores] = Seq.empty
        )  


### validation & cleaning:
After finalize raw data ingestion cleaning phase is started. I added sample rule in pipeline;

        cleaned <- ZIO.succeed(prepared.filter(f=> f.date_local.getYear > 2010))

### trasformation:
Main strategy of transformation is star schema so I generate fact_launches and dim_payload objects as sample.

        //sink data
        case class dim_Payloads(launchId:String,payload:String) 
            .... 

        case class fact_Launches( id : String,
        flight_number : Int,
        name : String,
        date_local : LocalDate,
        success : Boolean,
        rocket : String
        )


        dim_Payloads(5eb87cdfffd86e000604b331,5eb0e4bab6c3bb0006eeb1ea)
        dim_Payloads(5eb87ce0ffd86e000604b332,5eb0e4bab6c3bb0006eeb1eb)
        dim_Payloads(5eb87ce0ffd86e000604b332,5eb0e4bab6c3bb0006eeb1ec)
        dim_Payloads(5eb87ce1ffd86e000604b333,5eb0e4bbb6c3bb0006eeb1ed)
        dim_Payloads(5eb87ce1ffd86e000604b334,5eb0e4bbb6c3bb0006eeb1ee)
        ...
        fact_Launches(5eb87cdfffd86e000604b331,8,COTS 2,2012-05-22,true,5e9d0d95eda69973a809d1ec)
        fact_Launches(5eb87ce0ffd86e000604b332,9,CRS-1,2012-10-08,true,5e9d0d95eda69973a809d1ec)
        fact_Launches(5eb87ce1ffd86e000604b333,10,CRS-2,2013-03-01,true,5e9d0d95eda69973a809d1ec)
        fact_Launches(5eb87ce1ffd86e000604b334,11,CASSIOPE,2013-09-29,true,5e9d0d95eda69973a809d1ec)
        fact_Launches(5eb87ce2ffd86e000604b335,12,SES-8,2013-12-03,true,5e9d0d95eda69973a809d1ec)

### consumed:
In this compact solution serving data as console output but in production would be better used partitioned bucket for fact table.
If date is selected as partition we could parallelize pipeline by year at orchestration.

### scheduling:
Framework gives us some useful functions to handle scheduling but in production should be optimized by data frequency and size. 

    pipe.retry(Schedule.fixed(1.seconds) && Schedule.recurs(3))

### run:

sbt run
