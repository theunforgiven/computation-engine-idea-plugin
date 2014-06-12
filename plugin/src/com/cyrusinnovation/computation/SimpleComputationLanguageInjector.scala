package com.cyrusinnovation.computation

import com.intellij.lang.Language
import com.intellij.openapi.util.TextRange
import com.intellij.psi.{InjectedLanguagePlaces, LanguageInjector, PsiLanguageInjectionHost}
import org.jetbrains.plugins.scala.lang.psi.api.base.ScLiteral
import org.jetbrains.plugins.scala.lang.psi.api.expr.{ScInfixExpr, ScMethodCall}

class SimpleComputationLanguageInjector extends LanguageInjector with PsiHelpers {

  override def getLanguagesToInject(host: PsiLanguageInjectionHost, injectionPlacesRegistrar: InjectedLanguagePlaces): Unit = {
    val scala = Language.findLanguageByID("Scala")
    if (!host.isInstanceOf[ScLiteral]) return
    val literal = host.asInstanceOf[ScLiteral]
    findFirstParentConstructor(host).foreach(x => {
      val computationText = x.arguments.head.exprs(4)
      if (computationText == literal) {
        val inputs = x.arguments.head.exprs(5) match {
          case methodCall: ScMethodCall =>
            val children = methodCall.args.getChildren.toList
            children.map {
              case infix: ScInfixExpr => firstChildrenLiterals(infix)
            }.toList
          case _                        => List()
        }
        val importArgumentsListLiterals = x.arguments.head.exprs(3) match {
          case methodCall: ScMethodCall => methodCall.args.exprs.collect { case literal: ScLiteral => literal.getValue.asInstanceOf[String]}
          case _                        => List()
        }

        val inputText = inputs.map(input => s"val $input = null").mkString("\n")
        val importText = importArgumentsListLiterals.map(quotedImport => s"import $quotedImport;").mkString("\n")
        val injectedCodePrefix = importText + "\n" + inputText + "\n"
        val offset = if (computationText.getText.startsWith("\"\"\"")) 3 else 1
        val range = new TextRange(offset, host.getTextLength - offset)
        injectionPlacesRegistrar.addPlace(scala, range, injectedCodePrefix, "")
      }
    })
  }

  def firstChildrenLiterals(infix: ScInfixExpr): String = {
    infix.children.collectFirst { case literal: ScLiteral => literal.getValue.asInstanceOf[String]}.get
  }
}
