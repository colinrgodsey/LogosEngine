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

package com.colingodsey.logos.asset.manager

import java.nio.ByteBuffer

trait DataProvider[T] {
    def load(from: T)
    def onLoad(bytes: Array[Byte])

}

trait AssetHolder {
    def asset: Any
}

trait TypedAssetHolder[A] extends AssetHolder {
    def asset: Option[A]
}
