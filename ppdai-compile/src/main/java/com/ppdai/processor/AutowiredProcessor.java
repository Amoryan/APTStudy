package com.ppdai.processor;

import com.google.auto.service.AutoService;
import com.ppdai.annotation.Autowired;
import com.ppdai.core.ISyringe;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author sunshine big boy
 *
 * <pre>
 *      talk is cheap, show me the code
 * </pre>
 */
@AutoService(Processor.class)
public class AutowiredProcessor extends AbstractProcessor {

    private HashMap<TypeElement, List<Element>> map = new HashMap<>();
    private Elements elementUtils;
    private Filer filer;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!annotations.isEmpty()) {
            categories(roundEnv.getElementsAnnotatedWith(Autowired.class));
            generateFile();
        }
        return false;
    }

    /**
     * 对所有的@Autowired元素进行分类
     */
    private void categories(Set<? extends Element> set) {
        for (Element element : set) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            if (map.containsKey(typeElement)) {
                map.get(typeElement).add(element);
            } else {
                List<Element> list = new ArrayList<>();
                list.add(element);
                map.put(typeElement, list);
            }
        }
    }

    /**
     * 生成文件
     */
    private void generateFile() {
        for (Map.Entry<TypeElement, List<Element>> entry : map.entrySet()) {
            TypeElement typeElement = entry.getKey();
            List<Element> elementList = entry.getValue();

            PackageElement packageElement = elementUtils.getPackageOf(typeElement);
            // 获取包名
            String packageName = packageElement.getQualifiedName().toString();

            String sourceClassName = typeElement.getSimpleName().toString();
            // 定义生成的文件名
            String genClassName = String.format("%s$$ARouter$$Autowired", sourceClassName);

            // 方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("inject")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(Object.class, "target")
                    .addStatement("$T inject = ($T) target", ClassName.get(typeElement), ClassName.get(typeElement));

            // 类名$$ARouter$$Autowired
            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(genClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ISyringe.class);

            TypeMirror activity = elementUtils.getTypeElement("android.app.Activity").asType();

            for (Element element : elementList) {
                Autowired autowired = element.getAnnotation(Autowired.class);
                String key = autowired.name();

                TypeMirror typeMirror = element.asType();
                String fieldName = element.getSimpleName().toString();

                if (typeUtils.isSubtype(typeElement.asType(), activity)) {
                    String source = "inject.getIntent()";
                    switch (typeMirror.getKind().toString()) {
                        case "BOOLEAN":
                            methodBuilder.addStatement("inject.$L = $L.getBooleanExtra($S, inject.$L)", fieldName, source, key, fieldName);
                            break;
                        case "LONG":
                            methodBuilder.addStatement("inject.$L = $L.getLongExtra($S, inject.$L)", fieldName, source, key, fieldName);
                            break;
                        // ...
                        default:
                    }
                }
            }

            try {
                JavaFile.builder(packageName, typeBuilder.addMethod(methodBuilder.build()).build()).build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
        set.add(Autowired.class.getCanonicalName());
        return set;
    }

}
