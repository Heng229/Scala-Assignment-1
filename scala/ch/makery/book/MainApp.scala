package ch.makery.book

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene 
import scalafx.Includes._ 
import scalafxml.core.{NoDependencyResolver, FXMLView, FXMLLoader}
import javafx.{scene => jfxs}

import scalafx.collections.ObservableBuffer
import ch.makery.book.model.Book
import ch.makery.book.view.BookEditDialogController
import scalafx.stage.{Stage, Modality}

import ch.makery.book.util.Database
import scalafx.scene.image.Image

object MainApp extends JFXApp{

    if (Database.hasDBInitialize == false){
        Database.setupDB()
    }

    val bookData = new ObservableBuffer[Book]()

    /*bookData += new Book("Alice Wonderland","Lewis Carroll")
    bookData += new Book("Harry Potter","J.K. Rowling")*/

    bookData ++= Book.getAllBooks

    val rootResource = getClass.getResourceAsStream("view/RootLayout.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(rootResource);
    val roots = loader.getRoot[jfxs.layout.BorderPane]

    stage = new PrimaryStage{
        title = "Library Management System"
        icons += new Image(getClass().getResourceAsStream("images/icon.png"))
        scene = new Scene{
            root = roots
        }
    }

    def showBookOverview() = {
        val resource = getClass.getResourceAsStream("view/BookOverview.fxml")
        val loader = new FXMLLoader(null,NoDependencyResolver)
        loader.load(resource);
        val roots = loader.getRoot[jfxs.layout.AnchorPane]
        this.roots.setCenter(roots)
    }

    def showBookEditDialog(book : Book): Boolean = {
        val resource = getClass.getResourceAsStream("view/BookEditDialog.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2 = loader.getRoot[jfxs.Parent]
        val control = loader.getController[BookEditDialogController#Controller]

        val dialog = new Stage(){
            title = "Update/Add Book Information"
            initModality(Modality.APPLICATION_MODAL)
            icons += new Image(getClass().getResourceAsStream("images/magnifiericon.png"))
            initOwner(stage)
            scene = new Scene {
                root = roots2
            }
        }
        control.dialogStage = dialog
        control.book = book
        dialog.showAndWait()
        control.okClicked
    }

    showBookOverview()
}