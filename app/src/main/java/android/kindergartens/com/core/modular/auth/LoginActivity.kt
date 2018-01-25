package android.kindergartens.com.core.modular.auth

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarFragmentActivity
import android.os.Bundle

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : BaseToolbarFragmentActivity() {
    private var currentTabIndex: Int = 0
    private var index: Int = 0
    val loginFragment = LoginFragment()
    val inputPasswordFragment = InputPasswordFragment()
    val registerFragment = RegisterFragment()
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 允许使用transitions
        setContentView(R.layout.activity_login)
        initFragments()
    }

    private fun initFragments() {
        add(fragment = loginFragment)
        supportFragmentManager.beginTransaction().add(R.id.auth_container, loginFragment)
                .show(loginFragment).commit()
    }

    fun switchRegisterFragment(tel: String) {
        add(fragment = registerFragment)
        registerFragment.tel = tel
        changeFragmentByIndex(mFragments!!.size - 1)
    }

    fun switchInputPasswordFragment(tel: String) {
        add(fragment = inputPasswordFragment)
        inputPasswordFragment.tel = tel
        changeFragmentByIndex(mFragments!!.size - 1)
    }

    private fun changeFragmentByIndex(currentIndex: Int) {
        index = currentIndex
        switchFragment()
    }

    private fun switchFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(mFragments!![currentTabIndex])
        if (!mFragments!![index].isAdded) {
            fragmentTransaction.add(R.id.auth_container, mFragments!![index])
        }
        fragmentTransaction.show(mFragments!![index]).commit()
        currentTabIndex = index
        if (currentTabIndex == 1) {
//            (mFragments!![1] as DynamicFragment).initData()
        }

    }

    override fun onBackPressed() {
        if (mFragments?.size == 1) {
            super.onBackPressed()
        } else {
            if (mFragments == null || mFragments!!.size < 2) {
                super.onBackPressed()
            } else {
                changeFragmentByIndex(mFragments!!.size - 2)
                mFragments!!.removeAt(mFragments!!.size - 1)

            }

        }

    }
}


