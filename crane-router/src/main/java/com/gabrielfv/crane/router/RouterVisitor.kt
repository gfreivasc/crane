package com.gabrielfv.crane.router

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*

internal class RouterVisitor(
    private val outputMap: MutableMap<String, String>,
    private val logger: KSPLogger
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (!classDeclaration.isFragmentDeclaration) {
            logger.error(
                "@RoutedBy should only be used against " +
                    "${RouterProcessor.FRAGMENT_FQ_NAME} instances"
            )
        }
        val fqName = classDeclaration.qualifiedName?.asString() ?: "ERROR"
        val annotations = classDeclaration.annotations.filter { ksAnnotation ->
            ksAnnotation.isRoutedByAnnotation
        }
        if (annotations.isEmpty()) {
            logger.error(
                "${javaClass.simpleName} shouldn't be accepted by non " +
                    "@RoutedBy annotated classes."
            )
            return
        } else if (annotations.size > 1) {
            logger.error(
                "Fragment $fqName is annotated with @RoutedBy more than once."
            )
        }
        val route = processAnnotation(annotations[0])

        outputMap[route] = fqName
    }

    @Suppress("UNCHECKED_CAST")
    private fun processAnnotation(routedBy: KSAnnotation): String {
        val routeDec = routedBy.arguments
            .first()
            .value
            .let { it as KSType }
            .declaration
        val fqName = routeDec.qualifiedName?.asString() ?: "ERROR"
        if (outputMap.containsKey(fqName)) {
            logger.error(
                "Route ${routeDec.simpleName.asString()} is routing " +
                    "multiple different fragments."
            )
        }
        return fqName
    }

    private val KSClassDeclaration.isFragmentDeclaration: Boolean get() {
        return superTypes.contains { typeRef ->
            val fqName = typeRef
                .resolve()
                .declaration
                .qualifiedName
                ?.asString() ?: "ERROR"
            fqName == RouterProcessor.FRAGMENT_FQ_NAME && classKind == ClassKind.CLASS
        }
    }

    private val KSAnnotation.isRoutedByAnnotation: Boolean get() {
        val type = annotationType
            .resolve()
            .declaration
        return type.qualifiedName
            ?.asString() ?: "ERROR" == RouterProcessor.ANNOTATION_FQ_NAME
    }

    private fun <T> Collection<T>.contains(predicate: (T) -> Boolean): Boolean {
        return find(predicate) != null
    }
}
