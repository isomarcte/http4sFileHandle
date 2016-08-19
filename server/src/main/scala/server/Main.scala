package server

import org.http4s.server.{ Server, ServerApp }
import org.http4s.server.blaze.BlazeBuilder
import scalaz.concurrent.Task


object Main extends ServerApp {

  def server(args: List[String]): Task[Server] =
    BlazeBuilder.bindHttp(8080)
    .mountService(
      Service.service, "/").start


}
