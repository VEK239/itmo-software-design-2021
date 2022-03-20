package com.vlasova.db

import com.mongodb.client.model.Filters
import com.mongodb.rx.client.MongoClient
import com.mongodb.rx.client.MongoClients
import com.mongodb.rx.client.Success
import com.vlasova.model.Product
import com.vlasova.model.User
import rx.Observable
import org.bson.Document

class ReactiveMongoDriver(url: String) {
    init {
        client = MongoClients.create(url)
    }

    private fun addObject(collectionName: String, collectionObject: Document): Observable<Success> {
        return client.getDatabase(DATABASE_NAME)
                .getCollection(collectionName)
                .insertOne(collectionObject)
    }

    fun addUser(user: User): Observable<Success> {
        return addObject(USER_COLLECTION, user.asDocument())
    }

    fun addProduct(product: Product): Observable<Success> {
        return addObject(PRODUCT_COLLECTION, product.asDocument())
    }

    val allProducts: Observable<Product>
        get() = client.getDatabase(DATABASE_NAME)
            .getCollection(PRODUCT_COLLECTION)
            .find()
            .toObservable().map(::Product)

    fun getUser(id: Int): Observable<User> {
        return client.getDatabase(DATABASE_NAME)
            .getCollection(USER_COLLECTION)
            .find(Filters.eq("id", id))
            .toObservable().map(::User)
    }

    companion object {
        private lateinit var client: MongoClient
        const val MONGO_DB_URL = "mongodb://localhost:27017"
        private const val DATABASE_NAME = "test_db"
        private const val USER_COLLECTION = "users"
        private const val PRODUCT_COLLECTION = "products"
    }
}