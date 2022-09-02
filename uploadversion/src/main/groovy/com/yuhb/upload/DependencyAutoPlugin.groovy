package com.yuhb.upload

import org.gradle.api.Plugin
import org.gradle.api.Project

class DependencyAutoPlugin implements Plugin<Project>{
    final String EXTENSIVE = 'dependencyList'
    final String TASK_NAME = 'uploadTask'
    @Override
    void apply(Project project) {
        println "begin:now this is a ${project.name} 's dependencyAuto plugin"
        //1.在插件中引入extensions中的字段，就是我们Project中配置的扩展字段
        project.extensions.create(EXTENSIVE,DependencyInfo.class)
        def isRunAlone = project.extensions.dependencyList.isRunAlonePlugin
        def dependList = project.extensions.dependencyList.list
        project.dependencies {
            if(!isRunAlone.toBoolean()){
                dependList.each { String depend ->
                    depend.startsWithAny(':lib',':ft')? compileOnly(project(depend)):compileOnly(depend){
                        switch (depend){
                            case rootProject.depsLibs.arouterapi:
                                exclude group: 'com.android.support'
                                break;
                        }
                    }
                }
            }else {
                dependList.each { String depend ->
                    depend.startsWithAny(':lib',':ft')? implementation(project(depend)):implementation(depend) {
                        switch (depend) {
                            case rootProject.depsLibs.arouterapi:
                                exclude group: 'com.android.support'
                                break;
                        }
                    }
                }
            }
        }
    }
}
