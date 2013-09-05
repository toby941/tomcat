/*
 * Copyright 2003-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.*;

import org.codehaus.groovy.reflection.*;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.PogoMetaClassSite;
import org.codehaus.groovy.runtime.wrappers.Wrapper;
import org.codehaus.groovy.util.FastArray;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A meta class for closures generated by the Groovy compiler. These classes
 * have special characteristics this MetaClass uses. One of these is that a
 * generated Closure has only additional doCall methods, all other methods
 * are in the Closure class as well. To use this fact this MetaClass uses
 * a MetaClass for Closure as static field And delegates calls to this
 * MetaClass if needed. This allows a lean implementation for this MetaClass.
 * Multiple generated closures will then use the same MetaClass for Closure.
 * For static dispatching this class uses the MetaClass of Class, again
 * all instances of this class will share that MetaClass. The Class MetaClass
 * is initialized lazy, because most operations do not need this MetaClass.
 * <p/>
 * The Closure and Class MetaClasses are not replaceable.
 * <p/>
 * This MetaClass is for internal usage only!
 *
 * @author Jochen Theodorou
 * @since 1.5
 */
public final class ClosureMetaClass extends MetaClassImpl {
    private boolean initialized;
    private final FastArray closureMethods = new FastArray(3);
    private Map<String, CachedField> attributes = new HashMap<String, CachedField>();
    private MethodChooser chooser;
    private volatile boolean attributeInitDone = false;

    private static final MetaClassImpl CLOSURE_METACLASS;
    private static MetaClassImpl classMetaClass;
    private static final Object[] EMPTY_ARGUMENTS = {};
    private static final String CLOSURE_CALL_METHOD = "call";
    private static final String CLOSURE_DO_CALL_METHOD = "doCall";

    static {
        CLOSURE_METACLASS = new MetaClassImpl(Closure.class);
        CLOSURE_METACLASS.initialize();
    }

    private static synchronized MetaClass getStaticMetaClass() {
        if (classMetaClass == null) {
            classMetaClass = new MetaClassImpl(Class.class);
            classMetaClass.initialize();
        }
        return classMetaClass;
    }

    private interface MethodChooser {
        Object chooseMethod(Class[] arguments, boolean coerce);
    }

    private static class StandardClosureChooser implements MethodChooser {
        private final MetaMethod doCall0;
        private final MetaMethod doCall1;

        StandardClosureChooser(MetaMethod m0, MetaMethod m1) {
            doCall0 = m0;
            doCall1 = m1;
        }

        public Object chooseMethod(Class[] arguments, boolean coerce) {
            if (arguments.length == 0) return doCall0;
            if (arguments.length == 1) return doCall1;
            return null;
        }
    }

    private static class NormalMethodChooser implements MethodChooser {
        private final FastArray methods;
        final Class theClass;

        NormalMethodChooser(Class theClass, FastArray methods) {
            this.theClass = theClass;
            this.methods = methods;
        }

        public Object chooseMethod(Class[] arguments, boolean coerce) {
            if (arguments.length == 0) {
                return MetaClassHelper.chooseEmptyMethodParams(methods);
            } else if (arguments.length == 1 && arguments[0] == null) {
                return MetaClassHelper.chooseMostGeneralMethodWith1NullParam(methods);
            } else {
                List matchingMethods = new ArrayList();

                final int len = methods.size();
                final Object[] data = methods.getArray();
                for (int i = 0; i != len; ++i) {
                    Object method = data[i];

                    // making this false helps find matches
                    if (((ParameterTypes) method).isValidMethod(arguments)) {
                        matchingMethods.add(method);
                    }
                }
                if (matchingMethods.isEmpty()) {
                    return null;
                } else if (matchingMethods.size() == 1) {
                    return matchingMethods.get(0);
                }
                return chooseMostSpecificParams(CLOSURE_DO_CALL_METHOD, matchingMethods, arguments);
            }
        }

