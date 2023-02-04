package com.test.fitnessstudios.data.sources.remote.api

import com.google.gson.annotations.SerializedName
import com.test.fitnessstudios.data.models.Studio

data class StudioResponse(
    @SerializedName("businesses")
    val studios: List<Studio>
)