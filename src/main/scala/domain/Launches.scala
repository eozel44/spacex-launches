package domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

import scala.util.control.NonFatal


//source
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

object Launches {

  val formatter = DateTimeFormatter.ISO_DATE_TIME

  implicit  val dateDecoder: Decoder[LocalDate] = Decoder.decodeString.emap[LocalDate] { str =>
    try Right(LocalDate.parse(str, formatter))
    catch {
      case NonFatal(e) => Left(e.getMessage)
    }
  }



  implicit  val decoderFairings: Decoder[Fairings] = deriveDecoder[Fairings]

  implicit  val decoderPatch: Decoder[Patch] = deriveDecoder[Patch]
  implicit  val decoderFlickr: Decoder[Flickr] = deriveDecoder[Flickr]
  implicit  val decoderReddit: Decoder[Reddit] = deriveDecoder[Reddit]
  implicit  val decoderLinks: Decoder[Links] = deriveDecoder[Links]
  implicit  val decoderFailures: Decoder[Failures] = deriveDecoder[Failures]
  implicit  val decoderCrew: Decoder[Crew] = deriveDecoder[Crew]
  implicit  val decoderCores: Decoder[Cores] = deriveDecoder[Cores]
  implicit  val decoderRootInterface: Decoder[Launches] = deriveDecoder[Launches]


}