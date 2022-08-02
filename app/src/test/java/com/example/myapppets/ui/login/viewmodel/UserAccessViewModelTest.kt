package com.example.myapppets.ui.login.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapppets.data.model.request.UserAccessRequest
import com.example.myapppets.domian.model.ResultModel
import com.example.myapppets.domian.usecase.UserAccessUseCase
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class UserAccessViewModelTest{

    private lateinit var viewModel: UserAccessViewModel
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userAccessUseCase: UserAccessUseCase

    private val userRequest = UserAccessRequest(email = "test@admin.com", password = "Password123")
    private val userRequestError = UserAccessRequest(email = "test@admin.com", password = "")

    private var resultModel = ResultModel()
    var resultModelError = ResultModel()


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        setUpRxSchedulers()
        initObjectMock()
        initControllers()
    }


    private fun initObjectMock() {

        resultModel = ResultModel(
            code = "0",
            message = "Sucessfull"
            )

        resultModelError = ResultModel(
            code = "-1",
            message = "Error"
        )
    }

    private fun setUpRxSchedulers() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    private fun initControllers() {
        whenever(userAccessUseCase.userAccess(userRequest)).thenReturn(
            Single.just(
                resultModel
            )
        )
        whenever(userAccessUseCase.userAccess(userRequestError)).thenReturn(
            Single.just(
                resultModelError
            )
        )

    }


    private fun initializeViewModel() {
        viewModel = UserAccessViewModel(
            userAccessUseCase
        )
    }


    @Test
    fun `When call userAccess then execute one time userAccess `() {
        initializeViewModel()
        viewModel.userValidation( "test@admin.com", "Password123")
        verify(userAccessUseCase, times(1)).userAccess(userRequest)
    }


    @Test
    fun `When call userAccess then return sucessfull response `() {
        initializeViewModel()
        viewModel.userValidation( "test@admin.com", "Password123")
        assertEquals(resultModel.code, "0")
        assertEquals(resultModel.message, "Sucessfull")
    }

    @Test
    fun `When call userAccess then return error response `() {
        initializeViewModel()
        viewModel.userValidation( "test@admin.com", "")
        assertEquals(resultModelError.code, "-1")
    }


}