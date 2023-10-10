package com.android.tanksgold;

import android.content.Context;

import java.io.*;
import java.util.Properties;

public class AuthorizationManager {

    private static final String AUTH_FILE = "authorization.properties";
    private static final String AUTH_TOKEN_KEY = "authToken";
    private static final String TOKEN_ISSUE_TIME_KEY = "tokenIssueTime";
    private static Context context;

    // Метод, чтобы установить контекст из вашей активности или приложения
    public static void setContext(Context ctx) {
        context = ctx;
    }

    public static void saveAuthorizationToken(String token) {
        try {
            Properties properties = new Properties();
            properties.setProperty(AUTH_TOKEN_KEY, token);

            // Сохраните текущее время как метку времени выдачи токена
            long currentTime = System.currentTimeMillis();
            properties.setProperty(TOKEN_ISSUE_TIME_KEY, String.valueOf(currentTime));

            File file = new File(context.getFilesDir(), AUTH_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            properties.store(fos, null);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAuthorizationToken() {
        try {
            Properties properties = new Properties();
            File file = new File(context.getFilesDir(), AUTH_FILE);
            FileInputStream fis = new FileInputStream(file);
            properties.load(fis);
            fis.close();

            // Получите токен и метку времени выдачи токена из свойств
            String authToken = properties.getProperty(AUTH_TOKEN_KEY);
            String tokenIssueTimeStr = properties.getProperty(TOKEN_ISSUE_TIME_KEY);

            if (tokenIssueTimeStr != null) {
                // Преобразуйте метку времени обратно в long
                long tokenIssueTime = Long.parseLong(tokenIssueTimeStr);

                // Возвращаем токен только если срок действия не истек
                if (isTokenValid(tokenIssueTime)) {
                    return authToken;
                } else {
                    // Вызываем метод для удаления токена
                    AuthorizationManager.removeAuthorizationToken();
                }
            }

            // Токен отсутствует или срок его действия истек, возвращаем null
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Метод для проверки срока действия токена (например, 7 дней)
    private static boolean isTokenValid(long tokenIssueTime) {
        long currentTime = System.currentTimeMillis();
        long tokenLifetime = 7 * 24 * 60 * 60 * 1000; // 7 дней в миллисекундах
        return (currentTime - tokenIssueTime) <= tokenLifetime;
    }

    // Метод для удаления токена и метки времени выдачи токена
    public static void removeAuthorizationToken() {
        try {
            File file = new File(context.getFilesDir(), AUTH_FILE);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
