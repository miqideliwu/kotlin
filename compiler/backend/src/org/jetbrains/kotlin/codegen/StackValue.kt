/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.codegen.AsmUtil.unboxPrimitiveTypeOrNull
import org.jetbrains.kotlin.codegen.StackValue.*
import org.jetbrains.kotlin.codegen.state.KotlinTypeMapper
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.load.java.descriptors.NullDefaultValue
import org.jetbrains.kotlin.load.java.descriptors.StringDefaultValue
import org.jetbrains.kotlin.load.java.descriptors.getDefaultValueFromAnnotation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.utils.DFS
import org.jetbrains.kotlin.utils.extractRadix
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class CoercionValue(
        val value: StackValue,
        private val castType: Type
) : StackValue(castType, value.canHaveSideEffects()) {

    override fun putSelector(type: Type, v: InstructionAdapter) {
        value.putSelector(value.type, v)
        StackValue.coerce(value.type, castType, v)
        StackValue.coerce(castType, type, v)
    }

    override fun storeSelector(topOfStackType: Type, v: InstructionAdapter) {
        value.storeSelector(topOfStackType, v)
    }

    override fun putReceiver(v: InstructionAdapter, isRead: Boolean) {
        value.putReceiver(v, isRead)
    }

    override fun isNonStaticAccess(isRead: Boolean): Boolean {
        return value.isNonStaticAccess(isRead)
    }
}


class StackValueWithLeaveTask(
        val stackValue: StackValue,
        val leaveTasks: (StackValue) -> Unit
) : StackValue(stackValue.type) {

    override fun putReceiver(v: InstructionAdapter, isRead: Boolean) {
        stackValue.putReceiver(v, isRead)
    }

    override fun putSelector(type: Type, v: InstructionAdapter) {
        stackValue.putSelector(type, v)
        leaveTasks(stackValue)
    }
}

open class OperationStackValue(resultType: Type, val lambda: (v: InstructionAdapter) -> Unit) : StackValue(resultType) {

    override fun putSelector(type: Type, v: InstructionAdapter) {
        lambda(v)
        coerceTo(type, v)
    }
}

class FunctionCallStackValue(resultType: Type, lambda: (v: InstructionAdapter) -> Unit) : OperationStackValue(resultType, lambda)

fun findJavaDefaultArgumentValue(descriptor: ValueParameterDescriptor, type: Type, typeMapper: KotlinTypeMapper): StackValue {
    val descriptorWithDefaultValue = DFS.dfs(
            listOf(descriptor),
            { it.overriddenDescriptors.map(ValueParameterDescriptor::getOriginal) },
            object : DFS.AbstractNodeHandler<ValueParameterDescriptor, ValueParameterDescriptor?>() {
                var result: ValueParameterDescriptor? = null

                override fun beforeChildren(current: ValueParameterDescriptor?): Boolean {
                    if (current?.declaresDefaultValue() == true && current.getDefaultValueFromAnnotation() != null) {
                        result = current
                        return false
                    }

                    return true
                }

                override fun result(): ValueParameterDescriptor? = result
            }
    ) ?: error("Should be at least one descriptor with default value: " + descriptor)

    val defaultValue = descriptorWithDefaultValue.getDefaultValueFromAnnotation()
    if (defaultValue is NullDefaultValue) {
        return constant(null, type)
    }

    val classDescriptorForParameterType = descriptor.type.constructor.declarationDescriptor
    if (DescriptorUtils.isEnumClass(classDescriptorForParameterType)) {
        val value = Name.identifier((defaultValue as StringDefaultValue).value)

        val enumDescriptor = (classDescriptorForParameterType as ClassDescriptor)
                .unsubstitutedInnerClassesScope
                .getContributedClassifier(value, NoLookupLocation.FROM_BACKEND) as ClassDescriptor

        return enumEntry(enumDescriptor, typeMapper)
    }

    val constant = constantFromString((defaultValue as StringDefaultValue).value, type)
    return coercion(constant, type)
}

fun constantFromString(value: String, type: Type): StackValue {
    val unboxedType = unboxPrimitiveTypeOrNull(type) ?: type

    val (number, radix) = extractRadix(value)
    return when (unboxedType.sort) {
        Type.BOOLEAN -> constant(java.lang.Boolean.valueOf(value), unboxedType)
        Type.CHAR -> constant(value[0], unboxedType)
        Type.BYTE -> constant(java.lang.Byte.valueOf(number, radix), unboxedType)
        Type.SHORT -> constant(java.lang.Short.valueOf(number, radix), unboxedType)
        Type.INT -> constant(Integer.valueOf(number, radix), unboxedType)
        Type.LONG -> constant(java.lang.Long.valueOf(number, radix), unboxedType)
        Type.FLOAT -> constant(java.lang.Float.valueOf(value), unboxedType)
        Type.DOUBLE -> constant(java.lang.Double.valueOf(value), unboxedType)
        else -> constant(value, unboxedType)
    }
}
