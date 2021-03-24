package org.szh.util;

@FunctionalInterface
public interface BeanCopyUtilCallBack<S,T>{
        void callBack(S s,T t);
}