        private Object chooseMostSpecificParams(String name, List matchingMethods, Class[] arguments) {
            long matchesDistance = -1;
            LinkedList matches = new LinkedList();
            for (Iterator iter = matchingMethods.iterator(); iter.hasNext();) {
                Object method = iter.next();
                final ParameterTypes parameterTypes = (ParameterTypes) method;
                Class[] paramTypes = parameterTypes.getNativeParameterTypes();
                if (!MetaClassHelper.parametersAreCompatible(arguments, paramTypes)) continue;
                long dist = MetaClassHelper.calculateParameterDistance(arguments, parameterTypes);
                if (dist == 0) return method;
                if (matches.isEmpty()) {
                    matches.add(method);
                    matchesDistance = dist;
                } else if (dist < matchesDistance) {
                    matchesDistance = dist;
                    matches.clear();
                    matches.add(method);
                } else if (dist == matchesDistance) {
                    matches.add(method);
                }

            }
            if (matches.size() == 1) {
                return matches.getFirst();
            }
            if (matches.isEmpty()) {
                return null;
            }

            // more than one matching method found --> ambiguous!
            String msg = "Ambiguous method overloading for method ";
            msg += theClass.getName() + "#" + name;
            msg += ".\nCannot resolve which method to invoke for ";
            msg += InvokerHelper.toString(arguments);
            msg += " due to overlapping prototypes between:";
            for (Object match : matches) {
                CachedClass[] types = ((ParameterTypes) match).getParameterTypes();
                msg += "\n\t" + InvokerHelper.toString(types);
            }
            throw new GroovyRuntimeException(msg);
        }
    }


    public ClosureMetaClass(MetaClassRegistry registry, Class theClass) {
        super(registry, theClass);
    }

    public MetaProperty getMetaProperty(String name) {
        return CLOSURE_METACLASS.getMetaProperty(name);
    }

    private void unwrap(Object[] arguments) {
        for (int i = 0; i != arguments.length; i++) {
            if (arguments[i] instanceof Wrapper) {
                arguments[i] = ((Wrapper) arguments[i]).unwrap();
            }
        }
    }

    private MetaMethod pickClosureMethod(Class[] argClasses) {
        Object answer = chooser.chooseMethod(argClasses, false);
        return (MetaMethod) answer;
    }

    private MetaMethod getDelegateMethod(Closure closure, Object delegate, String methodName, Class[] argClasses) {
        if (delegate == closure || delegate == null) return null;
        MetaClass delegateMetaClass;
        if (delegate instanceof Class) {
            delegateMetaClass = registry.getMetaClass((Class) delegate);
            return delegateMetaClass.getStaticMetaMethod(methodName, argClasses);
        } else {
            delegateMetaClass = lookupObjectMetaClass(delegate);
            MetaMethod method = delegateMetaClass.pickMethod(methodName, argClasses);
            if (method != null) {
                return method;
            }

            if (delegateMetaClass instanceof ExpandoMetaClass) {
                method = ((ExpandoMetaClass) delegateMetaClass).findMixinMethod(methodName, argClasses);

                if (method != null) {
                    onMixinMethodFound(method);
                    return method;
                }
            }

            if (delegateMetaClass instanceof MetaClassImpl) {
                method = MetaClassImpl.findMethodInClassHierarchy(getTheClass(), methodName, argClasses, this);
                if (method != null) {
                    onSuperMethodFoundInHierarchy(method);
                    return method;
                }
            }

            return method;
        }
    }

