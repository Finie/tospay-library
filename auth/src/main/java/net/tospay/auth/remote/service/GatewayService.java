package net.tospay.auth.remote.service;

        import androidx.lifecycle.LiveData;

        import net.tospay.auth.model.Bank;
        import net.tospay.auth.model.Country;
        import net.tospay.auth.model.Network;
        import net.tospay.auth.remote.response.ApiResponse;
        import net.tospay.auth.remote.response.Result;
        import net.tospay.auth.ui.account.bank.model.Branch;
        import net.tospay.auth.ui.account.bank.model.Currency;

        import java.util.List;

        import retrofit2.http.GET;
        import retrofit2.http.Header;
        import retrofit2.http.Path;

public interface GatewayService {

    @GET("v1/config/countries")
    LiveData<ApiResponse<Result<List<Country>>>> countries();

    @GET("v1/config/mobile-countries")
    LiveData<ApiResponse<Result<List<Country>>>> mobileCountries(
            @Header("Authorization") String bearer
    );

    @GET("v1/config/mobile-operators/{iso}")
    LiveData<ApiResponse<Result<List<Network>>>> networks(
            @Header("Authorization") String bearer,
            @Path("iso") String iso
    );

    @GET("v1/config/bank-countries")
    LiveData<ApiResponse<Result<List<Country>>>> bankCountries(
            @Header("Authorization") String bearer
    );

    @GET("v1/bank/fetch/{country_name}/{page}/{size}")
    LiveData<ApiResponse<Result<List<Bank>>>> banks(
            @Header("Authorization") String bearer,
            @Path("country_name") String country_name,
            @Path("page") String page,
            @Path("size") String size
    );

    @GET("v1/bank/fetch/branch/{bank_id}/{page}/{size}")
    LiveData<ApiResponse<Result<List<Branch>>>> branch(
            @Header("Authorization") String bearer,
            @Path("bank_id") String bank_id,
            @Path("page") String page,
            @Path("size") String size
    );

    @GET("v1/config/currencies")
    LiveData<ApiResponse<Result<List<Currency>>>> currencies();
}
