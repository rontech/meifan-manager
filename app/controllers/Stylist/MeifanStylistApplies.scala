package controllers

import play.api.mvc.{Action, Controller}
import play.api.data.Form
import play.api.data.Forms._
import play.cache.Cache
import models.manager.stylist
import models.manager.stylist.{Stylist, MeifanStylistSearch, Page, StylistApply}
import controllers.Stylist.MeifanStylistManager
import com.mongodb.casbah.commons.Imports._
import models.manager.stylist.Page
import models.manager.stylist.MeifanStylistSearch

/**
 * Created by CCC on 14/06/18.
 */
object MeifanStylistApplies extends Controller{
//a constant for page size
  val pageSize : Int = 10
  //0表示正在申请中状态
  val meifanStylistFlagisApy = false

  //
  val meifanStylist :String = "meifanAppliedStylist"
  /**
   * The play form for search salon which has applied
   * @param userid stylist objectId or stylist accountId
   * @param nickName stylist's name
   * @param industry
   * @param  isVarified salon apply status
   */
  //  检索功能： 技师ID或者技师账号、行业类别、状态；
  /**
   * Define form for Stylist search
   */
  val StylistSearchForm :Form[MeifanStylistSearch] = Form(mapping(
    "userId" -> optional(text),
    "nickName" -> optional(text),
     "industry" -> optional(text),
    "isValid" -> optional(boolean)
  )(MeifanStylistSearch.apply)(MeifanStylistSearch.unapply)

  )
  /**
   * Define a function to a page
   * @param p the page want go
   * @return
   */
  def StylistR(p: Int) = Redirect(routes.MeifanStylistApplies.list(p))

  /**
   * Show all stylist items in a page
   * @param page the page want to go
   * @return
   */
  def list(page :Int = 0) = Action { implicit request =>
    var stylistApply :List[models.portal.stylist.Stylist] = Nil
    val cac = Cache.get( meifanStylist)

    cac match {
      /*case Some(cachedValue) =>{
        println("match"+cachedValue)
        salonsApply = cachedValue.asInstanceOf[List[SalonApply]]
      }
      can't mach anything while the object is exists
      */

      case null => {
          stylistApply = StylistApply.findAllAPStylists()
//        Cache.set(meifanStylist, stylistApply)
      }

      case __ =>{
        stylistApply = cac.asInstanceOf[List[models.portal.stylist.Stylist]]
      }
    }

    //val salonsApply :List[SalonApply] = SalonApply.findAllAPSalons(Salon.findAll.toList)
    val offset = page * pageSize
    val currentPage = new Page[models.portal.stylist.Stylist](stylistApply.slice(offset,offset+ pageSize), page, offset, stylistApply.length)

    Ok(views.html.Stylist.applyStylist(StylistSearchForm,MeifanStylistManager.stylistIdForm, currentPage)).withSession("stylistPage" -> page.toString)
  }

  /**
   * return to the preview page after in a page about
   * information while you want to back
   * @return
   */
  def stylistPage = Action { implicit request =>
    val stylistPage = request.session.get("stylistPage").map{p=>p}getOrElse{"0"}
    StylistR((stylistPage).toInt)
  }
  /*
  search stylist according to the conditions
   */
  def getByCondition = Action { implicit request =>
    //Cache.set(meifanStylist, null)
    StylistSearchForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index("")),
      meifanStylistSearch => {
        val stylistSrch :List[models.portal.stylist.Stylist]= StylistApply.findStylistByCondition(meifanStylistSearch)
        val currentPage = new Page[models.portal.stylist.Stylist](stylistSrch.slice(0,0+ pageSize), 0, 0, stylistSrch.length)
        Ok(views.html.Stylist.applyStylist(StylistSearchForm,MeifanStylistManager.stylistIdForm, currentPage))
      }
    )
  }
/*
active the stylist
 */
  def activeMeifanStylist(stylistId: ObjectId) = Action { implicit request =>
    val stylist :Option[models.portal.stylist.Stylist] = models.portal.stylist.Stylist.findOneById(stylistId)
    val page = {request.session.get("stylistPage").map{p=>p}getOrElse{"0"}}.toInt
    stylist.map{ s =>
      StylistApply.activeStylist(s)
      StylistR(page).flashing("stylistSuccess" -> "%s has been actived".format(models.portal.stylist.Stylist.findUser(s.stylistId).nickName))
    }getOrElse{
      StylistR(page)
    }

  }
/*
frozen the stylist
 */
  def frozenMeifanStylist(stylistId: ObjectId) = Action { implicit request =>
    val stylist :Option[models.portal.stylist.Stylist] = models.portal.stylist.Stylist.findOneById(stylistId)
    val page = {request.session.get("stylistPage").map{p=>p}getOrElse{"0"}}.toInt
    stylist.map{ s =>
      StylistApply.frozenStylist(s)
      StylistR(page).flashing("stylistSuccess" -> "%s has been frozen".format(models.portal.stylist.Stylist.findUser(s.stylistId).nickName))
    }getOrElse{
      StylistR(page)
    }
  }


}
