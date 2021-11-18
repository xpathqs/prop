package org.xpathqs.prop.impl

import org.xpathqs.prop.base.IValueProcessor

class ProcessVars(source: IValueProcessor) : ValueProcessorDecorator(source) {
    override fun selfProcess(obj: Any): Any {
        if(obj is String) {
            if(obj.contains("\${")) {
                val start = obj.indexOf("\${")
                val end = obj.indexOf("}", start + 2)
                val varName = obj.substring(start + 2, end)
                if(System.getProperty(varName) != null) {
                    val varValue = System.getProperty(varName, "")
                    return obj.replace("\${$varName}", varValue)
                }
            }
        }
        return obj
    }
}