// app/src/main/java/com/goaltrackr/data/api/TodoDto.kt
package com.goaltrackr.data.api

// Gson kütüphanesinden SerializedName anotasyonunu içe aktar
import com.google.gson.annotations.SerializedName

// API'den gelen Todo öğesi için veri transfer nesnesi (DTO).
// JSON yanıtını Kotlin nesnesine dönüştürmek için kullanılır.
data class TodoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("todo") val todo: String, // API'de "title" yerine "todo" kullanılıyor
    @SerializedName("completed") val completed: Boolean,
    @SerializedName("userId") val userId: Int
)

// API'den gelen tüm Todo listesi için yanıt nesnesi
data class TodoListResponse(
    @SerializedName("todos") val todos: List<TodoDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("skip") val skip: Int,
    @SerializedName("limit") val limit: Int
)

// Yeni bir Todo oluşturmak veya güncellemek için istek gövdesi
data class CreateUpdateTodoRequest(
    @SerializedName("todo") val todo: String,
    @SerializedName("completed") val completed: Boolean? = null, // Opsiyonel
    @SerializedName("userId") val userId: Int? = null // Opsiyonel
)
