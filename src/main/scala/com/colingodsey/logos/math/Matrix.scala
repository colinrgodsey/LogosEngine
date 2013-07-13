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

package com.colingodsey.logos.math

import java.awt.geom.{Point2D => JPoint2D, AffineTransform}
import Core._
import scala.Immutable
import com.colingodsey.logos.math.Core.Immutable

trait Matrix extends Product {
    //def defaultFactory: MatrixFactory[Matrix]

    //def iterator: TraversableOnce[Double]

    def apply(idx: Int): Double

    def productElement(idx: Int) = apply(idx)

    def determinant: Double

    val nRows: Int
    val nCols: Int

    //lazy val bigMatrix = new AffineTransform(m, n)

    override def productPrefix = "Matrix"

    final override def toString =
        productIterator.mkString(productPrefix + "(", ",", ")")
    override def hashCode = util.hashing.MurmurHash3.productHash(this)
    final override def canEqual(that: Any) = that match {
        case x: Matrix if x.productArity == productArity => true
        case _ => false
    }
    final override def equals(that: Any) = that match {
        case x: Matrix if x.productArity == productArity =>
            productIterator sameElements x.productIterator
        case _ => false
    }
}

trait MatrixFactory[+T <: Matrix] {
    //def apply(vals: Double*): T

    //def apply(other: Matrix): T = if(other.defaultFactory == this)
    //    other.asInstanceOf[T] else apply(other.iterator.toSeq: _*)

    def apply(trans: AffineTransform): T
    //def apply(): T

    //type Type = T
}

trait BetterMatrix {
    def isIdentity: Boolean
    def isScaled: Boolean
    def isRotated: Boolean
    def isTransformed: Boolean

    def is2D: Boolean
    def is3D: Boolean
}

object Matrix3x2 {

    trait Immutable extends Matrix3x2 with Matrix3x2ImmutableOps {
        def toMutable: Matrix3x2.Mutable =
            Matrix3x2.Mutable(matrixCopy)
        def toImmutable: Matrix3x2.Immutable = this

        private lazy val _iter = super.productIterator.toSeq

        override def productIterator: Iterator[Any] = _iter.iterator
        override lazy val hashCode = super.hashCode
    }

    object Immutable extends MatrixFactory[Immutable] {
        // def apply(vals: Double*): Matrix3x2 = ???
        def apply(trans: AffineTransform): Immutable = new Immutable {
            protected val matrix = trans
        }

        def apply(): Immutable = apply(new AffineTransform)
    }

    def apply() = Immutable()

    class Builder extends Mutable {
        def build: Immutable = {
            seal
            toImmutable
        }
    }

    //object Mutable extends MatrixFactory[Matrix3x2]
    trait Mutable extends Matrix3x2 with MatrixFactory[Mutable] with
            Matrix3x2ImmutableOps with Matrix3x2MutableOps {
        protected var matrix = new AffineTransform
        private var _sealed = false

        private lazy val sealCopy = Matrix3x2.Immutable(matrix)

        def toMutable: Matrix3x2.Mutable = this
        def toImmutable: Matrix3x2.Immutable =
            if(!isSealed) Matrix3x2.Immutable(matrixCopy)
            else sealCopy

        private[Matrix3x2] def seal: Unit = _sealed = true
        def isSealed = _sealed

        def apply(trans: AffineTransform) = {
            matrix = trans
            this
        }
    }

    object Mutable {
        def apply(): Mutable = new Mutable {}

        def apply(trans: AffineTransform) = new Mutable {
            matrix = trans
        }
    }

    trait Matrix3x2MutableOps extends Matrix3x2 {
        def isSealed: Boolean

        @inline private def checkSeal {
            if(isSealed) sys.error("Cannot modify a sealed matrix!")
        }

        def invert {
            checkSeal
            matrix.invert
        }

        def *= (other: Matrix3x2) {
            checkSeal
            matrix.concatenate(other.getMatrix)
        }

        def >>= (rads: Double) {
            checkSeal
            matrix.rotate(rads)
        }

        def >>= (rads: Double, point: Point2D) {
            checkSeal
            matrix.rotate(rads, point.x, point.y)
        }

        def *= (point: Point2D) {
            checkSeal
            matrix.scale(point.x, point.y)
        }

        def += (point: Point2D) {
            checkSeal
            matrix.translate(point.x, point.y)
        }

    }

    trait Matrix3x2ImmutableOps extends Matrix3x2 {
        def inverse = {
            val cp = matrixCopy
            cp.invert
            Matrix3x2.Immutable(cp)
        }

        def *( other: Matrix3x2) = {
            val cp = matrixCopy
            cp.concatenate(other.getMatrix)
            Matrix3x2.Immutable(cp)
        }

        def >> (rads: Double) = {
            val cp = matrixCopy
            cp.rotate(rads)
            Matrix3x2.Immutable(cp)
        }

        def >> (rads: Double, point: Point2D) = {
            val cp = matrixCopy
            cp.rotate(rads, point.x, point.y)
            Matrix3x2.Immutable(cp)
        }

        def * (point: Point2D) = {
            val cp = matrixCopy
            cp.scale(point.x, point.y)
            Matrix3x2.Immutable(cp)
        }

        def + (point: Point2D) = {
            val cp = matrixCopy
            cp.translate(point.x, point.y)
            Matrix3x2.Immutable(cp)
        }
    }
}

trait Matrix3x2 extends Matrix {
    def _1: Double = matrix.getScaleX
    def _2: Double = matrix.getShearY
    def _3: Double = matrix.getShearX
    def _4: Double = matrix.getScaleY
    def _5: Double = matrix.getTranslateX
    def _6: Double = matrix.getTranslateY

    val nRows = 2
    val nCols = 3

    def productArity = 6

    protected def matrix: AffineTransform

    def toMutable: Matrix3x2.Mutable
    def toImmutable: Matrix3x2.Immutable

    private[Matrix3x2] def getMatrix = matrix

    //either this, or a copy if immutable
    protected def matrixCopy: AffineTransform = matrix.clone.asInstanceOf[AffineTransform]

    def apply(idx: Int) = idx match {
        case 0 => _1
        case 1 => _2
        case 2 => _3
        case 3 => _4
        case 4 => _5
        case 5 => _6
    }

    //def determinant = (apply(0) * apply(3) - apply(1) * apply(2))
    def determinant = matrix.getDeterminant

    def apply[T <: Point2D](point: T)(implicit factory: PointFactory[T]): T = {

        val dst = new JPoint2D.Double()
        val src = new JPoint2D.Double(point.x, point.y)

        matrix.transform(src, dst)

        factory(dst.x, dst.y)
    }
}