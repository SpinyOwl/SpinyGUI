package com.spinyowl.spinygui.backend.glfw.util.cbchain;

import org.lwjgl.system.CallbackI;

import java.util.Collection;

/**
 * Base interface for chain callbacks.
 *
 * @param <T> type of callback interface
 */
public interface IChainCallback<T extends CallbackI> {

    /**
     * Returns <tt>true</tt> if this chain contains no callbacks.
     *
     * @return <tt>true</tt> if this chain contains no callbacks
     */
    boolean isEmpty();

    /**
     * Returns the number of callbacks in this chain.
     *
     * @return the number of callbacks in this chain
     */
    int size();

    boolean remove(T callback);

    /**
     * Adds the specified callback to the chain.
     *
     * @param callback callback to be added to this chain
     * @return <tt>true</tt> if callback added
     */
    boolean add(T callback);

    /**
     * Returns <tt>true</tt> if this chain contains the specified callback.
     *
     * @param callback callback whose presence in this chain is to be tested
     * @return <tt>true</tt> if this chain contains the specified callback
     */
    boolean contains(T callback);

    /**
     * Returns <tt>true</tt> if this chain contains all of the specified callbacks.
     *
     * @param callbacks collection of callbacks to be checked for containment in this chain
     * @return <tt>true</tt> if this chain contains all of the specified callbacks.
     */
    boolean containsAll(Collection<? extends T> callbacks);

    /**
     * Adds all of the specified callbacks in collection to the chain.
     *
     * @param callbacks collection of callbacks to be added to this chain
     * @return <tt>true</tt> if callbacks are added
     */
    boolean addAll(Collection<? extends T> callbacks);

    /**
     * Removes all of the specified callbacks in collection from chain.
     *
     * @param callbacks collection of callbacks to be removed from this chain
     * @return <tt>true</tt> if callbacks are removed
     */
    boolean removeAll(Collection<? extends T> callbacks);

    /**
     * Removes all callbacks from the chain.
     */
    void clear();

    /**
     * Returns the callback at the specified position in this chain.
     *
     * @param index index of the callback to return
     * @return the callback at the specified position in this chain
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    T get(int index);

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
    T set(int index, T callback);

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
    void add(int index, T callback);

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
    T remove(int index);

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
    int indexOf(T callback);

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
    int lastIndexOf(T callback);
}