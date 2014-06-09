package controllers

import play.api.mvc._
import play.api.data._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import models.portal.salon._
import models.portal.manager._
import play.api.data.Forms._
import models.portal.manager.Page
import models.portal.manager.MeifanSalonApySearch


/**
 * Created by HZ-HAN on 14/06/09.
 */
object MeifanSalonApplies extends Controller {
  val pageSize :Int = 10

  val SalonAppliedForm :Form[MeifanSalonApySearch] = Form(mapping(
    "id" -> txt,
    "salonName" -> txt,
    "industry" -> txt,
    "registerStarDate" -> date,
    "registerEndDate" -> date,
    "flag" -> number
  )(MeifanSalonApySearch.apply)(MeifanSalonApySearch.unapply)

  )

  def Home(p: Int) = Redirect(routes.MeifanSalonApplies.list(p))

  def list(page :Int = 0) = Action { implicit request =>
    val salonsApply :List[SalonApply] = SalonApply.findAllAPSalons(Salon.findAll.toList)
    val offset = page * pageSize
    val currentPage = new Page[SalonApply](salonsApply.slice(offset,offset+ pageSize), page, offset, salonsApply.length)
    Ok(views.html.salon.applySalons(SalonAppliedForm, currentPage))
  }

  def agreeMeifanSalonApy(salonId: ObjectId, page: Int) = Action { implicit request =>
    val salon :Option[Salon] = Salon.findOneById(salonId)
    salon.map{ s =>
      SalonApply.agreeSalonApy(s)
      Home(page).flashing("success" -> "%s has become a salon".format(s.salonName))
    }getOrElse{
      Home(page)
    }

  }

  def rejectMeifanSalonApy(salonId: ObjectId, page: Int) = Action { implicit request =>
    val salon :Option[Salon] = Salon.findOneById(salonId)
    salon.map{ s =>
      SalonApply.rejectSalonApy(s)
      Home(page).flashing("success" -> "%s has denied".format(s.salonName))
    }getOrElse{
      Home(page)
    }
  }

  def getItemDetail(salonId: ObjectId, page :Int) = Action { implicit request =>
    val salon :Option[Salon] = Salon.findOneById(salonId)
    salon.map{ s=>
      Ok(views.html.salon.appliedItemDetail(s)).withSession("page" -> page.toString)
    }getOrElse{
      Home(page)
    }
  }

  def getByCondition = Action { implicit request =>
    SalonAppliedForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index()),
      meifanSalonApySearch => {


      }
    )
  }

}
