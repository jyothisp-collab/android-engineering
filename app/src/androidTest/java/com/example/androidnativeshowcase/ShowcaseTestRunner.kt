package com.example.androidnativeshowcase

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class ShowcaseTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, ShowcaseApplication::class.java.name, context)
    }
}
