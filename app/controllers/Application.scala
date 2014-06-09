package controllers

import play.api._
import play.api.mvc._
import com.mongodb.casbah.Imports._
import play.api.mvc.ResponseHeader
import scala.Some
import play.api.mvc.SimpleResult
import scala.concurrent.ExecutionContext
import com.meifannet.framework.db.DBDelegate
import com.mongodb.casbah.gridfs.GridFS
import java.text.SimpleDateFormat
import play.api.libs.iteratee.Enumerator
import java.io.{FileInputStream, File}
import models.portal.common.Image

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getPhoto(file: ObjectId) = Action {

    import com.mongodb.casbah.Implicits._
    import ExecutionContext.Implicits.global

    val db = DBDelegate.picDB
    val gridFs = GridFS(db)
    gridFs.findOne(Map("_id" -> file)) match {
      case Some(f) => SimpleResult(
        ResponseHeader(OK, Map(
          CONTENT_LENGTH -> f.length.toString,
          CONTENT_TYPE -> f.contentType.getOrElse(BINARY),
          DATE -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US).format(f.uploadDate))),
        Enumerator.fromStream(f.inputStream))
      // TODO ? is this necessary ? Enumerator.eof

      case None => {
        val fi = new File(play.Play.application().path() + "/public/images/user/dafaultLog/portrait.png")
        var in: FileInputStream = null
        if (fi.exists) {
          in = new FileInputStream(fi)
          try {
            val bytes = Image.fileToBytes(in)
            Ok(bytes)
          } finally {
            in.close
          }
        } else {
          Ok("")
        }
      }
    }
  }

}