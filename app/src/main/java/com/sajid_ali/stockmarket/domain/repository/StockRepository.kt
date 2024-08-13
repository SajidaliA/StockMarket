package com.sajid_ali.stockmarket.domain.repository

import com.sajid_ali.stockmarket.domain.model.CompanyListing
import com.sajid_ali.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query : String
    ): Flow<Resource<List<CompanyListing>>>
}