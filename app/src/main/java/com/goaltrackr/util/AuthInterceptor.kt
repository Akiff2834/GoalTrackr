// app/src/main/java/com/goaltrackr/util/AuthInterceptor.kt
package com.goaltrackr.util

import com.goaltrackr.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

// Retrofit için bir OkHttp Interceptor'ı.
// API isteklerine yetkilendirme token'ını (Bearer Token) eklemek için kullanılır.
@Singleton
class AuthInterceptor @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // runBlocking, senkron bir bağlamda asenkron bir işlemi beklemek için kullanılır.
        // Interceptor'lar genellikle senkron çalıştığı için bu gereklidir.
        val authToken = runBlocking {
            userPreferencesRepository.authToken.first()
        }

        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Eğer token varsa, Authorization başlığını ekle
        authToken?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        // Hata yönetimi: Eğer 401 Unauthorized hatası alınırsa
        if (response.code == 401) {
            // Token'ı temizle veya kullanıcıyı login ekranına yönlendir (uygulama mantığına göre)
            // Bu kısım genellikle ViewModel veya bir Event bus ile daha iyi yönetilir.
            // Şimdilik sadece loglayalım.
            Logger.log("AuthInterceptor", "401 Unauthorized: Token geçersiz veya süresi dolmuş.")
            // Token'ı temizle (örneğin)
            runBlocking {
                userPreferencesRepository.setAuthToken(null)
            }
        }

        return response
    }
}
