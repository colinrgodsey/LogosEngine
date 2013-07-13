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

import org.lwjgl.opengl
import java.awt
import java.awt.event.{WindowEvent, WindowAdapter, ComponentEvent, ComponentAdapter}
import java.awt.{Dimension, BorderLayout}
import com.colingodsey.logos.math.{Types2D, Core => MCore}
import org.lwjgl.opengl.{ARBShaderObjects, ARBFragmentShader, ARBVertexShader}

trait Logging {
    def warn(msg: => String, cause: Throwable = null)
}

/**
 * Created with IntelliJ IDEA.
 * User: crgodsey
 * Date: 6/11/13
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
object LWJGL32 {
    import org.lwjgl.opengl.GL11
    import org.lwjgl.opengl.GL20
    import org.lwjgl.opengl.GL30
    import org.lwjgl.opengl.GL32

    val log = new Logging {
        def warn(msg: => String, cause: Throwable = null) = println(msg)
    }

    



    object TestShader {
        val vertexShaderStr =
            """
varying vec4 vertColor;

void main(){

    gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;

    vertColor = vec4(0.6, 0.3, 0.4, 1.0);


}
            """

        val pixelShaderStr =
            """
varying vec4 vertColor; 

void main(){ 

    gl_FragColor = vertColor;

}
            """
    }



    trait Display extends Core.Display {
        val frame = new awt.Frame("Test")
        val canvas = new awt.Canvas
        //val renderer = new LWJGLRenderer

        var closeRequested = false
        var lastSize: Option[Types2D.Point] = None
        var currentSize = Types2D.Point(0, 0)

        frame.setLayout(new awt.BorderLayout)

        canvas.addComponentListener(new ComponentAdapter() {
            override def componentResized(e: ComponentEvent): Unit = {
                val newCanvasSize = canvas.getSize

                currentSize = Types2D.Point(newCanvasSize.getWidth,
                    newCanvasSize.getHeight)
            }
        })

        frame.addWindowFocusListener(new WindowAdapter() {
            override def windowGainedFocus(e: WindowEvent): Unit =
                canvas.requestFocusInWindow()
        })

        frame.addWindowListener(new WindowAdapter() {
            override def windowClosing(e: WindowEvent): Unit =
                closeRequested = true
        })

        frame.add(canvas, BorderLayout.CENTER)

        def start {
            val pixelFormat = new opengl.PixelFormat
            val contextAtrributes = new opengl.ContextAttribs(3, 2).
                    withForwardCompatible(true).withProfileCore(true)

            opengl.Display.setParent(canvas)
            opengl.Display.setVSyncEnabled(true)

            frame.setPreferredSize(new Dimension(width, height))
            frame.setMinimumSize(new Dimension(800, 600))
            frame.pack
            frame.setVisible(true)
            //opengl.Display.setDisplayMode(new opengl.DisplayMode(width, height))
            opengl.Display.create(pixelFormat, contextAtrributes)

            /*GL.glMatrixMode(GL.GL_PROJECTION)
            GL.glLoadIdentity()
            GL.glOrtho(0, 800, 0, 600, 1, -1)
            GL.glMatrixMode(GL.GL_MODELVIEW)*/


        }

        def stop = opengl.Display.destroy

        def update: Unit = {
            if(lastSize != Some(currentSize)) {
                GL11.glViewport(0, 0, currentSize.x.toInt, currentSize.y.toInt);
                //renderer.syncViewportSize

                log.warn(s"Changing viewport to $currentSize")

                lastSize = Some(currentSize)
            }

            currentScene.render

            opengl.Display.update()
            //opengl.Display.sync(70)

        }

        def isRunning = !opengl.Display.isCloseRequested && !closeRequested

        def version: String = {
            val maj = GL32.glGetInteger64(GL30.GL_MAJOR_VERSION)
            val min = GL32.glGetInteger64(GL30.GL_MINOR_VERSION)

            maj + "." + min
        }
    }

    object Display {
        def apply(w: Int, h: Int, scene: Core.Scene) = new Display {
            def width = currentSize.x.toInt
            def height = currentSize.y.toInt

            currentSize = Types2D.Point(w, h)

            val currentScene: Core.Scene = scene
        }
    }

    class Scene extends Core.Scene {
        def render {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

            GL11.glClearColor(0.0f, 0.0f, math.random.toFloat, 1.0f)
        }
    }
}

