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
    val fileEditor = FileEditorManager.getInstance(project).getSelectedTextEditor
    val psi = PsiDocumentManager.getInstance(project).getPsiFile(fileEditor.getDocument)
    val elementUnderCursor = psi.findElementAt(fileEditor.getCaretModel.getOffset)
    val constructor = findConstructorFrom(elementUnderCursor)

    constructor match {
      case Some(c: ScConstructor) => generateSourceForConstructor(c, project) 
      case None => Messages.showMessageDialog(project, "Could not resolve computation type from cursor position.", "Could Not Find Constructor", Messages.getErrorIcon)
    }
  }

  private def generateSourceForConstructor(constructor: ScConstructor, project: Project) {
    Messages.showMessageDialog(project, "Found computation type at cursor position.", "Computation Found", Messages.getInformationIcon)
  }

  private def findConstructorFrom(element: PsiElement): Option[ScConstructor] = {
    element match {
      case constructor: ScConstructor => Some(constructor)
      case null => None
      case other => findConstructorFrom(element.getParent)
    }
  }
}
