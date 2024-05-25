package com.tasnimulhasan.data

import com.google.gson.JsonParser
import com.tasnimulhasan.domain.base.ApiResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NetworkBoundResource @Inject constructor(){

    private  val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun<ResultType> downloadData(api : suspend () -> Response<ResultType>): Flow<ApiResult<ResultType>> {
        return withContext(ioDispatcher) {
            flow {
                try {
                    emit(ApiResult.Loading(true))
                    val response:Response<ResultType> = api()
                    emit(ApiResult.Loading(false))
                    if (response.isSuccessful){
                        response.body()?.let {
                            emit(ApiResult.Success(data = it))
                        }?: emit(ApiResult.Error(message = "Unknown error occurred", code = 0))
                    }else{
                        emit(ApiResult.Error(message = parserErrorBody(response.errorBody()), code = response.code()))
                    }
                }catch (e:Exception){
                    emit(ApiResult.Loading(false))
                    emit(ApiResult.Error(message = message(e), code = code(e)))
                }
            }
        }
    }

    private fun parserErrorBody(response: ResponseBody?):String{
        return response?.let {
            val errorMessage = JsonParser.parseString(it.string()).asJsonObject["message"].asString
            errorMessage.ifEmpty { "Whoops! Something went wrong" }
            errorMessage
        }?:"Unknown error occur, please try again"
    }

    private fun message(throwable: Throwable?):String{
        when (throwable) {
            is SocketTimeoutException -> return "Whoops! connection time out, try again!"
            is IOException -> return "No internet connection, try again!"
            is HttpException -> return try {
                val errorJsonString = throwable.response()?.errorBody()?.string()
                val errorMessage = JsonParser.parseString(errorJsonString).asJsonObject["message"].asString
                errorMessage.ifEmpty { "Whoops! Something went wrong" }
            }catch (e:Exception){
                "Unknown error occur, please try again!"
            }
        }
        return "Unknown error occur, please try again!!!"
    }

    private fun code(throwable: Throwable?):Int{
        return when (throwable) {
            is HttpException -> (throwable).code()
            is IOException -> 100
            else -> 0
        }
    }
}