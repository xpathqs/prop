package org.xpathqs.prop.impl

import org.xpathqs.prop.base.IValueProcessor

abstract class ValueProcessorDecorator(
    private val source: IValueProcessor
) : IValueProcessor {
    abstract fun selfProcess(obj: Any): Any

    override fun process(obj: Any): Any {
        return source.process(selfProcess(obj))
    }
}