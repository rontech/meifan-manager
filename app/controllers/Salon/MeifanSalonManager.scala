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
import controllers._


/**
 * Created by HZ-HAN on 14/06/10.
 */
object MeifanSalonManager extends Controller {
  val salonIdForm :Form[List[String]]  = Form("salonId" -> list(text))
  /**
   * Get salon basic baseInfo, just return to basicInfo page with salon object
   *
   * @param salonId salon objectId
   * @return
   */
  def getBasicInfo( salonId :ObjectId) = Action { implicit request =>
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon.map{ s =>
      Ok(views.html.salon.salonBasicInfo(s)).flashing("success" -> "%s basic information".format(s.salonName))
    }getOrElse {
      NotFound
    }

  }

  /**
   * Get salon basic detailInfo, just return to detailInfo page with salon object
   * @param salonId salon objectId
   * @return
   */
  def getDetailInfo(salonId :ObjectId) = Action { implicit request =>
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon.map{ s =>
      Ok(views.html.salon.salonDetailInfo(s)).flashing("success" -> "%s detail information".format(s.salonName))
    }getOrElse {
      NotFound
    }
  }

  /**
   * Get salon pictures, just return to imageInfo page with salon object
   * @param salonId salon objectId
   * @return
   */
  def getImageInfo(salonId :ObjectId) = Action { implicit request =>
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon.map{ s =>
      Ok(views.html.salon.salonImgInfo(s)).flashing("success" -> "%s image to issue".format(s.salonName))
    }getOrElse {
      NotFound
    }
  }

  /**
   * just set salon valid false
   * @param salonId salon object id
   * @return
   */
  def deleteMeifanSalon(salonId :ObjectId) = Action { implicit request =>
    val salon: Option[Salon] = Salon.findOneById(salonId)
    val page = request.session.get("page").map{p=>p}getOrElse{"0"}
    salon.map{ s =>
      SalonManager.deleteSalon(s)
      MeifanSalonApplies.Home(page.toInt)
    }getOrElse {
      NotFound
    }
  }

  /**
   * active salon set it's valid parameter true
   * @param salonId salon objectId
   * @return
   */
  def activeMeifanSalon(salonId :ObjectId) = Action{ implicit request =>
    val salon: Option[Salon] = Salon.findOneById(salonId)
    val page = request.session.get("page").map{p=>p}getOrElse{"0"}
    salon.map{ s =>
      SalonManager.activeSalon(s)
      MeifanSalonApplies.Home(page.toInt)
    }getOrElse {
      NotFound
    }
  }
}