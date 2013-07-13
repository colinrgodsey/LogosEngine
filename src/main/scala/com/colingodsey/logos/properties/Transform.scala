/*
Copyright [2013] [Colin Godsey]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.colingodsey.logos.properties

import com.colingodsey.logos.math.{Matrix3x2, Types2D, Core}
import com.colingodsey.logos.display.Core._

trait Position extends Property {
    def origin: MCore.Point

    def update(dt: Double) {}
}

trait Position2D extends Position {
    def origin: Types2D.Point
}

object Transform2D {
    val null2DScale = Types2D.Point(1.0, 1.0)
}

trait Transform2D extends Position2D {
    def origin: Types2D.Point
    def scale: Types2D.Point
    def rotation: Double
    def matrix: Matrix3x2

}

trait MutableTransform2D extends Transform2D {
    protected var matrixDirty = true

    private var _origin = Types2D.Point()
    private var _scale = Types2D.Point(1.0, 1.0)
    private var _rotation = 0.0
    private var _matrix = Matrix3x2.Immutable()

    def origin = _origin
    def origin_=(x: Types2D.Point): Unit = {
        _origin = x
        dirtyMatrix
    }

    def scale = _scale
    def scale_=(x: Types2D.Point) = {
        _scale = x
        dirtyMatrix
    }

    def rotation = _rotation
    def rotation_=(x: Double) = {
        _rotation = x
        dirtyMatrix
    }

    def matrix = if(matrixDirty) {
        _matrix = calcMatrix
        matrixDirty = false
        _matrix
    } else _matrix

    protected def calcMatrix: Matrix3x2.Immutable = {
        val m = new Matrix3x2.Builder

        if(!_origin.isOrigin) m += _origin
        if(_scale != Transform2D.null2DScale) m *= _scale
        if(_rotation != 0.0) m >>= _rotation

        m.build
    }

    protected def dirtyMatrix: Unit = matrixDirty = true

    override def update(dt: Double) {
        matrix
    }
}


//Entity traits
trait EntityPosition extends EntityProperty {
    val position: Position/* = new Position {
            var origin = Types2D.Point()
            val owner: Entity = EntityPosition.this
        }*/

    abstract override def propertiesList: Seq[Property] =
        position +: super.propertiesList
}

trait Entity2DTransform extends EntityPosition {
    val transform = new MutableTransform2D {
        val owner: Entity = Entity2DTransform.this
    }

    val position: Position2D = transform
}