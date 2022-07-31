package org.xpathqs.prop.data

import org.xpathqs.prop.Model

open class Base {
    var p1 = ""
}

open class Base2 : Base() {
    var p2 = ""
}

@Model
object WithInheritance {
    var m = Base()
}