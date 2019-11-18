package com.spinyowl.spinygui.backend.glfwutil.callback.chain;

import org.lwjgl.system.CallbackI;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base implementation of {@link IChainCallback} based on {@link CopyOnWriteArrayList}
 */
public abstract class AbstractChainCallback<T extends CallbackI> implements IChainCallback<T> {
    protected final List<T> callbackChain = new CopyOnWriteArrayList<>();

    /**
     * Returns <tt>true</tt> if this chain contains no callbacks.
     *
     * @return <tt>true</tt> if this chain contains no callbacks
     */
    @Override
    public boolean isEmpty() {
        return callbackChain.isEmpty();
    }

    /**
     * Returns the number of callbacks in this chain.
     *
     * @return the number of callbacks in this chain
     */
    @Override
    public int size() {
        return callbackChain.size();
    }

    @Override
    public boolean remove(T callback) {
        return callbackChain.remove(callback);
    }

    /**
     * Adds the specified callback to the chain.
     *
     * @param callback callback to be added to this chain
     * @return <tt>true</tt> if callback added
     */
    @Override
    public boolean add(T callback) {
        return callbackChain.add(callback);
    }

    /**
     * Returns <tt>true</tt> if this chain contains the specified callback.
     *
     * @param callback callback whose presence in this chain is to be tested
     * @return <tt>true</tt> if this chain contains the specified callback
     */
    @Override
    public boolean contains(T callback) {
        return callbackChain.contains(callback);
    }

    /**
     * Returns <tt>true</tt> if this chain contains all of the specified callbacks.
     *
     * @param callbacks collection of callbacks to be checked for containment in this chain
     * @return <tt>true</tt> if this chain contains all of the specified callbacks.
     */
    @Override
    public boolean containsAll(Collection<? extends T> callbacks) {
        return callbackChain.containsAll(callbacks);
    }

    /**
     * Adds all of the specified callbacks in collection to the chain.
     *
     * @param callbacks collection of callbacks to be added to this chain
     * @return <tt>true</tt> if callbacks are added
     */
    @Override
    public boolean addAll(Collection<? extends T> callbacks) {
        return callbackChain.addAll(callbacks);
    }

    /**
     * Removes all of the specified callbacks in collection from chain.
     *
     * @param callbacks collection of callbacks to be removed from this chain
     * @return <tt>true</tt> if callbacks are removed
     */
    @Override
    public boolean removeAll(Collection<? extends T> callbacks) {
        return callbackChain.removeAll(callbacks);
    }

    /**
     * Removes all callbacks from the chain.
     */
    @Override
    public void clear() {
        callbackChain.clear();
    }

    /**
     * Returns the callback at the specified position in this chain.
     *
     * @param index index of the callback to return
     * @return the callback at the specified position in this chain
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    @Override
    public T get(int index) {
        return callbackChain.get(index);
    }

    /**
     * Replaces the callback at the specified position in this chain with the
     * specified callback (optional operation).
     *
     * @param index    index of the callback to replace
     * @param callback callback to be stored at the specified position
     * @return the callback previously at the specified position
     * @throws UnsupportedOperationException if the <tt>set</tt> operation
     *                                       is not supported by this chain
     * @throws NullPointerException          if the specified callback is null and
     *                                       this chain does not permit null callbacks
     * @throws IllegalArgumentException      if some property of the specified
     *                                       callback prevents it from being added to this chain
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    @Override
    public T set(int index, T callback) {
        return callbackChain.set(index, callback);
    }

    /**
     * Inserts the specified callback at the specified position in this chain
     * (optional operation).  Shifts the callback currently at that position
     * (if any) and any subsequent callbacks to the right (adds one to their
     * indices).
     *
     * @param index    index at which the specified callback is to be inserted
     * @param callback callback to be inserted
     * @throws UnsupportedOperationException if the <tt>add</tt> operation
     *                                       is not supported by this chain
     * @throws NullPointerException          if the specified callback is null and
     *                                       this chain does not permit null callbacks
     * @throws IllegalArgumentException      if some property of the specified
     *                                       callback prevents it from being added to this chain
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       (<tt>index &lt; 0 || index &gt; size()</tt>)
     */
    @Override
    public void add(int index, T callback) {
        callbackChain.add(index, callback);
    }

    /**
     * Removes the callback at the specified position in this chain (optional
     * operation).  Shifts any subsequent callbacks to the left (subtracts one
     * from their indices).  Returns the callback that was removed from the
     * chain.
     *
     * @param index the index of the callback to be removed
     * @return the callback previously at the specified position
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *                                       is not supported by this chain
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    @Override
    public T remove(int index) {
        return callbackChain.remove(index);
    }

    /**
     * Returns the index of the first occurrence of the specified callback
     * in this chain, or -1 if this chain does not contain the callback.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param callback callback to search for
     * @return the index of the first occurrence of the specified callback in
     * this chain, or -1 if this chain does not contain the callback
     * @throws NullPointerException if the specified callback is null and this
     *                              chain does not permit null callbacks
     */
    @Override
    public int indexOf(T callback) {
        return callbackChain.indexOf(callback);
    }

    /**
     * Returns the index of the last occurrence of the specified callback
     * in this chain, or -1 if this chain does not contain the callback.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param callback callback to search for
     * @return the index of the last occurrence of the specified callback in
     * this chain, or -1 if this chain does not contain the callback
     * @throws NullPointerException if the specified callback is null and this
     *                              chain does not permit null callbacks
     */
    @Override
    public int lastIndexOf(T callback) {
        return callbackChain.lastIndexOf(callback);
    }
}