package ch.makery.book.model

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}
import java.time.LocalDate;
import ch.makery.book.util.Database
import ch.makery.book.util.DateUtil._
import scalikejdbc._
import scala.util.{Try, Success, Failure}

class Book (val bookNameS : String,val bookAuthorS : String ) extends Database{
  def this()     = this(null, null)
  var bookName      = new StringProperty(bookNameS)
  var bookAuthor    = new StringProperty(bookAuthorS) 
  var borrowedBy    = new StringProperty("Member 1")
  var dateBorrowed  = ObjectProperty[LocalDate](LocalDate.of(2020, 2, 21))
  var availability  = new StringProperty("Y")

  def save() : Try[Int] = {
    if(!(isExist)){
      Try(DB autoCommit{ implicit session =>
        sql"""
          insert into book (bookName,bookAuthor,borrowedBy,dateBorrowed,availability) values(${bookName.value},${bookAuthor.value},${borrowedBy.value},${dateBorrowed.value.asString},${availability.value})
          """.update.apply()
      })
    }else{
      Try(DB autoCommit{ implicit session => 
        sql"""
          update book set
          bookName = ${bookName.value},
          bookAuthor = ${bookAuthor.value},
          borrowedBy = ${borrowedBy.value},
          dateBorrowed = ${dateBorrowed.value.asString},
          availability = ${availability.value}
          where bookName = ${bookName.value}
        """.update.apply()
      })
    }
  }

  def delete() : Try[Int] = {
    if(isExist){
      Try(DB autoCommit{ implicit session => 
        sql"""
          delete from book where bookName = ${bookName.value}
        """.update.apply()
      })
    }else
       throw new Exception("Book not Exists in the Database.")
  }

  def isExist : Boolean = {
    DB readOnly {implicit session => 
      sql"""
       select * from book
       where bookName = ${bookName.value}
       """.map(rs => rs.string("bookName")).single.apply()
    }match{
      case Some(x) => true
      case None => false
    }
  }
}

object Book extends Database{
  def apply(
            bookNameS : String,
            bookAuthorS : String,
            borrowedByS : String,
            dateBorrowedS : String,
            availabilityS : String
  ) : Book = {
    new Book(bookNameS, bookAuthorS){
      borrowedBy.value = borrowedByS
      dateBorrowed.value = dateBorrowedS.parseLocalDate
      availability.value = availabilityS
    }
  }

  def initializeTable() = {
    DB autoCommit{ implicit session => 
      sql"""
       create table book(
         bookName varchar(100),
         bookAuthor varchar(100),
         borrowedBy varchar(100),
         dateBorrowed varchar(11),
         availability char(1)
       )
      """.execute.apply()
    }
  }

  def getAllBooks : List[Book] = {
    DB readOnly{ implicit session => 
        sql"select * from book".map(rs => Book(rs.string("bookName"),
        rs.string("bookAuthor"),
        rs.string("borrowedBy"),
        rs.string("dateBorrowed"),
        rs.string("availability")
        )).list.apply()
    }
  }
}