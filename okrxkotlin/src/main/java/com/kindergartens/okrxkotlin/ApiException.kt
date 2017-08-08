package com.kindergartens.okrxkotlin

/**
 * Created by zhangruiyu on 2017/8/8.
 */
class ApiException(var code: Int, var errorMessage: String) : RuntimeException(errorMessage)