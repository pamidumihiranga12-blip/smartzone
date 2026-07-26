package com.example.data

import android.util.Log
import com.example.data.db.OrderEntity
import com.example.data.db.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object MySqlApiManager {
    private const val TAG = "MySqlApiManager"

    suspend fun fetchProductsFromWebsite(apiUrl: String, apiKey: String): List<ProductEntity> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<ProductEntity>()
            try {
                val cleanUrl = if (apiUrl.endsWith("/")) "${apiUrl}get_products.php" else "$apiUrl/get_products.php"
                val url = URL("$cleanUrl?api_key=$apiKey")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val rawResponse = response.toString().trim()
                    if (rawResponse.startsWith("[")) {
                        val jsonArray = JSONArray(rawResponse)
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val priceVal = obj.optDouble("price", 0.0)
                            list.add(
                                ProductEntity(
                                    id = obj.optString("id", "mysql_$i"),
                                    title = obj.optString("title", "Product $i"),
                                    price = priceVal,
                                    originalPrice = obj.optDouble("original_price", priceVal * 1.2),
                                    category = obj.optString("category", "Routers"),
                                    imageUrl = obj.optString("image_url", "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800"),
                                    rating = obj.optDouble("rating", 4.8).toFloat(),
                                    reviewCount = obj.optInt("review_count", 15),
                                    isSale = obj.optBoolean("is_sale", true),
                                    description = obj.optString("description", "Imported from MySQL Website Database"),
                                    isAvailable = true,
                                    brand = obj.optString("brand", "SmartZone")
                                )
                            )
                        }
                        Log.d(TAG, "Successfully fetched ${list.size} products from MySQL API")
                    } else if (rawResponse.startsWith("{")) {
                        val obj = JSONObject(rawResponse)
                        val err = obj.optString("error", "Unknown API error")
                        Log.e(TAG, "MySQL API returned error object: $err")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed fetching products from website MySQL API: ${e.message}")
            }
            list
        }
    }

    suspend fun syncOrderToWebsite(apiUrl: String, apiKey: String, order: OrderEntity): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val cleanUrl = if (apiUrl.endsWith("/")) "${apiUrl}add_order.php" else "$apiUrl/add_order.php"
                val url = URL(cleanUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val json = JSONObject().apply {
                    put("api_key", apiKey)
                    put("order_id", order.orderId)
                    put("total_amount", order.totalAmount)
                    put("payment_method", order.paymentMethod)
                    put("delivery_address", order.deliveryAddress)
                    put("status", order.status)
                    put("items_summary", order.itemsSummary)
                }

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(json.toString())
                writer.flush()
                writer.close()

                val code = connection.responseCode
                Log.d(TAG, "Synced order ${order.orderId} to MySQL website database. Status: $code")
                code == 200
            } catch (e: Exception) {
                Log.e(TAG, "Failed syncing order to website MySQL API: ${e.message}")
                false
            }
        }
    }

    suspend fun syncProductToWebsite(apiUrl: String, apiKey: String, product: ProductEntity): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val cleanUrl = if (apiUrl.endsWith("/")) "${apiUrl}add_product.php" else "$apiUrl/add_product.php"
                val url = URL(cleanUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val json = JSONObject().apply {
                    put("api_key", apiKey)
                    put("id", product.id)
                    put("title", product.title)
                    put("price", product.price)
                    put("original_price", product.originalPrice)
                    put("category", product.category)
                    put("description", product.description)
                    put("image_url", product.imageUrl)
                }

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(json.toString())
                writer.flush()
                writer.close()

                val code = connection.responseCode
                Log.d(TAG, "Synced product ${product.id} to MySQL website. Status: $code")
                code == 200
            } catch (e: Exception) {
                Log.e(TAG, "Failed syncing product to website MySQL API: ${e.message}")
                false
            }
        }
    }

    fun getSamplePhpGetProductsCode(): String {
        return """
            === 1. config.php ===
            <?php
            ${'$'}db_host = "sdb-l.hosting.stackcp.net";
            ${'$'}db_name = "smartzoneweb-313932d478";
            ${'$'}db_user = "smartzoneweb-313932d478";
            ${'$'}db_pass = "6EiwvqlQu";
            ${'$'}conn = new mysqli(${'$'}db_host, ${'$'}db_user, ${'$'}db_pass, ${'$'}db_name);
            ?>

            === 2. get_products.php ===
            <?php
            header('Content-Type: application/json');
            require_once 'config.php';
            if ((${'_'}GET['api_key'] ?? '') !== 'sz_api_key_90231938') die(json_encode(["error"=>"Unauthorized"]));
            ${'$'}res = ${'$'}conn->query("SELECT * FROM products ORDER BY id DESC");
            ${'$'}list = array();
            while (${'$'}r = ${'$'}res->fetch_assoc()) { ${'$'}list[] = ${'$'}r; }
            echo json_encode(${'$'}list);
            ?>

            === 3. add_order.php (With Automatic SMTP Mail to User & Admin) ===
            <?php
            header('Content-Type: application/json');
            require_once 'config.php';
            ${'$'}data = json_decode(file_get_contents('php://input'), true);
            if ((${'$'}data['api_key'] ?? '') !== 'sz_api_key_90231938') die(json_encode(["error"=>"Unauthorized"]));

            ${'$'}orderId = ${'$'}conn->real_escape_string(${'$'}data['order_id']);
            ${'$'}total = (float)${'$'}data['total_amount'];
            ${'$'}method = ${'$'}conn->real_escape_string(${'$'}data['payment_method']);
            ${'$'}address = ${'$'}conn->real_escape_string(${'$'}data['delivery_address']);
            ${'$'}items = ${'$'}conn->real_escape_string(${'$'}data['items_summary']);

            // Insert into Database
            ${'$'}conn->query("INSERT INTO orders (order_id, total_amount, payment_method, delivery_address, items_summary, created_at) VALUES ('${'$'}orderId', '${'$'}total', '${'$'}method', '${'$'}address', '${'$'}items', NOW())");

            // SMTP Email Notification (account@smartzonelk.lk)
            ${'$'}toUser = "account@smartzonelk.lk";
            ${'$'}toAdmin = "smartzonelk101@gmail.com";
            ${'$'}subject = "SmartZone Order Confirmation - ${'$'}orderId";
            ${'$'}msg = "<h2>Order ${'$'}orderId Placed!</h2><p>Items: ${'$'}items</p><p>Total: Rs ${'$'}total</p><p>Address: ${'$'}address</p>";
            ${'$'}headers = "MIME-Version: 1.0\r\nContent-type:text/html;charset=UTF-8\r\nFrom: SmartZone Orders <account@smartzonelk.lk>\r\n";

            mail(${'$'}toUser, ${'$'}subject, ${'$'}msg, ${'$'}headers);
            mail(${'$'}toAdmin, "ADMIN: New Order ${'$'}orderId", ${'$'}msg, ${'$'}headers);

            echo json_encode(["success" => true]);
            ?>
        """.trimIndent()
    }
}
