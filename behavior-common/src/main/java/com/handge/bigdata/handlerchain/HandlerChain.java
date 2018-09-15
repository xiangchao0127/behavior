/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.handlerchain;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * base handler pipeline
 */
public class HandlerChain implements Chain {

    protected final List<Handler> handlers = new ArrayList<Handler>();
    protected final List<Handler> finalHandlers = new ArrayList<Handler>();
    protected Handler head;

    protected Handler tail;
    private volatile boolean sorted = false;

    public HandlerChain() {

    }


    public HandlerChain(Handler handler) {

        if (handler == null) {
            throw new IllegalArgumentException();
        }
        addHandler(handler);

    }


    public HandlerChain(Handler[] handlers) {

        if (handlers == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < handlers.length; i++) {
            addHandler(handlers[i]);
        }

    }


    public HandlerChain(List handlers) {

        if (handlers == null) {
            throw new IllegalArgumentException();
        }
        Iterator elements = handlers.iterator();
        while (elements.hasNext()) {
            addHandler((Handler) elements.next());
        }

    }

    /**
     * add head ,tail to handlers pipeline
     */
    private void sortedHandlers() {
        if (!sorted) {
            synchronized (this) {
                if (!sorted) {
                    if (head != null) finalHandlers.add(head);
                    for (Handler handler : handlers) {
                        finalHandlers.add(handler);
                    }
                    if (tail != null) finalHandlers.add(tail);
                    sorted = true;
                }
            }
        }
    }

    @Override
    public void addHandler(Handler handler) {
        if (handler == null) {
            throw new IllegalArgumentException();
        }
        if (handlers.contains(handler)) {
            return;
        }
        handlers.add(handler);
    }

    @Override
    public Handler getHead() {
        return head;
    }

    @Override
    public void setHead(Handler head) {
        if (head == null) {
            throw new IllegalArgumentException();
        }
        this.head = head;
    }

    @Override
    public Handler getTail() {
        return tail;
    }

    @Override
    public void setTail(Handler tail) {
        if (tail == null) {
            throw new IllegalArgumentException();
        }
        this.tail = tail;
    }

    @Override
    public List<Handler> getHandlers() {
        return handlers;
    }


    @Override
    public boolean execute(final Context context) throws Exception {

        if (context == null) {
            throw new IllegalArgumentException();
        }
        sortedHandlers();
        boolean saveResult = false;

        for (Handler handler : finalHandlers) {
            try {
                saveResult = handler.execute(context);
                if (saveResult) {
                    break;
                }
            } catch (Exception e) {
                handler.exception(context.toString(), e);
            }
        }
        context.destroy();
        return saveResult;
    }
}
