package com.example.diplom.data.mapper

import com.example.diplom.data.model.TokenData
import com.example.diplom.domain.entity.Token

object TokenMapper {
    fun mapDataToDomain(data: TokenData?) : Token{
        if (data == null) throw IllegalArgumentException("Token can not be null")
        return Token(
            id = data.id,
            token = data.token,
        )
    }
}