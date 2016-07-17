package com.valevich.moneytracker.utils.errorHandlers;

import com.valevich.moneytracker.R;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import timber.log.Timber;

@EBean(scope = EBean.Scope.Singleton)
public class CustomRestErrorHandler implements ErrorHandler {

    @StringRes(R.string.error_network)
    String mNetworkErrorMessage;

    @StringRes(R.string.error_no_response)
    String mErrorNoResponseMessage;

    @StringRes(R.string.error_network_http)
    String mErrorNetworkHttpMessage;

    @StringRes(R.string.error_unknown)
    String mUnknownErrorMessage;


    @Override
    public Throwable handleError(RetrofitError cause) {
        String errorDescription;

        if (cause.getKind() == RetrofitError.Kind.NETWORK) {
            errorDescription = mNetworkErrorMessage;
        } else {
            if (cause.getResponse() == null) {
                errorDescription = mErrorNoResponseMessage;
            } else {

                // Error message handling - return a simple error to Retrofit handlers..
                try {
                    ErrorResponse errorResponse = (ErrorResponse) cause.getBodyAs(ErrorResponse.class);
                    errorDescription = errorResponse.error.data.message;
                } catch (Exception ex) {
                    try {
                        errorDescription = mErrorNetworkHttpMessage + cause.getResponse().getStatus();
                    } catch (Exception ex2) {
                        Timber.e("handleError: %s", ex2.getLocalizedMessage());
                        errorDescription = mUnknownErrorMessage;
                    }
                }
            }
        }

        return new Exception(errorDescription);
    }

    private class ErrorResponse {
        Error error;

        public class Error {
            Data data;

            public class Data {
                String message;
            }
        }
    }
}
