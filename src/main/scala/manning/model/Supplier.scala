package manning.model

import net.liftweb.common.{Full,Box,Empty,Failure}
import net.liftweb.sitemap.Loc._
import net.liftweb.mapper._
import scala.xml.NodeSeq

object Supplier extends Supplier with LongKeyedMetaMapper[Supplier]{
  override def dbTableName = "suppliers"
}
class Supplier extends LongKeyedMapper[Supplier] 
  with IdPK 
  with CreatedUpdated
  with OneToMany[Long, Supplier]{

  def getSingleton = Supplier
  object name extends MappedString(this, 150)
  object telephone extends MappedString(this, 30)
  object email extends MappedEmail(this, 200)
  object address extends MappedText(this)
  object openingHours extends MappedString(this, 255)
  object auctions extends MappedOneToMany(Auction, Auction.supplier, 
    OrderBy(Auction.endsAt, Descending)) 
      with Owned[Auction] 
      with Cascade[Auction] 
}
