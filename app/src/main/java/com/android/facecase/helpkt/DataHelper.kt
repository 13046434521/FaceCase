package com.android.facecase.helpkt

/**
 * @author：TianLong
 * @date：2022/6/14 16:09
 * @detail：数据源接口
 */
interface DataHelper<T,K> {

    fun `输入数据` (t:T,k :K)

    fun `拉取数据`() :K
}