    public Object invokeMethod(Class sender, Object object, String methodName, Object[] originalArguments, boolean isCallToSuper, boolean fromInsideClass) {
        checkInitalised();
        if (object == null) {
            throw new NullPointerException("Cannot invoke method: " + methodName + " on null object");
        }

        final Object[] arguments = makeArguments(originalArguments, methodName);
        final Class[] argClasses = MetaClassHelper.convertToTypeArray(arguments);
        unwrap(arguments);

        MetaMethod method = null;
        final Closure closure = (Closure) object;

        if (CLOSURE_DO_CALL_METHOD.equals(methodName) || CLOSURE_CALL_METHOD.equals(methodName)) {
            method = pickClosureMethod(argClasses);
            if (method == null && arguments.length == 1 && arguments[0] instanceof List) {
                Object[] newArguments = ((List) arguments[0]).toArray();
                Class[] newArgClasses = MetaClassHelper.convertToTypeArray(newArguments);
                method = pickClosureMethod(newArgClasses);
                if (method != null) {
                    method = new TransformMetaMethod(method) {
                        public Object invoke(Object object, Object[] arguments) {
                            Object firstArgument = arguments[0];
                            List list = (List) firstArgument;
                            arguments = list.toArray();
                            return super.invoke(object, arguments);
                        }
                    };
                }
            }
            if (method == null) throw new MissingMethodException(methodName, theClass, arguments, false);
        }

        boolean shouldDefer = closure.getResolveStrategy() == Closure.DELEGATE_ONLY && isInternalMethod(methodName);
        if (method == null && !shouldDefer) {
            method = CLOSURE_METACLASS.pickMethod(methodName, argClasses);
        }

        if (method != null) return method.doMethodInvoke(object, arguments);

        MissingMethodException last = null;
        Object callObject = object;
        if (method == null) {
            final Object owner = closure.getOwner();
            final Object delegate = closure.getDelegate();
            final Object thisObject = closure.getThisObject();
            final int resolveStrategy = closure.getResolveStrategy();
            boolean invokeOnDelegate = false;
            boolean invokeOnOwner = false;
            boolean ownerFirst = true;

            switch (resolveStrategy) {
                case Closure.TO_SELF:
                    break;
                case Closure.DELEGATE_ONLY:
                    method = getDelegateMethod(closure, delegate, methodName, argClasses);
                    callObject = delegate;
                    if (method == null) {
                        invokeOnDelegate = delegate != closure && (delegate instanceof GroovyObject);
                    }
                    break;
                case Closure.OWNER_ONLY:
                    method = getDelegateMethod(closure, owner, methodName, argClasses);
                    callObject = owner;
                    if (method == null) {
                        invokeOnOwner = owner != closure && (owner instanceof GroovyObject);
                    }

                    break;
                case Closure.DELEGATE_FIRST:
                    method = getDelegateMethod(closure, delegate, methodName, argClasses);
                    callObject = delegate;
                    if (method == null) {
                        method = getDelegateMethod(closure, owner, methodName, argClasses);
                        callObject = owner;
                    }
                    if (method == null) {
                        invokeOnDelegate = delegate != closure && (delegate instanceof GroovyObject);
                        invokeOnOwner = owner != closure && (owner instanceof GroovyObject);
                        ownerFirst = false;
                    }
                    break;
                default: // owner first
                    // owner first means we start with the outer most owner that is not a generated closure
                    // this owner is equal to the this object, so we check that one first.
                    method = getDelegateMethod(closure, thisObject, methodName, argClasses);
                    callObject = thisObject;
                    if (method == null) {
                        // try finding a delegate that has that method... we start from
                        // outside building a stack and try each delegate
                        LinkedList list = new LinkedList();
                        for (Object current = closure; current != thisObject;) {
                            Closure currentClosure = (Closure) current;
                            if (currentClosure.getDelegate() != null) list.add(current);
                            current = currentClosure.getOwner();
                        }

                        while (!list.isEmpty() && method == null) {
                            Closure closureWithDelegate = (Closure) list.removeLast();
                            Object currentDelegate = closureWithDelegate.getDelegate();
                            method = getDelegateMethod(closureWithDelegate, currentDelegate, methodName, argClasses);
                            callObject = currentDelegate;
                        }
                    }
                    if (method == null) {
                        invokeOnDelegate = delegate != closure && (delegate instanceof GroovyObject);
                        invokeOnOwner = owner != closure && (owner instanceof GroovyObject);
                    }
            }
            if (method == null && (invokeOnOwner || invokeOnDelegate)) {
                try {
                    if (ownerFirst) {
                        return invokeOnDelegationObjects(invokeOnOwner, owner, invokeOnDelegate, delegate, methodName, arguments);
                    } else {
                        return invokeOnDelegationObjects(invokeOnDelegate, delegate, invokeOnOwner, owner, methodName, arguments);
                    }
                } catch (MissingMethodException mme) {
                    last = mme;
                }
            }
        }

        if (method != null) {
            MetaClass metaClass = registry.getMetaClass(callObject.getClass());
            if (metaClass instanceof ProxyMetaClass) {
                return metaClass.invokeMethod(callObject, methodName, arguments);
            } else {
                return method.doMethodInvoke(callObject, arguments);
            }
        } else {
            // if no method was found, try to find a closure defined as a field of the class and run it
            Object value = null;
            try {
                value = this.getProperty(object, methodName);
            } catch (MissingPropertyException mpe) {
                // ignore
            }
            if (value instanceof Closure) {  // This test ensures that value != this If you ever change this ensure that value != this
                Closure cl = (Closure) value;
                MetaClass delegateMetaClass = cl.getMetaClass();
                return delegateMetaClass.invokeMethod(cl.getClass(), closure, CLOSURE_DO_CALL_METHOD, originalArguments, false, fromInsideClass);
            }
        }

        if (last != null) throw last;
        throw new MissingMethodException(methodName, theClass, arguments, false);
    }

