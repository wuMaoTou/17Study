package com.maotou.dagger2study.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by wuchundu on 17-6-29.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface UserScope {
}
