package ch.makery.book.view

import ch.makery.book.model.Book 
import ch.makery.book.MainApp 
import scalafx.scene.control.{TableView, TableColumn, Label, Alert, TextField}
import scalafxml.core.macros.sfxml 
import scalafx.beans.property.{StringProperty}
import ch.makery.book.util.DateUtil._
import scalafx.Includes._
import scalafx.event.ActionEvent

import scala.util.{Failure, Success}
import scalafx.scene.control.Alert.AlertType

@sfxml
class BookOverviewController(
    private val bookTable : TableView[Book],
    private val bookNameColumn : TableColumn[Book, String],
    private val bookAuthorColumn : TableColumn[Book, String],
    private val bookNameLabel : Label,
    private val bookAuthorLabel : Label,
    private val borrowedByLabel : Label,
    private val dateBorrowedLabel : Label,
    private val availabilityLabel : Label,
    private val searchField : TextField
){
    // initialize Table View display contents model
    bookTable.items = MainApp.bookData
    // initialize columns' cell values
    bookNameColumn.cellValueFactory = {_.value.bookName}
    bookAuthorColumn.cellValueFactory = {_.value.bookAuthor}

    private def showBookDetails (book : Option[Book]) = {
        book match {
            case Some(book) => 
            bookNameLabel.text     <== book.bookName
            bookAuthorLabel.text   <== book.bookAuthor
            borrowedByLabel.text   <== book.borrowedBy
            dateBorrowedLabel.text = book.dateBorrowed.value.asString
            availabilityLabel.text <== book.availability

            case None =>
            bookNameLabel.text.unbind()
            bookAuthorLabel.text.unbind()
            borrowedByLabel.text.unbind()
            availabilityLabel.text.unbind()
            bookNameLabel.text     = ""
            bookAuthorLabel.text   = ""
            borrowedByLabel.text   = ""
            dateBorrowedLabel.text = ""
            availabilityLabel.text = ""
        }
    }

    showBookDetails(None);

    bookTable.selectionModel().selectedItem.onChange(
        (a, oldValue, newValue) => showBookDetails(Option(newValue))
    )


    def handleAddBook(action : ActionEvent) = {
        val book = new Book("","")
        val okClicked = MainApp.showBookEditDialog(book);
            if (okClicked){
                book.save() match{
                    case Success(value) => 
                    MainApp.bookData += book
                    case Failure(exception) =>
                    val alert = new Alert(Alert.AlertType.Warning){
                        initOwner(MainApp.stage)
                        title       = "Failed to Save"
                        headerText  = "Database Error"
                        contentText = "Database problem filed to save changes"
                    }.showAndWait()
                }
            }
    }

    def handleEditBook(action : ActionEvent) = {
        val selectedBook = bookTable.selectionModel().selectedItem.value
        if(selectedBook != null){
            val okClicked = MainApp.showBookEditDialog(selectedBook)

            if(okClicked){
                selectedBook.save() match{
                    case Success(x) =>
                    showBookDetails(Some(selectedBook))
                    case Failure(e) =>
                    val alert = new Alert(Alert.AlertType.Warning){
                        initOwner(MainApp.stage)
                        title       = "Failed to Update"
                        headerText  = "Database Error"
                        contentText = "Database problem filed to save changes"
                    }.showAndWait()
                }
            }

        }else{
            val alert = new Alert(Alert.AlertType.Warning){
                initOwner(MainApp.stage)
                title       = "No Selection"
                headerText  = "No Book Selected"
                contentText = "Please select a book in the table."
            }.showAndWait()
        }
    }

    def handleRmvBook(action :ActionEvent) = {
        val selectedIndex = bookTable.selectionModel().selectedIndex.value
        val selectedBook = bookTable.selectionModel().selectedItem.value
        if(selectedIndex >= 0){
            selectedBook.delete() match{
                case Success(x) =>
                bookTable.items().remove(selectedIndex);
                case Failure(e) =>
                val alert = new Alert(Alert.AlertType.Warning){
                     initOwner(MainApp.stage)
                     title       = "Failed to Delete"
                     headerText  = "Database Error"
                     contentText = "Database problem filed to save changes"
                }.showAndWait()
            }
        }else{
            val alert = new Alert(Alert.AlertType.Warning){
                initOwner(MainApp.stage)
                title       = "No Selection"
                headerText  = "No Book Selected"
                contentText = "Please select a book in the table."
            }.showAndWait()
        }
    }

    def nullChecking(x : String) = x == null || x.length == 0

    def handleSearch(action :ActionEvent) = {
        if(nullChecking(searchField.text.value)){
            val alert = new Alert(Alert.AlertType.Warning){
                initOwner(MainApp.stage)
                title = "Invalid Book Name"
                headerText = "Please enter valid book name"
                contentText = "A valid book name : Alice's Adventures in Wonderland"
            }.showAndWait()
        }else{
            var exist = false
            for (x <- 0 to MainApp.bookData.size-1){
                if(searchField.text.value.toLowerCase() == MainApp.bookData(x).bookName.value.toLowerCase()){
                    val bookSearched = MainApp.bookData(x)
                    val okClicked = MainApp.showBookEditDialog(bookSearched)

                    if(okClicked){
                        bookSearched.save() match{
                            case Success(x) =>
                            showBookDetails(Some(bookSearched))
                            case Failure(e) =>
                            val alert = new Alert(Alert.AlertType.Warning){
                                initOwner(MainApp.stage)
                                title       = "Failed to Update"
                                headerText  = "Database Error"
                                contentText = "Database problem filed to save changes"
                            }.showAndWait()
                        }
                    }
                    exist = true
                }else{
                    if(x == MainApp.bookData.size-1 && exist == false){
                    val alert = new Alert(Alert.AlertType.Warning){
                        initOwner(MainApp.stage)
                        title       = "Book Not Found"
                        headerText  = "Book Not exist"
                        contentText = "The book is not exist in the system"
                        }.showAndWait()
                    }
                } 
            }
            if(MainApp.bookData.size < 1){
                val alert = new Alert(Alert.AlertType.Warning){
                    initOwner(MainApp.stage)
                    title       = "Book Not Found"
                    headerText  = "Book Not exist"
                    contentText = "You did it"
                    }.showAndWait()
            }
        }
    }
}