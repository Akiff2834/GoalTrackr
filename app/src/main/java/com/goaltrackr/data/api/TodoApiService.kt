// app/src/main/java/com/goaltrackr/data/api/TodoApiService.kt
package com.goaltrackr.data.api

// Retrofit kütüphanesinden gerekli sınıfları içe aktar
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Kendi tanımladığımız veri transfer nesnelerini (DTO'ları) içe aktar
import com.goaltrackr.data.api.TodoDto
import com.goaltrackr.data.api.TodoListResponse
import com.goaltrackr.data.api.CreateUpdateTodoRequest

// Retrofit için API servis arayüzü.
// dummyjson.com/todos API'si ile etkileşim kurmak için HTTP metodlarını ve endpoint'leri tanımlar.
interface TodoApiService {

    // Tüm todoları getir (opsiyonel olarak userId'ye göre filtrele)
    // Örnek: GET /todos?userId=123
    @GET("todos")
    suspend fun getTodos(
        @Query("userId") userId: Int? = null,
        @Header("Authorization") authToken: String? = null // Yetkilendirme token'ı
    ): Response<TodoListResponse>

    // Yeni bir todo oluştur
    // Örnek: POST /todos
    @POST("todos/add") // dummyjson.com'da ekleme endpoint'i "todos/add"
    suspend fun createTodo(
        @Body request: CreateUpdateTodoRequest,
        @Header("Authorization") authToken: String? = null
    ): Response<TodoDto>

    // Belirli bir todo'yu güncelle
    // Örnek: PUT /todos/5
    @PUT("todos/{id}")
    suspend fun updateTodo(
        @Path("id") id: Int,
        @Body request: CreateUpdateTodoRequest,
        @Header("Authorization") authToken: String? = null
    ): Response<TodoDto>

    // Belirli bir todo'yu sil
    // Örnek: DELETE /todos/5
    @DELETE("todos/{id}")
    suspend fun deleteTodo(
        @Path("id") id: Int,
        @Header("Authorization") authToken: String? = null
    ): Response<TodoDto> // Silme işleminden sonra silinen öğeyi dönebilir
}
