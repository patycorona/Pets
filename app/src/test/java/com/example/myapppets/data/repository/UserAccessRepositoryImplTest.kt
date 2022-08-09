package com.example.myapppets.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapppets.data.model.request.UserAccessRequest
import com.example.myapppets.data.model.request.UserRequest
import com.example.myapppets.data.model.response.UserAccessResponse
import com.example.myapppets.data.network.CoreHomeApi
import com.example.myapppets.domian.model.ResultModel
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

class UserAccessRepositoryImplTest {

    //Creamos instancia del RepositoryImpl
    private lateinit var userAccessRepositoryImpl: UserAccessRepositoryImpl

    //hace la config. para las pruebas en otro lugar
    // y que todos los métodos den Before se emulen
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    //Hacemos el mock de apiService
    @Mock
    private lateinit var  apiService: CoreHomeApi

    // le pasomos valores a las variables para probar el requerimiento y error, datos de entrada
    private val userRequest = UserAccessRequest(email = "test@admin.com", password = "Password123")
    private val userRequestError = UserAccessRequest(email = "test@admin.com", password = "")

    //asignamos variables con los resultados de las pruebas
    private var resultModel = ResultModel()
    var resultModelError = ResultModel()

    //inicializamos la simulación de la instancia de los objetos
    private lateinit var userAccessResponse: UserAccessResponse
    private lateinit var userAccessResponseError: UserAccessResponse

    // asignamos la valores a los objetos de respuesta
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        setUpRxSchedulers()
        initObjectMock()
        initControllers()
    }

    // asignamos la valores a los objetos de respuesta si es correcta y error
    private fun initObjectMock() {
         userAccessResponse = UserAccessResponse(
            code = "0",
            message = "Sucessfull",
            user = UserRequest(
                name = "Juan",
                lastName = "Perez"
            )
        )

         userAccessResponseError = UserAccessResponse(
            code = "-1",
            message = "Error",
            user = UserRequest(
                name = "",
                lastName = ""
            )
        )


        resultModel = ResultModel(
            code = "0",
            message = "Sucessfull"
        )

        resultModelError = ResultModel(
            code = "-1",
            message = "Error"
        )

    }

    // simular  la ejecución en el hilo secundario RX programación reactiva
    private fun setUpRxSchedulers() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    //validacion de la pruba cuando le pasen los datos correctos que regrese el resulMol
    // y cuando no que responda resultModelError
    private fun initControllers() {
        whenever(apiService.userAccess(userRequest)).thenReturn(
            Single.just(
                userAccessResponse
            )
        )
        whenever(apiService.userAccess(userRequestError)).thenReturn(
            Single.just(
                userAccessResponseError
            )
        )
    }

    //inicializamos el RepositoryImpl
    private fun initializeViewModel() {
        userAccessRepositoryImpl = UserAccessRepositoryImpl(
            apiService
        )
    }

    //verificar que se ejecute esta función almenos 1 vez pasandole sus parametros
    @Test
    fun `When call userAccess then execute one time userAccess `() {
        initializeViewModel()
        val userAccessRequest = UserAccessRequest("test@admin.com", "Password123")
        userAccessRepositoryImpl.userAccess(userAccessRequest)
        verify(apiService, times(1)).userAccess(userRequest)
    }
    //cuando llamas la función userAccess y todo esta correcto
    @Test
    fun `When call userAccess then return sucessfull response `() {
        initializeViewModel()
        val userAccessRequest = UserAccessRequest("test@admin.com", "Password123")
        userAccessRepositoryImpl.userAccess(userAccessRequest)
        assertEquals(resultModel.code, "0")
        assertEquals(resultModel.message, "Sucessfull")
    }

    // cuando llamas a userAccess y mandas un dato mal te responde un error
    @Test
    fun `When call userAccess then return error response `() {
        initializeViewModel()
        val userAccessRequest = UserAccessRequest("test@admin.com", "")
        userAccessRepositoryImpl.userAccess(userAccessRequest)
        assertEquals(resultModelError.code, "-1")
    }

}