package nl.peperzaken.renameprocessor

import com.google.auto.service.AutoService
import com.sun.source.util.Trees
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeTranslator
import com.sun.tools.javac.util.Names
import nl.peperzaken.renameannotation.Rename
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("nl.peperzaken.renameannotation.Rename")
class RenameProcessor : AbstractProcessor() {

    private lateinit var trees: Trees
    private lateinit var names: Names

    private val visitor = object : TreeTranslator() {
        override fun visitMethodDef(jcMethodDecl: JCTree.JCMethodDecl) {
            super.visitMethodDef(jcMethodDecl)

            // print original declaration
            processingEnv.messager.printMessage(
                Diagnostic.Kind.NOTE,
                jcMethodDecl.toString()
            )

            // Rename declaration
            jcMethodDecl.name = names.fromString("renamed")

            // print renamed declaration
            processingEnv.messager.printMessage(
                Diagnostic.Kind.NOTE,
                jcMethodDecl.toString()
            )

            // commit changes
            result = jcMethodDecl
        }
    }

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        trees = Trees.instance(processingEnvironment)
        val context = (processingEnvironment as JavacProcessingEnvironment).context
        names = Names.instance(context)
    }

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        // Find elements that are annotated with @Rename
        for (element in roundEnvironment.getElementsAnnotatedWith(Rename::class.java)) {
            val tree = trees.getTree(element) as JCTree
            tree.accept(visitor)
        }
        return true
    }
}