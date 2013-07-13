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

object Core {
    final val vectorEpsilon = 0.00000000000001

    trait PointLike[T <: Point] extends Point {
        private implicit def pointFac = pointFactory

        def normal: T = this / length

        def pointFactory: PointFactory[T]
    }

	trait Point extends Product {
	    def defaultFactory: PointFactory[Point]

	    final lazy val dimensions = productArity

		def apply(idx: Int): Double = productElement(idx).asInstanceOf[Double]

		/** Dot product */
		def * (other: Point): Double = {
		    val l = other.dimensions

		    val vals = for(i <- 0 until l) yield this(i) * other(i)

		    vals.sum
		}

        def isOrigin = iterator.count(_ != 0.0) == 0

	    //def x() //cross product

	    /** scale */
	    def * [T <: Point](scalar: Double)(implicit factory: PointFactory[T]): T =
	        factory(iterator.toSeq.map(_ * scalar): _*)

	    def + [T <: Point](other: T)(implicit factory: PointFactory[T]): T =
	        factory((for(i <- 0 until other.dimensions) yield this(i) + other(i)): _*)

	    def - [T <: Point](other: T)(implicit factory: PointFactory[T]): T =
	        factory((for(i <- 0 until other.dimensions) yield this(i) - other(i)): _*)

		def / [T <: Point](scalar: Double)(implicit factory: PointFactory[T]): T =
		    this * (1.0 / scalar)

        def ~~ (other: Point): Boolean = if(dimensions == other.dimensions) {
            val l = this.-(other)(defaultFactory).length

            l <= vectorEpsilon
        } else false

		protected def calcLength = math.sqrt(this * this)

		def length = calcLength

		//def normal[T <: Point](implicit factory: PointFactory[T]): T = this / length
		def normal: Point

		def iterator: TraversableOnce[Double] =
		    productIterator.map(_.asInstanceOf[Double])

		override def productPrefix = "Point"

		final def > (other: Point) = length > other.length
		final def < (other: Point) = length < other.length

		def isNormal = Math.abs(length - 1.0) < vectorEpsilon

		override def toString =
		    productIterator.mkString(productPrefix + "(", ",", ")")
		override def hashCode = util.hashing.MurmurHash3.productHash(this)
		override def canEqual(that: Any) = that match {
		    case x: Point if x.dimensions == dimensions => true
		    case _ => false
		}
		override def equals(that: Any) = that match {
		    case x: Point if x.productArity == productArity &&
                    hashCode == x.hashCode =>
		        productIterator sameElements x.productIterator
		    case _ => false
		}
	}

	trait PointFactory[+T <: Point] {
	    def apply(vals: Double*): T

	    def apply(other: Point): T = if(other.defaultFactory == this)
	        other.asInstanceOf[T] else apply(other.iterator.toSeq: _*)

	    //type Type = T
	}

	trait Position extends Point //??

	trait Point2D extends Point with PointLike[Point2D] with Product2[Double, Double] {
	    def x: Double
	    def y: Double

	    def _1 = x
	    def _2 = y
	}

	trait Point3D extends Point with PointLike[Point3D] with Product3[Double, Double, Double] {
	    def x: Double
	    def y: Double
	    def z: Double

	    def _1 = x
	    def _2 = y
	    def _3 = z
	}

	object Immutable {
	    trait Point[T <: Core.Point] extends Core.Point with scala.Immutable with PointLike[T] {
	        override lazy val length = calcLength
	        override lazy val iterator: TraversableOnce[Double] = super.iterator.toSeq
	        override lazy val isNormal = super.isNormal
            override lazy val hashCode = super.hashCode
	        override lazy val normal = super.normal
	    }
	}
}

object Types3D {
    implicit case object PointFactory extends Core.PointFactory[Point] {
        def apply(vals: Double*) = {
            val vs = vals.toSeq
            //should check length?
            Point(vs(0), vs(1), vs(2))
        }
    }

    case class Point(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0)
    		extends Core.Point3D with Core.Immutable.Point[Core.Point3D] {
        val defaultFactory = PointFactory
        val pointFactory = PointFactory

        def + (other: Point) = Point(x + other.x, y + other.y, z + other.z)
    }
}

object Types2D {
    //type Point = Core.Point2D
    implicit case object PointFactory extends Core.PointFactory[Point] {
        def apply(vals: Double*) = {
            val vs = vals.toSeq
            //should check length?
            Point(vs(0), vs(1))
        }
    }

    case class Point(x: Double = 0.0, y: Double = 0.0)
    		extends Core.Point2D with Core.Immutable.Point[Core.Point2D] {
        val defaultFactory = PointFactory
        val pointFactory = PointFactory

        def + (other: Point) = Point(x + other.x, y + other.y)
    }
}

object Main {
    def main(args: Array[String]) {
         def test2d {
             import Types2D._

             val testPoint = Point(0.23, 1)
	         val testPoint2 = Point(500, 5) * 4.232344 +
	         	Point(testPoint.length, 1) * Math.PI
	         val testPoint3 = testPoint * 5 + testPoint + testPoint2
	         val testPoint4 = testPoint2 * (testPoint3 * testPoint2)

	         println(testPoint4, testPoint4.length, testPoint4.normal)

             var testMatrix = Matrix3x2()

             testMatrix += testPoint
             testMatrix *= testPoint2
             testMatrix >>= Math.PI * 1.57

             val resPoint = testMatrix(testPoint)

             val testMatrix2 = Matrix3x2.Mutable()

             testMatrix2 += testPoint
             testMatrix2 *= testPoint2
             testMatrix2 >>= Math.PI * 1.57

             //((testMatrix2 + testPoint) * testPoint) >> (Math.PI * 1.57)
             val resPoint2 = testMatrix2(testPoint)

             println(testMatrix * testMatrix2, resPoint2, resPoint)

             require(resPoint2 ~~ resPoint)
             require(testMatrix == testMatrix2)
         }

         def test3d {
             import Types3D._

	         val testPoint6 = Point(Math.PI, 5, 8) * 23.4
	         val testPoint7 = Point(0, 1, 8)

	         println(testPoint6, testPoint7)

	         val testPoint8 = testPoint6 / 343434 + testPoint7

	         println(testPoint8, testPoint8.length,
	                 testPoint8.normal, testPoint8.normal.length)

             println("is normal " + testPoint8.normal.isNormal)
         }

        def testDisplay {
            import com.colingodsey.logos.display.LWJGL32

            val scene = new LWJGL32.Scene
            val display = LWJGL32.Display(
                800, 600, scene)

            display.start

            println(display.version)
            while(display.isRunning) {
                display.update
            }

            display.stop
        }

        test2d
        test3d

        testDisplay
    }
}







