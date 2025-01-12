package com.bluntsoftware.saasy.repository;


import com.bluntsoftware.saasy.domain.App;
import com.bluntsoftware.saasy.domain.BraintreeCredentials;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class BraintreePaymentRepository {
    private final AppRepo appRepo;

    public BraintreePaymentRepository(AppRepo appRepo) {
        this.appRepo = appRepo;
    }

    public String generateClientToken(){
        return gateway().clientToken().generate();
    }

    public BraintreeGateway gateway(){
        App app = appRepo.findById("").block();
        BraintreeCredentials braintree = //app.getBraintree();

           BraintreeCredentials.builder()
                   .merchantId("gjjbjp8r4j3m58wr")
                   .publicKey("hfwmyzbcsnq2vjfy")
                   .privateKey("1efa04a33defe0476f44e373fbf451ab").build();

        BraintreeGateway braintreeGateway = null;

        if(braintree != null){
            Environment environment = Environment.SANDBOX;
            String env = braintree.getEnv(); //qa sandbox production
            if(env != null && !env.equalsIgnoreCase("")){
                environment = Environment.parseEnvironment(env);
            }

            braintreeGateway = new BraintreeGateway(environment,
                braintree.getMerchantId(),
                braintree.getPublicKey(),
                braintree.getPrivateKey());
        }
        return braintreeGateway;
    }
}
