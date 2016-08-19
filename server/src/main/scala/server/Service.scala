package server

import scala.concurrent.ExecutionContext

import java.nio.file.Paths
import org.http4s._
import org.http4s.dsl._
import org.http4s.util.CaseInsensitiveString.ToCaseInsensitiveStringSyntax
import org.http4s.server._

import scalaz.stream.io
import scalaz.concurrent.Task

object Service {

  val dir = Paths.get("")

  def clearOutDir: Task[Unit] =
    Task.delay{
      val out = Paths.get(this.dir.toString, "out").toFile()
      if (out.exists) {
        if (out.isDirectory) {
          out.listFiles.toList.foreach(_.delete())
          out.delete()
        } else {
          out.delete()
        }
      }
      out.mkdir()
    }

  def service(
    implicit executionContext: ExecutionContext = ExecutionContext.global): HttpService = {
    this.clearOutDir.unsafePerformSync
    Router("" -> rootService)
  }

  def rootService(implicit executionContext: ExecutionContext) = HttpService {
    case req @ POST -> Root / "file" =>
      val remoteFile =
        req.headers.get("Name".ci).map(_.value).getOrElse(throw new AssertionError())
      val name = Paths.get(this.dir.toString(), "out", remoteFile)
      if (name.toFile().getParentFile.exists() == false) {
        name.toFile().getParentFile.mkdirs()
      }
      req.body.to(io.fileChunkW(name.toString, 100000, false)).run.flatMap(_ =>
        Ok(s"Upload of $name complete"))
    case req =>
      println(req)
      BadRequest()
  }

}
