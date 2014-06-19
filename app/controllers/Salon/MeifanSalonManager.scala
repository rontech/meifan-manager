package controllers

import play.api.mvc.Controller
import play.api.mvc._
import play.api.data._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import models.portal.salon._
import play.api.data.Forms._
import controllers._
import play.cache.Cache
import models.manager._


/**
 * Created by HZ-HAN on 14/06/10.
 */
object MeifanSalonManager extends Controller {
  val salonIdForm :Form[Tuple2[List[String],String]]  = Form(tuple("salonId" -> list(text),"processType" -> text))
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
    val page = request.session.get(ManagerCommon.salonPage).map{p=>p}getOrElse{"0"}
    SalonManager.deleteSalon(salonId)
    MeifanSalonApplies.Home(page.toInt)
  }

  /**
   * active salon set it's valid parameter true
   * @param salonId salon objectId
   * @return
   */
  def activeMeifanSalon(salonId :ObjectId) = Action{ implicit request =>
    val page = request.session.get(ManagerCommon.salonPage).map{p=>p}getOrElse{"0"}
    SalonManager.activeSalon(salonId)
    MeifanSalonApplies.Home(page.toInt)
  }


  def processAllSalons = Action{ implicit request =>
    println("location.......")
    salonIdForm.bindFromRequest.fold(
    errors => BadRequest(views.html.index("")),
    {

      case(salonId, processType) => {
      processType match {
        case "delete" => {
          salonId.map { id =>
            SalonManager.deleteSalon(new ObjectId(id))
          }
        }

        case "active" => {
          salonId.map{ id =>
            SalonManager.activeSalon(new ObjectId(id))
          }
        }

        case "agree" => {
          salonId.map{ id =>
            SalonApply.agreeSalonApy(new ObjectId(id))
          }
        }

        case "reject" => {
          salonId.map{ id =>
            SalonApply.agreeSalonApy(new ObjectId(id))
          }
        }
      }
        val page = request.session.get(ManagerCommon.salonPage).map{p=>p}getOrElse{"0"}
        MeifanSalonApplies.Home(page.toInt)
      }
    })
  }
}