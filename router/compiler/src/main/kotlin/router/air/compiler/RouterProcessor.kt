package router.air.compiler

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import router.air.annotation.Route
import java.util.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier.*
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

/**
 * @author wuxi
 * @since 2019/3/12
 */
@AutoService(Processor::class)
class RouterProcessor : BaseProcessor() {
    private val SEPERATOR = "_"
    private val ROUTE_ = "Route_"
    private val MODULE_NAME = "moduleName"
    private val ROUTE_INFO_CLASS = "router.air.annotation.info.RouteInfo"
    private val routeInfoMapClass by lazy { ClassName.get("java.util", "Map") }
    private val routeInfoMapParams = "routeMap"

    override fun getAnnotationClass(): Class<out Annotation> {
        return Route::class.java
    }

    override fun process(set: MutableSet<out TypeElement>?, env: RoundEnvironment): Boolean {
        val sourceClassList = env.getElementsAnnotatedWith(Route::class.java)
        if (sourceClassList.size <= 0) {
            return true
        }

        val moduleName = options[MODULE_NAME] ?: "I_HAVE_NO_NAME".plus(Random().nextInt(1000000))
        val builder = TypeSpec.classBuilder(ROUTE_.plus(moduleName))
            .addModifiers(PUBLIC, FINAL)

        val routeClass = className(ROUTE_INFO_CLASS)
        val stringClass = ClassName.get(String::class.java)

        val routeInfoClass = ParameterizedTypeName.get(routeInfoMapClass, stringClass, routeClass)
        val parameterSpec = ParameterSpec.builder(routeInfoClass, routeInfoMapParams).build()

        val funInitSpec = MethodSpec.methodBuilder("init")
            .addModifiers(PUBLIC, STATIC)
            .addParameter(parameterSpec)

        for (annotatedElement in sourceClassList) {
            try {
                val annotation = annotatedElement.getAnnotation(Route::class.java)
                funInitSpec.addStatement("\$L.put(\"\$L\", new \$T(\"\$L\", \$L, \"\$L\"))",
                    routeInfoMapParams, annotation.path, routeClass, annotation.path, annotation.priority, annotatedElement.asType().toString())
            } catch (e: Exception) {
                printErr(
                    annotatedElement,
                    "Could not create builder for %s: %s",
                    annotatedElement.simpleName,
                    e.message ?: ""
                )
            }

        }

        builder.addMethod(funInitSpec.build())
        brewJava("route.module", builder.build())
        return true
    }

    override fun createTypeSpec(annotatedElement: Element): TypeSpec {
        val name = ROUTE_.plus(getPathName(annotatedElement.getAnnotation(Route::class.java).path))

        val builder = TypeSpec.classBuilder(name)
            .addModifiers(PUBLIC, FINAL)

        return builder.build()
    }

    private fun getPathName(path: String): String {
        return path.replace("/", SEPERATOR)
    }

    /**
     * 从字符串获取TypeElement对象
     */
    fun typeElement(className: String): TypeElement {
        print("typeElement: $className")
        return elementUtils.getTypeElement(className)
    }

    /**
     * 从字符串获取TypeMirror对象
     */
    fun typeMirror(className: String): TypeMirror {
        return typeElement(className).asType()
    }

    /**
     * 从字符串获取ClassName对象
     */
    fun className(className: String): ClassName {
        return ClassName.get(typeElement(className))
    }
}