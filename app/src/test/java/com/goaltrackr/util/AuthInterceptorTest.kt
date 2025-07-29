package com.goaltrackr.util

import com.goaltrackr.data.repository.UserPreferencesRepository
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import okhttp3.*
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals // ✅ doğru import
import java.nio.charset.Charset

class AuthInterceptorTest {

    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var interceptor: AuthInterceptor
    private lateinit var chain: Interceptor.Chain
    private lateinit var request: Request
    private lateinit var response: Response

    @Before
    fun setup() {
        mockkObject(Logger) // Logger.log çağrılarını izlemek için
        userPreferencesRepository = mockk()
        interceptor = AuthInterceptor(userPreferencesRepository)
        request = Request.Builder()
            .url("https://example.com/api")
            .build()
        chain = mockk()
    }

    @Test
    fun `adds Authorization header when token exists`() {
        every { chain.request() } returns request
        coEvery { userPreferencesRepository.authToken } returns flowOf("fake_token")

        val capturedRequest = slot<Request>()
        every { chain.proceed(capture(capturedRequest)) } returns Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(ResponseBody.create(null, "Success"))
            .build()

        interceptor.intercept(chain)

        val finalRequest = capturedRequest.captured
        val headerValue = finalRequest.header("Authorization")
        assertEquals("Bearer fake_token", headerValue)
    }

    @Test
    fun `does not add Authorization header when token is null`() {
        every { chain.request() } returns request
        coEvery { userPreferencesRepository.authToken } returns flowOf(null)

        every { chain.proceed(any()) } answers {
            val req = firstArg<Request>()
            Response.Builder()
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(null, "Success"))
                .build()
        }

        val response = interceptor.intercept(chain)
        val headerValue = response.request.header("Authorization")
        assertEquals(null, headerValue)
    }

    @Test
    fun `logs and clears token on 401 response`() {
        val fakeResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body(ResponseBody.create(null, "".toByteArray(Charset.defaultCharset())))
            .build()

        every { chain.request() } returns request
        coEvery { userPreferencesRepository.authToken } returns flowOf("expired_token")
        every { chain.proceed(any()) } returns fakeResponse
        coEvery { userPreferencesRepository.setAuthToken(null) } just Runs
        every { Logger.log(any(), any()) } just Runs

        interceptor.intercept(chain)

        verify { Logger.log("AuthInterceptor", "401 Unauthorized: Token geçersiz veya süresi dolmuş.") }
        coVerify { userPreferencesRepository.setAuthToken(null) }
    }
}
