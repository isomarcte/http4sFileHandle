Demonstrates the file handle resource bug with `http4s` 0.14.2a.

Each folder `client` and `server` is an `sbt` project.

# Running the Server #

Ther server will write files sent to it into an `out` folder which it creates relative to the path where you start `sbt`, so usually `server/out`.

**Beware** the server will attempt to delete any `out` folder that is already there assuming it is from another run.

# Running the Client #

The client will create about 100,000 files in a folder called `in` relative to the working directory you start `sbt` from, so normally `client`. These files will contain the `System.currentTimeMillis()` just to give them some bytes.

**Beware** if there is already an `in` folder the `client` will assume it is from another run and attempt to delete it.

After this is done it attempts to connect to the server on `localhost:8080` over `http`.

# What will happen #

The default setup sends the files from the client using the `PooledHttp1Client`, although the behavior is the same with the `SimpleHttp1Client`. It uses a `Process.constant(100000).toSource.through(nio.file.chunkR(fileName.toPath()))` to represent the `EntityBody`.

This will fail, for me at least, 100% of the time with the system running out of file handles.

You will seen another `Process.constant(100000).toSource.through(nio.file.chunkR(fc))` commented out right below the `Path` based version. This version uses the `lazy val fc` instead, which is a `java.nio.file.channels.AsynchronousFileChannel`. You will also need to uncommment the `fc.close()` line in the response handler. This works for me 100% of the time with no errors, with either client.
