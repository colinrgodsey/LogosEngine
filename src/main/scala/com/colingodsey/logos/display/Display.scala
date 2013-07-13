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

package com.colingodsey.logos.display

import com.colingodsey.logos.math.{Core => MCore, Matrix3x2, Types2D}

object Core {
    trait Display {
        def start
        def stop
        def update

        def isRunning: Boolean

        def currentScene: Scene

        def width: Int
        def height: Int

        def version: String
    }

    trait Scene {
        def render
    }

    trait Property {
        def owner: Entity

        def update(dt: Double)
    }

    trait Entity {
        protected def propertiesList: Seq[Property] = Nil

        lazy val properties = propertiesList

        def update(dt: Double) = properties.foreach(_ update dt)
    }

    trait EntityProperty extends Entity {
        //abstract override def propertiesList: Seq[Property]
    }



}