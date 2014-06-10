package controllers

import play.api.mvc._
import play.api.data._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import models.portal.salon._
import models.portal.manager._
import play.api.data.Forms._


/**
 * Created by PINGDOU on 14/06/09.
 */
object MeifanSalonApplies extends Controller {
  //a constant for page size
  val pageSize :Int = 10

  /**
   * The play form for search salon which has applied
   * @param id salon objectId or salon accountId
   * @param salonName salon's name
   * @param industry
   * @param  registerStarDate
   * @param  registerEndDate the date range of salon register in website
   * @param  flag salon apply status
   */
  val SalonAppliedSearchForm :Form[MeifanSalonApySearch] = Form(mapping(
    "id" -> text,
    "salonName" -> text,
    "industry" -> text,
    "registerStarDate" -> date,
    "registerEndDate" -> date,
    "flag" -> number
  )(MeifanSalonApySearch.apply)(MeifanSalonApySearch.unapply)

  )

  /**
   * Define a function to a page
   * @param p the page want go
   * @return
   */
  def Home(p: Int) = Redirect(routes.MeifanSalonApplies.list(p))

  /**
   * Show all apply salon items in a page
   * @param page the page want to go
   * @return
   */
  def list(page :Int = 0) = Action { implicit request =>
    val salonsApply :List[SalonApply] = SalonApply.findAllAPSalons(Salon.findAll.toList)
    val offset = page * pageSize
    val currentPage = new Page[SalonApply](salonsApply.slice(offset,offset+ pageSize), page, offset, salonsApply.length)
    Ok(views.html.salon.applySalons(SalonAppliedSearchForm, currentPage)).withSession("page" -> page.toString)
  }

  /**
   * Agree a apply item from salon to meifan websit
   * by manager checkout
   * @param salonId objectId of salon
   * @return
   */
  def agreeMeifanSalonApy(salonId: ObjectId) = Action { implicit request =>
    val salon :Option[Salon] = Salon.findOneById(salonId)
    val page = {request.session.get("page").map{p=>p}getOrElse{"0"}}.toInt
    salon.map{ s =>
      SalonApply.agreeSalonApy(s)
      Home(page).flashing("success" -> "%s has become a salon".format(s.salonName))
    }getOrElse{
      Home(page)
    }

  }

  /**
   * Reject a apply item from salon to meifan websit
   * by manager checkout
   * @param salonId objectId of salon
   * @return
   */
  def rejectMeifanSalonApy(salonId: ObjectId) = Action { implicit request =>
    val salon :Option[Salon] = Salon.findOneById(salonId)
    val page = {request.session.get("page").map{p=>p}getOrElse{"0"}}.toInt
    salon.map{ s =>
      SalonApply.rejectSalonApy(s)
      Home(page).flashing("success" -> "%s has denied".format(s.salonName))
    }getOrElse{
      Home(page)
    }
  }

  /**
   *
   * @param salonId
   * @param page
   * @return
   */
  def getItemDetail(salonId: ObjectId, page :Int) = Action { implicit request =>
    val salon :Option[Salon] = Salon.findOneById(salonId)
    salon.map{ s=>
      Ok(views.html.salon.appliedItemDetail(s)).withSession("page" -> page.toString)
    }getOrElse{
      Home(page)
    }
  }


  def getByCondition = Action { implicit request =>
    SalonAppliedSearchForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index("")),
      meifanSalonApySearch => {

      Home(0)
      }
    )
  }

  /**
   * return to the preview page after in a page about
   * information while you want to back
   * @return
   */
  def retrunToPrePage = Action { implicit request =>
    val page = request.session.get("page").map{p=>p}getOrElse{"0"}
    Home((page).toInt)
  }

}
