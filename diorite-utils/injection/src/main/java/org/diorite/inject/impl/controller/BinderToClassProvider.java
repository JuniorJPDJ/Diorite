/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016. Diorite (by Bartłomiej Mazur (aka GotoFinal))
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

package org.diorite.inject.impl.controller;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

import org.diorite.inject.binder.Provider;

final class BinderToClassProvider<T> implements Provider<T>
{
    private final Class<T>     type;
    private final MethodHandle handle;

    BinderToClassProvider(Class<T> type)
    {
        this.type = type;
        try
        {
            Constructor<T> declaredConstructor = this.type.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            this.handle = MethodHandles.lookup().unreflectConstructor(declaredConstructor);
        }
        catch (Exception e)
        {
            throw new RuntimeException("No usable default constructor", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get()
    {
        try
        {
            return (T) this.handle.invoke();
        }
        catch (Exception throwable)
        {
            throw new RuntimeException("Can't invoke constructor of " + this.type.getName(), throwable);
        }
        catch (Throwable throwable)
        {
            throw new InternalError("Can't invoke constructor of " + this.type.getName(), throwable);
        }
    }
}
