package com.cyrusinnovation.computation

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.scala.lang.psi.api.base.ScConstructor

trait PsiHelpers {
  protected def findFirstParentConstructor(element: PsiElement): Option[ScConstructor] = {
    Option(element).fold(Option.empty[ScConstructor]) {
      case constructor: ScConstructor => Some(constructor)
      case other                      => findFirstParentConstructor(element.getParent)
    }
  }
}
