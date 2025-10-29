package com.example.horapj.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val ONBOARDING = "onboarding"
    const val REGISTRATION = "registration"
    const val MAIN = "main"
    const val COMPANY = "company"
    const val COMPANY_DETAIL = "company_detail/{companyId}"
    fun companyDetailRoute(companyId: Int) = "company_detail/$companyId"
}