package com.github.catppuccin.jetbrains_icons.providers

import com.github.catppuccin.jetbrains_icons.IconPack.icons
import com.github.catppuccin.jetbrains_icons.util.PsiClassUtils
import com.intellij.icons.AllIcons
import com.intellij.ide.IconProvider
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.InheritanceUtil
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.util.childrenOfType
import com.intellij.ui.LayeredIcon
import com.intellij.util.IconUtil
import icons.KotlinBaseResourcesIcons
import org.jetbrains.kotlin.asJava.toLightClass
import org.jetbrains.kotlin.idea.refactoring.isAbstract
import org.jetbrains.kotlin.idea.refactoring.isInterfaceClass
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.util.isOrdinaryClass
import java.awt.Color
import javax.swing.Icon

/**
 * Provides icons for Kotlin elements.
 */
class KotlinIconProvider : IconProvider() {

  override fun getIcon(p0: PsiElement, p1: Int): Icon? {
    if (p0 is KtFile) {
      val elements = p0.childrenOfType<KtClassOrObject>()
      if (elements.size != 1) return icons.kotlin
      return getIcon(elements.first(), p1)
    }
    if (p0 !is KtClassOrObject)
      return if (PsiUtilCore.getVirtualFile(p0)?.name?.endsWith(".kt") == true) icons.kotlin else null

    return getElement(p0)?.let {
      LayeredIcon(2).apply {
        setIcon(it, 0)
        setIcon(IconUtil.scale(KotlinBaseResourcesIcons.Kotlin_file, null, 0.6f), 1, 10, 10)
      }
    } ?: run {
      icons.kotlin
    }
  }

  private val objectIcon = LayeredIcon(
    IconUtil.colorize(icons.java_class, Color(250, 179, 135)),
    AllIcons.Nodes.StaticMark,
  )

  private fun getElement(element: KtClassOrObject): Icon? = when {
    element is KtObjectDeclaration -> objectIcon
    element.isAnnotation() -> icons.java_annotation
    element.isOrdinaryClass -> when {
      element.hasModifier(KtTokens.ENUM_KEYWORD) -> icons.java_enum
      element.isAbstract() -> icons.java_class_abstract
      element.hasModifier(KtTokens.SEALED_KEYWORD) -> icons.java_class_sealed
      element.isData() -> icons.java_record
      else -> icons.java_class
    }
    element.isInterfaceClass() -> icons.java_interface
    else -> null
  }
}

