package controllers

import play.api.mvc._
import play.api.data._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import models.portal.salon._
import models.manager._
import play.api.data.Forms._
import play.cache.Cache


/**
 * Created by PINGDOU on 14/06/09.
 */
object MeifanSalonApplies extends Controller {

  val pageSize = ManagerCommon.pageSize

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
    "id" -> optional(text),
    "salonName" -> optional(text),
    "industry" -> optional(text),
    "registerStarDate" -> optional(date),
    "registerEndDate" -> optional(date),
    "flag" -> optional(number)
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
    var salonsApply :List[SalonApply] = Nil
    val cac = Cache.get(ManagerCommon.meifanSalonSearch)
    var salonSearchForms :List[MeifanSalonApySearch] = Nil
    cac match {
      /*case Some(cachedValue) =>{
        println("match"+cachedValue)
        salonsApply = cachedValue.asInstanceOf[List[SalonApply]]
      }
      can't mach anything while the object is exists
      */

      case null => {
        salonsApply = SalonApply.findAllAPSalons(Salon.findAll.toList, ManagerCommon.meifanSalonFlagisApy)
      }

      case __ =>{
       val salonSearch = cac.asInstanceOf[MeifanSalonApySearch]
        salonsApply = SalonApply.findSalonApyByCondition(salonSearch)
        salonSearchForms :::= List(salonSearch)
      }
    }
    //val salonsApply :List[SalonApply] = SalonApply.findAllAPSalons(salon.findAll.toList)
    val offset = page * pageSize
    val forms = if(salonSearchForms.nonEmpty){ SalonAppliedSearchForm.fill(salonSearchForms(0)) } else SalonAppliedSearchForm
    val currentPage = new Page[SalonApply](salonsApply.slice(offset,offset+ pageSize), page, offset, salonsApply.length)
    Ok(views.html.salon.applySalons(forms, MeifanSalonManager.salonIdForm, currentPage)).withSession(ManagerCommon.salonPage -> page.toString)
  }

  /**
   * Agree a apply item from salon to meifan websit
   * by manager checkout
   * @param salonId objectId of salon
   * @return
   */
  def agreeMeifanSalonApy(salonId: ObjectId) = Action { implicit request =>
    val salonName = SalonApply.agreeSalonApy(salonId)
    val page = {request.session.get(ManagerCommon.salonPage).map{p=>p}getOrElse{"0"}}.toInt
    salonName.map{ name =>
      Home(page).flashing("success" -> "%s has become a salon".format(name))
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
    val salonName = SalonApply.rejectSalonApy(salonId)
    val page = {request.session.get(ManagerCommon.salonPage).map{p=>p}getOrElse{"0"}}.toInt
    salonName.map{ name =>
      Home(page).flashing("success" -> "%s has denied".format(name))
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
      Ok(views.html.salon.appliedItemDetail(s)).withSession(ManagerCommon.salonPage -> page.toString)
    }getOrElse{
      Home(page)
    }
  }


  def getByCondition = Action { implicit request =>
    SalonAppliedSearchForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index("")),
      meifanSalonApySearch => {
        Cache.set(ManagerCommon.meifanSalonSearch, meifanSalonApySearch)
        val page = request.session.get(ManagerCommon.salonPage).map{page => page.toInt}getOrElse(0)
        val offset = page*pageSize
        val Salons :List[SalonApply]= SalonApply.findSalonApyByCondition(meifanSalonApySearch)
        val currentPage = Page.apply[SalonApply](Salons.slice(offset, offset + pageSize), page, offset, Salons.length)
        Ok(views.html.salon.applySalons(SalonAppliedSearchForm.fill(meifanSalonApySearch), MeifanSalonManager.salonIdForm, currentPage)).withSession("page" -> page.toString)
      }
    )
  }

  /**
   * return to the preview page after in a page about
   * information while you want to back
   * @return
   */
  def retrunToPrePage = Action { implicit request =>
    val page = request.session.get(ManagerCommon.salonPage).map{p=>p}getOrElse{"0"}
    Home((page).toInt)
  }

}
