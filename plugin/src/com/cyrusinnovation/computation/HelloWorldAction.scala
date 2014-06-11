package com.cyrusinnovation.computation

import com.intellij.openapi.actionSystem.{AnActionEvent, PlatformDataKeys, AnAction}
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class HelloWorldAction extends AnAction("Text _Boxes") {
  def actionPerformed(event: AnActionEvent) {
    val project: Project = event.getData(PlatformDataKeys.PROJECT_CONTEXT)
    val txt: String = Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon)
    Messages.showMessageDialog(project, s"Hello, $txt!\n I am glad to see you.", "Information", Messages.getInformationIcon)
  }
}
