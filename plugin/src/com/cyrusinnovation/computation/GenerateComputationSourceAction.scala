package com.cyrusinnovation.computation

import com.intellij.openapi.actionSystem._
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.{PsiDocumentManager, PsiElement}
import org.jetbrains.plugins.scala.lang.psi.api.base.ScConstructor

class GenerateComputationSourceAction extends AnAction("Generate _Source") {
  def actionPerformed(event: AnActionEvent) {
    val project = event.getProject

    findFirstParentConstructor(findElementUnderCursor(project)) match {
      case Some(constructor: ScConstructor) => generateSourceForConstructor(constructor, project)
      case None => constructorNotFound(project)
    }
  }

  private def findElementUnderCursor(project: Project): PsiElement = {
    val fileEditor = FileEditorManager.getInstance(project).getSelectedTextEditor
    val psi = PsiDocumentManager.getInstance(project).getPsiFile(fileEditor.getDocument)
    psi.findElementAt(fileEditor.getCaretModel.getOffset)
  }

  private def findFirstParentConstructor(element: PsiElement): Option[ScConstructor] = {
    Option(element).fold(Option.empty[ScConstructor]) {
      case constructor: ScConstructor => Some(constructor)
      case other                      => findFirstParentConstructor(element.getParent)
    }
  }

  private def constructorNotFound(project: Project) {
    Messages.showMessageDialog(project, "Could not resolve computation type from cursor position.", "Could Not Find Constructor", Messages.getErrorIcon)
  }

  private def generateSourceForConstructor(constructor: ScConstructor, project: Project) {
    Messages.showMessageDialog(project, "Found computation type at cursor position.", "Computation Found", Messages.getInformationIcon)
  }
}
