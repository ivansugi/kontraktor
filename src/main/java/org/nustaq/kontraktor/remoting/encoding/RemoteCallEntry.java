/*
Kontraktor Copyright (c) Ruediger Moeller, All rights reserved.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3.0 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

See https://www.gnu.org/licenses/lgpl.txt
*/

package org.nustaq.kontraktor.remoting.encoding;


import org.nustaq.kontraktor.Callback;
import org.nustaq.kson.ArgTypes;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.coders.JSONAsString;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by ruedi on 08.08.14.
 */
public class RemoteCallEntry implements Serializable {

    public static final int MAILBOX = 0;
    public static final int CBQ = 1;

    long receiverKey; // id of published actor in host, contains cbId in case of callbacks
    long futureKey; // id of future if any
    String method;
    @ArgTypes
    Object args[];
    @JSONAsString
    byte[] serializedArgs;
    int queue;
    boolean isContinue;
    Callback cb;

    public RemoteCallEntry() {}

    public RemoteCallEntry(long futureKey, long receiverKey, String method, Object[] args, byte[] serializedArgs) {
        this.receiverKey = receiverKey;
        this.futureKey = futureKey;
        this.method = method;
        this.args = args;
        this.serializedArgs = serializedArgs;
    }

    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    public byte[] getSerializedArgs() {
        return serializedArgs;
    }

    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    public long getReceiverKey() {
        return receiverKey;
    }

    public void setReceiverKey(long receiverKey) {
        this.receiverKey = receiverKey;
    }

    public long getFutureKey() {
        return futureKey;
    }

    public void setFutureKey(long futureKey) {
        this.futureKey = futureKey;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "RemoteCallEntry{" +
                "receiverKey=" + receiverKey +
                ", futureKey=" + futureKey +
                ", method='" + method + '\'' +
                ", args=" + Arrays.toString(args) +
                ", serargs=" + serializedArgs +
                ", queue=" + queue +
                '}';
    }

    public void setSerializedArgs(byte[] serializedArgs) {
        this.serializedArgs = serializedArgs;
    }

    public void pack(FSTConfiguration conf) {
        if ( args != null && serializedArgs == null ) {
            if ( args.length > 0 && args[args.length-1] instanceof Callback ) {
                cb = (Callback) args[args.length-1];
                args[args.length-1] = null;
            }
            serializedArgs = conf.asByteArray(args);
            args = null;
        }
    }

    public void unpackArgs(FSTConfiguration conf) {
        if ( getSerializedArgs() != null ) {
            setArgs((Object[]) conf.asObject(getSerializedArgs()));
            if ( cb != null ) {
                args[args.length-1] = cb;
                cb = null;
            }
            setSerializedArgs(null);
        }
    }

    public Callback getCB() {
        return cb;
    }

    public void setCB(Callback CB) {
        this.cb = CB;
    }
}

