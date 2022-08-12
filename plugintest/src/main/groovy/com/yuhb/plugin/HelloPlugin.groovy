package com.yuhb.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class HelloPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println 'heoolo plugin!!!'
    }
}