package stocks

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import setJsonBody

class StockService(private val httpClient: HttpClient) {
    suspend fun getOrganization(organizationId: Long): Organization =
        httpClient.get("/api/organization/${organizationId}")

    suspend fun addOrganization(organization: Organization) {
        httpClient.post<HttpResponse>("/api/organization") {
            setJsonBody(organization)
        }
    }

    suspend fun getOrganizationStocks(organizationId: Long): List<Stock> =
        httpClient.get("/api/organization/stocks")

    suspend fun addStock(organization: Organization, stock: Stock) {
        httpClient.post<HttpResponse>("/api/organization/${organization.id}/stocks") {
            setJsonBody(stock)
        }
    }

    suspend fun getAllStocks(): List<Stock> =
        httpClient.get("/api/stock")

    suspend fun getStock(stockId: Long): Stock =
        httpClient.get("/api/stock/${stockId}")
}
