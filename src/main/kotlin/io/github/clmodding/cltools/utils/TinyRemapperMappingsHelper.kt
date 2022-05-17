/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016-2019 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.clmodding.cltools.utils

import net.fabricmc.mappingio.tree.MappingTree
import net.fabricmc.tinyremapper.IMappingProvider
import net.fabricmc.tinyremapper.IMappingProvider.MappingAcceptor

object TinyRemapperMappingsHelper {
    private fun memberOf(className: String, memberName: String, descriptor: String): IMappingProvider.Member {
        return IMappingProvider.Member(className, memberName, descriptor)
    }

    fun create(mappings: MappingTree, from: String, to: String, remapLocalVariables: Boolean): IMappingProvider {
        return IMappingProvider { acceptor: MappingAcceptor ->
            for (classDef in mappings.classes) {
                val className = classDef.getName(from)
                val toVariable = to
                acceptor.acceptClass(className, classDef.getName(toVariable))
                for (field in classDef.fields) {
                    acceptor.acceptField(
                        memberOf(className, field.getName(from), field.getDesc(from)),
                        field.getName(toVariable)
                    )
                }
                for (method in classDef.methods) {
                    val methodIdentifier = memberOf(className, method.getName(from), method.getDesc(from))
                    acceptor.acceptMethod(methodIdentifier, method.getName(toVariable))
                    if (remapLocalVariables) {
                        for (parameter in method.args) {
                            acceptor.acceptMethodArg(methodIdentifier, parameter.lvIndex, parameter.getName(toVariable))
                        }
                        for (localVariable in method.vars) {
                            acceptor.acceptMethodVar(
                                methodIdentifier, localVariable.lvIndex,
                                localVariable.startOpIdx, localVariable.lvtRowIndex,
                                localVariable.getName(toVariable)
                            )
                        }
                    }
                }
            }
        }
    }
}