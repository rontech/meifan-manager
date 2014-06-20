package controllers.Stylist

import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._

/**
 * Created by CCC on 14/06/18.
 */
object MeifanStylistManager extends Controller {
  val stylistIdForm :Form[List[String]]  = Form("stylistId" -> list(text))


}
