package service

import domain.{HttpError, Launches, ParsingError, dim_Payloads, fact_Launches}
import io.circe.parser._
import scalaj.http.{Http, HttpResponse}
import zio._
import zio.duration._
import zio.logging.log


object SpacexLaunchesService {

def dataPipeline(url:String) = {

    val pipe  = for {

      // ingest with api
      raw <- ingestApi(Http(s"$url").asString)

      // extract
      prepared <- ZIO.fromEither(decode[List[Launches]](raw)).mapError{k =>
            //log errors
            log.error(k.getMessage)
            ParsingError(k.getMessage())
      }

      // cleaning
      cleaned <- ZIO.succeed(prepared.filter(f=> f.date_local.getYear > 2010))

      // transform
      transformed<- ZIO.succeed(cleaned.map{ l=>

        (
         l.payloads.map(p=> dim_Payloads(l.id,p)),

        fact_Launches(l.id,
                        l.flight_number,
                        l.name,
                        l.date_local,
                        // data correction
                        l.success match {
                        case Some(true) => true
                        case _ => false
                        },
                        l.rocket)
        )
      })


      // serve
    } yield {
      (transformed.flatMap(_._1),
      transformed.map(_._2))

    }

    //schedule pipeline
    pipe.retry(Schedule.fixed(1.seconds) && Schedule.recurs(3))

  }

private def ingestApi(response: HttpResponse[String]) = {
    val r = if (response.isError) {
      Left(HttpError(response.code))
    } else {
      Right(response.body)
    }
    ZIO.fromEither(r)
  }

}
