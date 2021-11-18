package org.xpathqs.prop.impl

import org.xpathqs.prop.base.IModelExtractor

class ModelExtractor(
    private val model: Map<String, Any>
): IModelExtractor {
    override fun getModel() = model
}