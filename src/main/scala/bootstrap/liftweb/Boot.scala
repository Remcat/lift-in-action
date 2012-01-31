package bootstrap.liftweb

import net.liftweb._
import net.liftweb.mapper.{DB,DefaultConnectionIdentifier}
import net.liftweb.http.{LiftRules, S}
import http.{LiftRules, NotFoundAsTemplate, ParsePath}
import sitemap.{SiteMap, Menu, Loc}
import util.{ NamedPF }


class Boot {
  def boot {
    DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
    LiftRules.unloadHooks.append( () => DBVendor.closeAllConnections_!() )

    S.addAround(DB.buildLoanWrapper)

    val sitemap = List(
      Menu("Home") / "index",
      Menu("Search") / "search",
      Menu("History") / "history"
    ) ::: Customer.menus
  
    // where to search snippet
    LiftRules.addToPackages("manning")

    // build sitemap
    val entries = List(Menu("Home") / "index") :::
                  Nil
    
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })
    
    LiftRules.setSiteMap(SiteMap(sitemap:_*))
    
    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    Schemifier.schemify(true, Schemifier.infoF _, Auction, Bid, Customer, Order, OrderAuction, Supplier) 
  }
  object Database extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:database/temp"),
    Props.get("db.user"),
    Props.get("db.pass"))
}
