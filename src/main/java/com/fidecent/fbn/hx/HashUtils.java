package com.fidecent.fbn.hx;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import java.security.MessageDigest;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HashUtils implements ApplicationContextAware {

    private static String saltPhrase;
    private static int tokenHashMin;
    private static int tokenHashMax;

    @SneakyThrows
    public static String generateHash(Object... inputs) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String random = saltPhrase + RandomUtils.nextInt(tokenHashMin, tokenHashMax);
        String input = random + Stream.of(inputs).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(":"));
        return bytesToHex(md.digest(input.getBytes()));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Environment environment = applicationContext.getBean(Environment.class);
        saltPhrase = environment.getProperty("hx-events.hash-salt-phrase", String.class, RandomStringUtils.random(10));
        tokenHashMin = environment.getProperty("hx-events.hash-token-min", Integer.class, 1000);
        tokenHashMax = environment.getProperty("hx-events.hash-token-max", Integer.class, 100000);
    }
}
