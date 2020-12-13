package ch.makery.book.view

import ch.makery.book.model.Book
import ch.makery.book.MainApp
import scalafx.scene.control.{TextField, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import ch.makery.book.util.DateUtil._
import scalafx.event.ActionEvent

@sfxml
class BookEditDialogController(
    private val bookNameField     : TextField,
    private val bookAuthorField   : TextField,
    private val borrowedByField   : TextField,
    private val dateBorrowedField : TextField,
    private val availabilityField : TextField
){
    var         dialogStage : Stage = null
    private var _book       : Book  = null
    var         okClicked           = false
    
    def book = _book
    def book_=(x : Book){
        _book = x 

        bookNameField.text     = _book.bookName.value
        bookAuthorField.text   = _book.bookAuthor.value
        borrowedByField.text   = _book.borrowedBy.value
        borrowedByField.setPromptText("Member ID");
        availabilityField.text      = _book.availability.value
        availabilityField.setPromptText("Y/N");
        dateBorrowedField.text      = _book.dateBorrowed.value.asString
        dateBorrowedField.setPromptText("dd-mm-yyyy");
    }

    def handleOk(action :ActionEvent){
    
    if(isInputValid()){
            _book.bookName     <== bookNameField.text
            _book.bookAuthor   <== bookAuthorField.text
            _book.borrowedBy   <== borrowedByField.text
            _book.availability <== availabilityField.text
            _book.dateBorrowed.value = dateBorrowedField.text.value.parseLocalDate;

            okClicked = true;
            dialogStage.close()
        }
    }

    def handleCancel(action :ActionEvent){
        dialogStage.close()
    }

    def nullChecking(x : String) = x == null || x.length == 0

    def isInputValid() : Boolean = {
        var errorMessage = ""

        if (nullChecking(bookNameField.text.value))
          errorMessage += "Invalid Book Name!\n"
        if (nullChecking(bookAuthorField.text.value))
          errorMessage += "Invalid Book Author!\n"
        if (nullChecking(borrowedByField.text.value))
          errorMessage += "Invalid Member!\n"
        if (nullChecking(availabilityField.text.value))
          errorMessage += "Invalid Input\n"
        else{
            if (availabilityField.text.value != "Y" && availabilityField.text.value != "N"){
                errorMessage += "Invalid Input , Availability either Y or N only!\n"
            }
        }
        if (nullChecking(dateBorrowedField.text.value))
          errorMessage += "Invalid Date\n"
        else{
            if(!dateBorrowedField.text.value.isValid){
                errorMessage += "Invalid Date. Format should be in dd-mm-yyyy\n";
            }
        }

        if (errorMessage.length() == 0){
            return true;
        }else{
            val alert = new Alert(Alert.AlertType.Error){
            initOwner(dialogStage)
            title       = "Invalid Input(s) detected."
            headerText  = "Please correct the invalid input(s)."
            contentText = errorMessage
        }.showAndWait()

        return false;
        }
    }
}