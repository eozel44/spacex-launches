import service.SpacexLaunchesService
import zio._


object Main {

  def main(args: Array[String]): Unit = {

    val pipeline = SpacexLaunchesService.dataPipeline(s"https://api.spacexdata.com/v5/launches/")
    val runtime = Runtime.default
    val result = runtime.unsafeRunSync(pipeline)
    result.map(l=>l._1.take(5).foreach(println))
    println("...")
    result.map(l=>l._2.take(5).foreach(println))

  }


}