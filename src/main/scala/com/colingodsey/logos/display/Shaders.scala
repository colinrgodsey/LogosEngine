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

import org.lwjgl.opengl.{GL11, ARBShaderObjects, ARBVertexShader, ARBFragmentShader}
import org.lwjgl.opengl
import com.colingodsey.logos.asset.manager._


//https://www.shadertoy.com/

object Shaders {
    import LWJGL32._

    trait ShaderType { def glType: Int }
    case object Fragment extends ShaderType { val glType = ARBFragmentShader.GL_FRAGMENT_SHADER_ARB }
    case object Vertex extends ShaderType { val glType = ARBVertexShader.GL_VERTEX_SHADER_ARB }

    trait Shader {
        def apply
        def bind = apply
    }
    trait ShaderAsset extends TypedAssetHolder[Shader]

    trait ShaderFactory {
        protected def loadShader(str: String, shaderType: ShaderType) = {
            import opengl.ARBShaderObjects

            val shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType.glType)

            require(shader != 0, "Failed to create shader object!")

            ARBShaderObjects.glShaderSourceARB(shader, str)
            ARBShaderObjects.glCompileShaderARB(shader)

            if (ARBShaderObjects.glGetObjectParameteriARB(shader,
                ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
                sys.error("Error creating shader from: " + str)

            shader
        }

        def shaders: Map[ShaderType, String]

        def build = {
            val program = ARBShaderObjects.glCreateProgramObjectARB

            shaders foreach {
                case (k, v) =>
                    val shader = loadShader(v, k)
                    require(shader != 0)
                    ARBShaderObjects.glAttachObjectARB(program, shader)
            }

            ARBShaderObjects.glLinkProgramARB(program)
            require(ARBShaderObjects.glGetObjectParameteriARB(program,
                ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) != GL11.GL_FALSE)

            ARBShaderObjects.glValidateProgramARB(program)

            require(ARBShaderObjects.glGetObjectParameteriARB(program,
                ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) != GL11.GL_FALSE)

            new Shader {
                private val prog = program
                def apply = ARBShaderObjects.glUseProgramObjectARB(prog)
            }
        }
    }

    object ShaderFactory {
        def apply(shads: Map[ShaderType, String]) = new ShaderFactory {
            val shaders = shads
        }
    }
}