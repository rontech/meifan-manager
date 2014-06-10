package controllers

import play.api.mvc.Controller
import play.api.mvc._
import play.api.data._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import models.portal.salon._
import models.portal.manager._
import play.api.data.Forms._


/**
 * Created by HZ-HAN on 14/06/10.
 */
object MeifanSalonManager extends Controller {

  def getBasicInfo(itemType :String, salonId :ObjectId) = Action { implicit request =>
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon.map{ s =>
      Ok(views.html.salon.salonBasicInfo(s, itemType)).flashing("success" -> "%s basic information".format(s.salonName))
    }getOrElse {
      NotFound
    }

  }

  def getDetailInfo(itemType :String, salonId :ObjectId) = Action { implicit request =>
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon.map{ s =>
      Ok(views.html.salon.salonBasicInfo(s, itemType)).flashing("success" -> "%s detail information".format(s.salonName))
    }getOrElse {
      NotFound
    }
  }

  def getImageInfo(itemType :String, salonId :ObjectId) = Action { implicit request =>
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon.map{ s =>
      Ok(views.html.salon.salonBasicInfo(s, itemType)).flashing("success" -> "%s image to issue".format(s.salonName))
    }getOrElse {
      NotFound
    }
  }
}