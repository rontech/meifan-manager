package controllers.auth

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import models.manager.admin.Admin
import play.api.mvc.Action



/**
 * Created by CCC on 14/06/12.
 */
object Admins {
  //login form for admin
  val loginForm = Form(mapping("adminId"->text,"password" -> text)(Admin.authenticate)(_.map(u =>(u.adminId,"")))
    .verifying(Messages("admin.loginErr"), result => result.isDefined))



  /**
   * login for user
   * @return add user to session
   */
 def login =  {

  }


}


















