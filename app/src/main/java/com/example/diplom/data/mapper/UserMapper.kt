package com.example.diplom.data.mapper

import com.example.diplom.data.model.UserData
import com.example.diplom.domain.entity.User

object UserMapper {
    fun mapDataToDomain(data: UserData?) : User {
        if (data == null) throw IllegalArgumentException("User can not be null")
        return User(
            id = data.id,
            login = data.login,
            name = data.name,
            avatar = data.avatar,
        )
    }
}