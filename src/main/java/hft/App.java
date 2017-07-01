package hft;

import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    private static OkHttpClient client = new OkHttpClient();

    public static void main( String[] args ) throws IOException {

        System.out.println(getForSymbol("BTC-EUR"));
        System.out.println(getForSymbol("ETH-EUR"));
        System.out.println(getForSymbol("ETH-BTC"));
    }

    private static Tick getForSymbol(String symbol) throws IOException {
        Response response = client.newCall(new Request.Builder().get().url(String.format("https://api.gdax.com/products/%s/book", symbol)).build()).execute();
        if (response.isSuccessful()) {
            String json = response.body().string();
            return Tick.forSymbol(symbol).fromJson(json).build();
        } else {
            return null;
        }
    }
}