    private boolean isInternalMethod(String methodName) {
        return methodName.equals("curry") || methodName.equals("ncurry") || methodName.equals("rcurry") ||
                methodName.equals("leftShift") || methodName.equals("rightShift");
    }

    private Object[] makeArguments(Object[] arguments, String methodName) {
        if (arguments == null) return EMPTY_ARGUMENTS;
        return arguments;
    }

    private static Throwable unwrap(GroovyRuntimeException gre) {
        Throwable th = gre;
        if (th.getCause() != null && th.getCause() != gre) th = th.getCause();
        if (th != gre && (th instanceof GroovyRuntimeException)) return unwrap((GroovyRuntimeException) th);
        return th;
    }

    private Object invokeOnDelegationObjects(
            boolean invoke1, Object o1,
            boolean invoke2, Object o2,
            String methodName, Object[] args) {
        MissingMethodException first = null;
        if (invoke1) {
            GroovyObject go = (GroovyObject) o1;
            try {
                return go.invokeMethod(methodName, args);
            } catch (MissingMethodException mme) {
                first = mme;
            } catch (GroovyRuntimeException gre) {
                Throwable th = unwrap(gre);
                if ((th instanceof MissingMethodException)
                        && (methodName.equals(((MissingMethodException) th).getMethod()))) {
                    first = (MissingMethodException) th;
                } else {
                    throw gre;
                }
            }
        }
        if (invoke2 && (!invoke1 || o1 != o2)) {
            GroovyObject go = (GroovyObject) o2;
            try {
                return go.invokeMethod(methodName, args);
            } catch (MissingMethodException mme) {
                // patch needed here too, but we need a test case to trip it first
                if (first == null) first = mme;
            } catch (GroovyRuntimeException gre) {
                Throwable th = unwrap(gre);
                if (th instanceof MissingMethodException) {
                    first = (MissingMethodException) th;
                } else {
                    throw gre;
                }
            }
        }
        throw first;
    }

    private synchronized void initAttributes() {
        if (!attributes.isEmpty()) return;
        attributes.put("!", null); // just a dummy for later
        CachedField[] fieldArray = theCachedClass.getFields();
        for (CachedField aFieldArray : fieldArray) {
            attributes.put(aFieldArray.getName(), aFieldArray);
        }
        attributeInitDone = !attributes.isEmpty();
    }

    public synchronized void initialize() {
        if (!isInitialized()) {
            CachedMethod[] methodArray = theCachedClass.getMethods();
            synchronized (theCachedClass) {
                for (final CachedMethod cachedMethod : methodArray) {
                    if (!cachedMethod.getName().equals(CLOSURE_DO_CALL_METHOD)) continue;
                    closureMethods.add(cachedMethod);
                }
            }
            assignMethodChooser();

            initialized = true;
        }
    }

