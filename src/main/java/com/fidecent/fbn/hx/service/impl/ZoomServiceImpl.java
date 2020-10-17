package com.fidecent.fbn.hx.service.impl;

import com.fidecent.fbn.hx.service.ZoomService;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

//@Service
public class ZoomServiceImpl implements ZoomService {
    private static final int EXPIRATION_DURATION = 180;

    //@Value("hx-events.zoom.api-key")
    private String apiKey;

    //@Value("hx-events.zoom.api-secret")
    private String apiSecret;

    @Override
    @SneakyThrows
    public String generateJwtToken() {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
        Payload payload = new Payload(new JSONObject()
                .appendField("iss", apiKey)
                .appendField("exp", Instant.now().plusSeconds(EXPIRATION_DURATION).getEpochSecond())
        );
        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(new MACSigner(apiSecret));
        return jwsObject.serialize();
    }

    
}
