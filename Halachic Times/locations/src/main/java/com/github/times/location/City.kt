/*
 * Copyright 2012, Moshe Waisberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.times.location

import android.location.Address
import android.os.Parcel
import android.os.Parcelable
import com.github.times.location.country.CountryPolygon
import java.util.Locale
import java.util.TimeZone
import kotlin.math.round

/**
 * City that is stored in the application binary.
 *
 * @author Moshe Waisberg
 */
class City : ZmanimAddress {

    constructor(locale: Locale) : super(locale)
    constructor(address: Address) : super(address)
    constructor(address: ZmanimAddress) : super(address)

    var timeZone: TimeZone = TimeZone.getDefault()

    companion object {
        /**
         * Factor to convert a fixed-point integer to double.
         */
        private const val RATIO = CountryPolygon.RATIO

        @JvmField
        val CREATOR: Parcelable.Creator<City> = object : Parcelable.Creator<City> {
            override fun createFromParcel(source: Parcel): City {
                val a = ZmanimAddress.CREATOR.createFromParcel(source)
                return City(a)
            }

            override fun newArray(size: Int): Array<City?> {
                return arrayOfNulls(size)
            }
        }

        @JvmStatic
        fun generateCityId(city: City): Long {
            return generateCityId(city.latitude, city.longitude)
        }

        @JvmStatic
        fun generateCityId(latitude: Double, longitude: Double): Long {
            val fixedPointLatitude = round(latitude * RATIO).toLong() and 0x7FFFFFFFL
            val fixedPointLongitude = round(longitude * RATIO).toLong() and 0xFFFFFFFFL
            return (fixedPointLatitude shl 31) or fixedPointLongitude
        }
    }
}