    private void assignMethodChooser() {
        if (closureMethods.size() == 1) {
            final MetaMethod doCall = (MetaMethod) closureMethods.get(0);
            final CachedClass[] c = doCall.getParameterTypes();
            int length = c.length;
            if (length == 0) {
                // no arg method
                chooser = new MethodChooser() {
                    public Object chooseMethod(Class[] arguments, boolean coerce) {
                        if (arguments.length == 0) return doCall;
                        return null;
                    }
                };
            } else {
                if (length == 1 && c[0].getTheClass() == Object.class) {
                    // Object fits all, so simple dispatch rule here
                    chooser = new MethodChooser() {
                        public Object chooseMethod(Class[] arguments, boolean coerce) {
                            // <2, because foo() is same as foo(null)
                            if (arguments.length < 2) return doCall;
                            return null;
                        }
                    };
                } else {
                    boolean allObject = true;
                    for (int i = 0; i < c.length - 1; i++) {
                        if (c[i].getTheClass() != Object.class) {
                            allObject = false;
                            break;
                        }
                    }
                    if (allObject && c[c.length - 1].getTheClass() == Object.class) {
                        // all arguments are object, so test only if argument number is correct
                        chooser = new MethodChooser() {
                            public Object chooseMethod(Class[] arguments, boolean coerce) {
                                if (arguments.length == c.length) return doCall;
                                return null;
                            }
                        };
                    } else {
                        if (allObject && c[c.length - 1].getTheClass() == Object[].class) {
                            // all arguments are Object but last, which is a vargs argument, that
                            // will fit all, so just test if the number of argument is equal or
                            // more than the parameters we have.
                            final int minimumLength = c.length - 2;
                            chooser = new MethodChooser() {
                                public Object chooseMethod(Class[] arguments, boolean coerce) {
                                    if (arguments.length > minimumLength) return doCall;
                                    return null;
                                }
                            };
                        } else {
                            // general case for single method
                            chooser = new MethodChooser() {
                                public Object chooseMethod(Class[] arguments, boolean coerce) {
                                    if (doCall.isValidMethod(arguments)) {
                                        return doCall;
                                    }
                                    return null;
                                }
                            };
                        }
                    }
                }
            }
        } else if (closureMethods.size() == 2) {
            MetaMethod m0 = null, m1 = null;
            for (int i = 0; i != closureMethods.size(); ++i) {
                MetaMethod m = (MetaMethod) closureMethods.get(i);
                CachedClass[] c = m.getParameterTypes();
                if (c.length == 0) {
                    m0 = m;
                } else {
                    if (c.length == 1 && c[0].getTheClass() == Object.class) {
                        m1 = m;
                    }
                }
            }
            if (m0 != null && m1 != null) {
                // standard closure (2 methods because "it" is with default null)
                chooser = new StandardClosureChooser(m0, m1);
            }
        }
        if (chooser == null) {
            // standard chooser for cases if it is not a single method and if it is
            // not the standard closure.
            chooser = new NormalMethodChooser(theClass, closureMethods);
        }
    }

    private MetaClass lookupObjectMetaClass(Object object) {
        if (object instanceof GroovyObject) {
            GroovyObject go = (GroovyObject) object;
            return go.getMetaClass();
        }
        Class ownerClass = object.getClass();
        if (ownerClass == Class.class) {
            ownerClass = (Class) object;
            return registry.getMetaClass(ownerClass);
        }
        MetaClass metaClass = InvokerHelper.getMetaClass(object);
        return metaClass;
    }

    @Override
    public List<MetaMethod> getMethods() {
        List<MetaMethod> answer = CLOSURE_METACLASS.getMetaMethods();
        answer.addAll(closureMethods.toList());
        return answer;
    }

    @Override
    public List<MetaMethod> getMetaMethods() {
        return CLOSURE_METACLASS.getMetaMethods();
    }

    @Override
    public List<MetaProperty> getProperties() {
        return CLOSURE_METACLASS.getProperties();
    }

    @Override
    public MetaMethod pickMethod(String name, Class[] argTypes) {
        if (argTypes == null) argTypes = MetaClassHelper.EMPTY_CLASS_ARRAY;
        if (name.equals(CLOSURE_CALL_METHOD) || name.equals(CLOSURE_DO_CALL_METHOD)) {
            return pickClosureMethod(argTypes);
        }
        return CLOSURE_METACLASS.getMetaMethod(name, argTypes);
    }

    public MetaMethod retrieveStaticMethod(String methodName, Class[] arguments) {
        return null;
    }

    protected boolean isInitialized() {
        return initialized;
    }

