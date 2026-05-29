package com.example.tegram

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Tegram Application — Hilt entry point.
 * @HiltAndroidApp triggers Hilt's code generation for the base application class,
 * which serves as the parent component for all dependency injection.
 */
@HiltAndroidApp
class MyApplication : Application()
