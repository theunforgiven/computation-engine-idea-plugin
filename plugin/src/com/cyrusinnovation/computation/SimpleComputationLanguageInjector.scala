package com.cyrusinnovation.computation

import com.intellij.lang.Language
import com.intellij.openapi.util.TextRange
import com.intellij.psi.{InjectedLanguagePlaces, LanguageInjector, PsiLanguageInjectionHost}
import org.jetbrains.plugins.scala.lang.psi.api.base.ScLiteral
import org.jetbrains.plugins.scala.lang.psi.api.expr.{ScInfixExpr, ScMethodCall}

class SimpleComputationLanguageInjector extends LanguageInjector with PsiHelpers {
  private lazy val scala = Language.findLanguageByID("Scala")

  override def getLanguagesToInject(host: PsiLanguageInjectionHost, injectionPlacesRegistrar: InjectedLanguagePlaces): Unit = {
    host match {
      case literal: ScLiteral => {
        findFirstParentConstructor(host).foreach(x => {
          val computationText = x.arguments.head.exprs(4)
          if(computationText == literal) {
            //x.arguments.head.exprs(5) .asInstanceOf[ScMethodCall].args.children.toList(1).asInstanceOf[ScInfixExpr].children.toList
            val inputs = x.arguments.head.exprs(5) match {
              case methodCall: ScMethodCall =>
                val children = methodCall.args.getChildren.toList
                children.map {
                  case infix: ScInfixExpr => firstChildrenLiterals(infix)
                }.toList
              case _ => List()
            }
            val inputText = inputs.map(x => {
              //example x = 'testValues: Map[String, Int]'
              s"val ${x.replaceAll("\"", "")} = null"
            }).mkString("\n")
            val importArgumentsListLiterals = x.arguments.head.exprs(3) match {
              case methodCall: ScMethodCall => methodCall.args.exprs.collect { case literal: ScLiteral => literal.getText }
              case _ => List()
            }
            val importText = importArgumentsListLiterals.map(quotedImport => {
              s"import ${quotedImport.replaceAll("\"", "")};"
            }).mkString("\n") + "\n" + inputText + "\n"

            val offset = if (computationText.getText.startsWith("\"\"\"")) 3 else 1
            injectionPlacesRegistrar.addPlace(scala, new TextRange(offset,host.getTextLength-offset), importText, "")
          }
        })
      }
      case _ =>
    }
  }

  def firstChildrenLiterals(infix: ScInfixExpr): String = {
    infix.children.collectFirst { case literal: ScLiteral => literal.getText}.get
  }
}