    public MetaMethod getStaticMetaMethod(String name, Object[] args) {
        return CLOSURE_METACLASS.getStaticMetaMethod(name, args);
    }

    public MetaMethod getStaticMetaMethod(String name, Class[] argTypes) {
        return CLOSURE_METACLASS.getStaticMetaMethod(name, argTypes);
    }

    public Object getProperty(Class sender, Object object, String name, boolean useSuper, boolean fromInsideClass) {
        if (object instanceof Class) {
            return getStaticMetaClass().getProperty(sender, object, name, useSuper, fromInsideClass);
        } else {
            return CLOSURE_METACLASS.getProperty(sender, object, name, useSuper, fromInsideClass);
        }
    }

    @Override
    public Object getAttribute(Class sender, Object object, String attribute, boolean useSuper, boolean fromInsideClass) {
        if (object instanceof Class) {
            return getStaticMetaClass().getAttribute(sender, object, attribute, useSuper);
        } else {
            if (!attributeInitDone) initAttributes();
            CachedField mfp = attributes.get(attribute);
            if (mfp == null) {
                return CLOSURE_METACLASS.getAttribute(sender, object, attribute, useSuper);
            } else {
                return mfp.getProperty(object);
            }
        }
    }

    @Override
    public void setAttribute(Class sender, Object object, String attribute,
                             Object newValue, boolean useSuper, boolean fromInsideClass) {
        if (object instanceof Class) {
            getStaticMetaClass().setAttribute(sender, object, attribute, newValue, useSuper, fromInsideClass);
        } else {
            if (!attributeInitDone) initAttributes();
            CachedField mfp = attributes.get(attribute);
            if (mfp == null) {
                CLOSURE_METACLASS.setAttribute(sender, object, attribute, newValue, useSuper, fromInsideClass);
            } else {
                mfp.setProperty(object, newValue);
            }
        }
    }

    public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        return getStaticMetaClass().invokeMethod(Class.class, object, methodName, arguments, false, false);
    }

    public void setProperty(Class sender, Object object, String name, Object newValue, boolean useSuper, boolean fromInsideClass) {
        if (object instanceof Class) {
            getStaticMetaClass().setProperty(sender, object, name, newValue, useSuper, fromInsideClass);
        } else {
            CLOSURE_METACLASS.setProperty(sender, object, name, newValue, useSuper, fromInsideClass);
        }
    }

    public MetaMethod getMethodWithoutCaching(int index, Class sender, String methodName, Class[] arguments, boolean isCallToSuper) {
        throw new UnsupportedOperationException();
    }

    public void setProperties(Object bean, Map map) {
        throw new UnsupportedOperationException();
    }

    public void addMetaBeanProperty(MetaBeanProperty mp) {
        throw new UnsupportedOperationException();
    }

    public void addMetaMethod(MetaMethod method) {
        throw new UnsupportedOperationException();
    }

    public void addNewInstanceMethod(Method method) {
        throw new UnsupportedOperationException();
    }

    public void addNewStaticMethod(Method method) {
        throw new UnsupportedOperationException();
    }

    public Constructor retrieveConstructor(Class[] arguments) {
        throw new UnsupportedOperationException();
    }

    public CallSite createPojoCallSite(CallSite site, Object receiver, Object[] args) {
        throw new UnsupportedOperationException();
    }

    public CallSite createPogoCallSite(CallSite site, Object[] args) {
        return new PogoMetaClassSite(site, this);
    }

    public CallSite createPogoCallCurrentSite(CallSite site, Class sender, Object[] args) {
        return new PogoMetaClassSite(site, this);
    }

    public List respondsTo(Object obj, String name, Object[] argTypes) {
        loadMetaInfo();
        return super.respondsTo(obj, name, argTypes);
    }

    public List respondsTo(final Object obj, final String name) {
        loadMetaInfo();
        return super.respondsTo(obj, name);
    }

    private synchronized void loadMetaInfo() {
        if (metaMethodIndex.isEmpty()) {
            initialized = false;
            super.initialize();
            initialized = true;
        }
    }

    protected void applyPropertyDescriptors(PropertyDescriptor[] propertyDescriptors) {
        // do nothing
    }
}
