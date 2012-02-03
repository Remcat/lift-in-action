package bootstrap.liftweb

import net.liftweb.common._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import net.liftweb.mapper.{DB,Schemifier,DefaultConnectionIdentifier,StandardDBVendor,MapperRules}

import manning.model.{Auction,Supplier,Customer,Bid,Order,OrderAuction}

class Boot {
  def boot {
    // handle JNDI not being avalible
    if (!DB.jndiJdbcConnAvailable_?){
      //logger.warn("No JNDI configured - making a direct application connection") 
      DB.defineConnectionManager(DefaultConnectionIdentifier, Database)
      // make sure cyote unloads database connections before shutting down
      LiftRules.unloadHooks.append(() => Database.closeAllConnections_!()) 
    }

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
