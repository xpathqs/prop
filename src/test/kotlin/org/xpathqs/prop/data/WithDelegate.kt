package org.xpathqs.prop.data

import org.xpathqs.prop.Model
import org.xpathqs.prop.models.Delegate

@Model
object WithDelegate {
    val f = Delegate()
}