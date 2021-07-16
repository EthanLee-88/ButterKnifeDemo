package com.ethan.processor;

import com.ethan.annotations.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

// AutoService 这个注解一定要加上
@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {
    private Filer mFiler;//文件类

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> bindViewElements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        // 解析 Element
        Map<Element, List<Element>> typeElementMap = new LinkedHashMap<>();
        for (Element bindViewElement : bindViewElements) {
            Element enclosingElement = bindViewElement.getEnclosingElement();
            // 变量名
            System.out.println("--------->bindViewElement = " + bindViewElement.toString());
            // 完整类名
            System.out.println("--------->enclosingElement = " + enclosingElement.toString());
            // 将 View 按所在的类进行分类保存
            List<Element> viewElements = typeElementMap.get(enclosingElement);
            if (viewElements == null) {
                viewElements = new ArrayList<>();
                typeElementMap.put(enclosingElement, viewElements);
            }
            viewElements.add(bindViewElement);
        }
        // 循环构建所有帮助类
        for (Map.Entry<Element, List<Element>> typeMap : typeElementMap.entrySet()) {
            // 完整类名
            Element typElement = typeMap.getKey();
            // 拿View
            List<Element> views = typeMap.getValue();
            // 创建一个 java 帮助类
            ClassName unbinderClassName = ClassName.get("com.butterknife", "Unbinder");
            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(typElement.getSimpleName() + "_ViewBinding")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(unbinderClassName);
            // 写构造器
            String classNameStr = typElement.getSimpleName().toString();
            ClassName uiThreadClassName = ClassName.get("androidx.annotation", "UiThread");
            ClassName parameterClassName = ClassName.bestGuess(classNameStr);
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addAnnotation(uiThreadClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(parameterClassName, "target");
            // 组装unbind 方法
            MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("unBind")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID);
            // 写所有 View 的 findById
            for (Element bindViewElement : views) {
                String filedName = bindViewElement.getSimpleName().toString();
                System.out.println("-------------> getClass = " + bindViewElement.asType().toString());
                int resId = bindViewElement.getAnnotation(BindView.class).value();
                constructorBuilder.addStatement("target.$L = target.findViewById($L)",
                        filedName, resId);
            }
            // 往类里添加方法
            typeBuilder.addMethod(constructorBuilder.build());
            typeBuilder.addMethod(unbindMethodBuilder.build());
            // 获取包名
            String packageName = ((PackageElement) typElement.getEnclosingElement()).getQualifiedName().toString();
            System.out.println("-------------> packageName = " + packageName);
            // 构建 java 文件
            JavaFile javaFile = JavaFile.builder(packageName, typeBuilder.build())
                    .addFileComment("ButterKnife add class")
                    .build();
            try {
                // 写入生成 java 类
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("翻车了");
            }
        }
        return false;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
    }

    /**
     * 指定注解类型
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    /**
     * 收集注解类型
     *
     * @return
     */
    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class);
        return annotations;
    }

    /**
     * 指定版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}