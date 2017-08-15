package com.maotou.dagger2study.qulifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by wuchundu on 17-6-29.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface QualifierA {
}
