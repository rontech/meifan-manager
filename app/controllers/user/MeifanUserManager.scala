package controllers

import play.api.mvc.Controller
import play.api.mvc._
import play.api.data._
import com.mongodb.casbah.commons.Imports._
import models._
import models.manager._
import play.api.data.Forms._
import controllers._
import play.cache.Cache
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.commons.TypeImports.ObjectId
import models.manager.Page
import models.manager.UserSearch

/**
 * Created by HZ-HAN on 14/06/17.
 */
object MeifanUserManager extends Controller {
  val pageSize = ManagerCommon.pageSize
  /**
   * create a form for search
   * use by specific condition
   */
  val userSearchForm: Form[UserSearch] = Form(
  mapping(
    "id" -> optional(text),
    "startTime" -> optional(date),
    "endTime" -> optional(date),
    "isValid" -> optional(text)
    )(UserSearch.apply)(UserSearch.unapply)
  )

  //val userBatchForm: Form[Tuple2[List[String], String]] = Form(tuple("id" -> list(text), "operate" -> text))

  /**
   * create a message form for user by ObjectId
   * @param id the ObjectId of user's record
   * @return message form for user
   */
  /*def userForm(id: ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> text,
      "nickName" -> text,
      "password" -> text,
      "sex" -> text,
      "birthDay" -> optional(date),
      "address" -> optional(mapping(
        "province" -> text,
        "city" -> optional(text),
        "region" -> optional(text)) {
        (province, city, region) => Address(province, city, region, None, "NO NEED", None, None, "No NEED")
      } {
        address => Some((address.province, address.city, address.region))
      }),
      "userPics" -> text,
      "tel" -> optional(text),
      "email" -> text,
      "optContactMethods" -> seq(mapping(
          "contMethodType" -> text,
          "accounts" -> list(number)
      )(OptContactMethod.apply)(OptContactMethod.unapply)),
      "socialScene" -> optional(text),
      "registerTime" -> longNumber,
      "userTyp" -> text,
      "userBehaviorLevel" -> text,
      "point" -> number,
      "activity" -> number,
      "permission" -> text) {
      // Binding: Create a User from the mapping result (ignore the second password and the accept field)
      (id, userId, nickName, password, sex, birthDay, address, userPics, tel, email, optContactMethods, socialScene, registerTime, userTyp, userBehaviorLevel, point, activity, permission) => User(id, userId, nickName, password, sex, birthDay, address, new ObjectId(userPics), tel, email, optContactMethods, socialScene, userTyp, userBehaviorLevel, point, activity, registerTime, permission, true)
    } // Unbinding: Create the mapping values from an existing Hacker value
    {
      user =>
        Some((user.id, user.userId, user.nickName, user.password, user.sex, user.birthDay, user.address, user.userPics.toString, user.tel, user.email, user.optContactMethods, user.socialScene, user.registerTime,
          user.userTyp, user.userBehaviorLevel, user.point, user.activity, user.permission))
    })*/

  /**
   * location to user manager homePage
   * @param p current page
   * @return
   */
  def Home(p: Int) = Redirect(routes.MeifanUserManager.list(p))

  /**
   * all users manager homePage
   * get the current page search form value from cache if exists
   * then find user by form values or no condition
   * @param page
   * @return
   */
  def list(page: Int) = Action { implicit request =>
    val offset = page * pageSize
    val cac = Cache.get(ManagerCommon.meifanUserSearch)
    var users :List[UserManager] = Nil
    var searchCondition: List[UserSearch] = Nil
    cac match {
      /*case Some(cachedValue) =>{
        println("match"+cachedValue)
        salonsApply = cachedValue.asInstanceOf[List[SalonApply]]
      }
      can't mach anything while the object is exists
      */

      case null => {
        users = UserManager.findAllUsers()

      }

      case __ =>{
        val userSearch = cac.asInstanceOf[UserSearch]
        searchCondition :::= List(userSearch)
        users = UserManager.findUserByCondition(userSearch)
      }
    }
    //val users = UserManager.findAllUsers().slice(offset,offset+ pageSize)
    val currentPage = new Page[UserManager](users.slice(offset,offset+ pageSize), page, offset, users.length)
    val searchForm = if(searchCondition.nonEmpty){userSearchForm.fill(searchCondition(0))} else userSearchForm
    Ok(views.html.user.usersManager(searchForm, currentPage)).withSession(ManagerCommon.userPage -> page.toString)
  }

  /**
   * set user to invalid
   * @param id
   * @return
   */
  def setUserInvaild(id: ObjectId) = Action { implicit request =>
    val page = request.session.get(ManagerCommon.userPage).map{page => page.toInt}getOrElse(0)
    UserManager.setUserInvalid(id)
    Home(page)
  }

  /**
   * set user to valid by user's objectId
   * @param id
   * @return
   */
  def activeUser(id: ObjectId) = Action { implicit request =>
    val page = request.session.get(ManagerCommon.userPage).map{page => page.toInt}getOrElse(0)
    UserManager.setUserVaild(id)
    Home(page)
  }

  /**
   * find user by specific condition by search form
   * @return
   */
  def getByCondition = Action { implicit request =>
    userSearchForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index("")),
        userSearch => {
          Cache.set(ManagerCommon.meifanUserSearch, userSearch)
          val page = request.session.get(ManagerCommon.userPage).map{page => page.toInt}getOrElse(0)
          val offset = page*pageSize
          val userManagers = UserManager.findUserByCondition(userSearch)
          val currentPage = Page.apply[UserManager](userManagers.slice(offset, offset + pageSize), page, offset, userManagers.length)
          Ok(views.html.user.usersManager(userSearchForm.fill(userSearch), currentPage))
        }
    )
  }

  /*def getUserInfo(id: ObjectId) = Action {
   /* User.findOneById(id).map{ user =>
      val userForms = userForm.fill(user)
      Ok(views.html.user.showUserDetail(userForms))
    }getOrElse{
      NotFound
    }*/
    TODO
  }*/

}