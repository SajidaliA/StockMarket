package com.sajid_ali.stockmarket.data.repository

import com.sajid_ali.stockmarket.data.local.StockDatabase
import com.sajid_ali.stockmarket.data.mapper.toCompanyListing
import com.sajid_ali.stockmarket.data.remote.StockAPI
import com.sajid_ali.stockmarket.domain.model.CompanyListing
import com.sajid_ali.stockmarket.domain.repository.StockRepository
import com.sajid_ali.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockAPI,
    private val database: StockDatabase
) : StockRepository {

    private val stockDao = database.getStockDao
    override suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListing = stockDao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListing.map { it.toCompanyListing() }
            ))

            val isDBEmpty = localListing.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDBEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListing = try {
                val response = api.getListings()
            }catch (e: IOException){
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load data"))
            }catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load data"))
            }
        }
    }
}