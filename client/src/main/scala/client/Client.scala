package client

import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.{ Path, Paths, StandardOpenOption }
import java.io.FileOutputStream
import java.util.concurrent.{ ExecutorService, Executors }
import java.util.concurrent.atomic._

/*
import org.http4s.Status
import org.http4s.Uri.RegName
import org.http4s.EntityBody
import org.http4s.{ Header, Headers }
import org.http4s.Uri.Authority
import org.http4s.{ Method, Request, Uri }
import org.http4s.util.CaseInsensitiveString.ToCaseInsensitiveStringSyntax
*/

import scalaz.concurrent.Task
import scalaz.concurrent.Task.taskInstance
import scalaz.stream.Process
import scalaz.stream.nio
import scalaz.syntax.bind.ToBindOps
import scalaz.std.list.listInstance
import scalaz.syntax.traverse.ToTraverseOps

final object Client {
  val wins = new AtomicInteger()

  val dir = Paths.get("").toAbsolutePath

  val executor: ExecutorService =
    Executors.newFixedThreadPool(20)

  def makeATonOfFiles(howMany: Int): Task[List[File]] =
    Task.delay{
      val in = Paths.get(this.dir.toString, "in").toFile
      if (in.exists) {
        if (in.isDirectory) {
          in.listFiles.toList.foreach(_.delete())
          in.delete()
        } else {
          in.delete()
        }
      }
      in.mkdir()
      println(
        s"Making $howMany files in $in")
      println(
        s"Warning we will delete everything in that folder on each run.")
      val files: List[File] = (0 to howMany).toList.par.map{i =>
        val fileName = i.toString
        val file = Paths.get(in.toPath.toString, fileName).toFile
        val fos = new FileOutputStream(file)
        fos.write(System.currentTimeMillis().toString.getBytes)
        fos.close()
        file
      }.seq.toList
      println("done")
      files
    }

  // val client: org.http4s.client.Client = org.http4s.client.blaze.SimpleHttp1Client()
  // val client: org.http4s.client.Client = org.http4s.client.blaze.PooledHttp1Client()

  def sendAllFilesToServer(
    files: List[File]
  ): Task[List[Unit]] =
    files.traverse { file =>
        val fileName = file
        lazy val fc = AsynchronousFileChannel.open(
          file.toPath(),
          StandardOpenOption.READ)
      /*
        client.fetch(
          new Request(
            Method.POST,
            new Uri(Some("http".ci), Some(new Authority(host = RegName("localhost"),
              port = Some(8080))), path = "/file"),
            headers =
              Headers.apply(
                Header("Name", fileName.toPath().getFileName.toString())),
            body =
              //Process.constant(100000).toSource.through(nio.file.chunkR(fileName.toPath()))
              //Process.constant(100000).toSource.through(nio.file.chunkR(fc))
              Process.constant(100000).toSource.through(scalaz.stream.io.fileChunkR(fileName.toPath.toString))
          )
        ){resp =>
//          fc.close()
          println(wins.incrementAndGet + " successes: "+resp)
          Task.now(resp.status)
        }
       */
      Process.constant(100000).toSource.through(nio.file.chunkR(fileName.toPath())).run.flatMap {
        case _ =>
          Task.delay {
            println(wins.incrementAndGet + " successes")
          }
      }
    }

  def main(args: Array[String]): Unit = {
    this.makeATonOfFiles(
      Int.MaxValue / 20000
    ).flatMap(sendAllFilesToServer
    ).unsafePerformSync
    println("Mission accomplished")
  }

}
