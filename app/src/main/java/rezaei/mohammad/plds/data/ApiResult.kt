/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rezaei.mohammad.plds.data

import rezaei.mohammad.plds.data.model.response.ErrorHandling

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class ApiResult<out R> {

    data class Success<out T>(val response: T) : ApiResult<T>()
    data class Error(val errorHandling: ErrorHandling?) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=${response}]"
            is Error -> "Error[exception=${errorHandling?.errorMessage}]"
            Loading -> "Loading"
        }
    }
}

/**
 * `true` if [ApiResult] is of type [Success] & holds non-null [Success.data].
 */
val ApiResult<*>.succeeded
    get() = this is ApiResult.Success && response != null
