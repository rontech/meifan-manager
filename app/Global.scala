import play.api._
import org.bson.types.ObjectId
import java.util.Date
import java.io.File
import play.api.mvc.Results._
import play.api.mvc._
import scala.concurrent.Future

import models.portal.advert._ 
import models.portal.blog._ 
import models.portal.common._ 
import models.portal.coupon._ 
import models.portal.industry._ 
import models.portal.info._ 
import models.portal.mail._ 
import models.portal.menu._ 
import models.portal.question._ 
import models.portal.relation._ 
import models.portal.reservation._ 
import models.portal.review._ 
import models.portal.salon._ 
import models.portal.search._ 
import models.portal.service._ 
import models.portal.style._ 
import models.portal.stylist._ 
import models.portal.user._ 


object Global extends GlobalSettings {

  /**
   *
   * @param app
   * @return
   */
  override def onStart(app: Application) {
    println("jie-zhang:" + play.Play.application().path())
  }

}

