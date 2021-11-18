package org.xpathqs.prop.impl

import org.xpathqs.prop.base.IValueProcessor

class NoValueProcessor : IValueProcessor {
    override fun process(obj: Any): Any {
        return obj
    }
}