package com.lab49.assignment.taptosnap

import com.lab49.assignment.taptosnap.features.main.ui.vm.MainViewModelTest
import com.lab49.assignment.taptosnap.features.splash.vm.SplashViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(SharedViewModelTest::class, MainViewModelTest::class, SplashViewModelTest::class)
class TapToSnapSuite