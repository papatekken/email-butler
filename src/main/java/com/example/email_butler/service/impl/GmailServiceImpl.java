package com.example.email_butler.service.impl;

import com.example.email_butler.model.SenderCount;
import com.example.email_butler.service.EmailService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class GmailServiceImpl implements EmailService {



    private static final String APPLICATION_NAME = "Gmail Stats API";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Token stored locally after first OAuth login
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    // We only need read access
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);

    // Path to your downloaded credentials.json from Google Cloud Console
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    // -------------------------------------------------------------------------
    // GmailService interface implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SenderCount> getTopSenders(int scanLimit) throws GeneralSecurityException, IOException {
        Gmail gmail = buildGmailClient();
        String user = "me";

        Map<String, Long> senderMap = new HashMap<>();
        String pageToken = null;
        int fetched = 0;

        // Paginate through inbox messages
        while (fetched < scanLimit) {
            int batchSize = Math.min(100, scanLimit - fetched); // Gmail max per page = 100

            Gmail.Users.Messages.List request = gmail.users().messages()
                    .list(user)
                    .setMaxResults((long) batchSize)
                    .setLabelIds(Collections.singletonList("INBOX"));

            if (pageToken != null) {
                request.setPageToken(pageToken);
            }

            ListMessagesResponse response = request.execute();
            List<Message> messages = response.getMessages();

            if (messages == null || messages.isEmpty()) break;

            // For each message, fetch only the From header (much faster than full message)
            for (Message msg : messages) {
                Message fullMsg = gmail.users().messages()
                        .get(user, msg.getId())
                        .setFormat("metadata")
                        .setMetadataHeaders(Collections.singletonList("From"))
                        .execute();

                String from = extractFromHeader(fullMsg);
                if (from != null && !from.isEmpty()) {
                    senderMap.merge(from, 1L, Long::sum);
                }
                fetched++;
            }

            pageToken = response.getNextPageToken();
            if (pageToken == null) break;
        }

        // Sort by count descending, return top 10
        return senderMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> new SenderCount(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Creates an authorized {@link Credential} via OAuth2.
     * On the first run this opens a browser window for the user to log in;
     * subsequent runs reuse the token stored in {@value #TOKENS_DIRECTORY_PATH}.
     */
    private Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        InputStream in = GmailServiceImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH
                    + "\n→ Please download credentials.json from Google Cloud Console "
                    + "and place it in src/main/resources/");
        }

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Builds and returns an authenticated {@link Gmail} API client.
     */
    private Gmail buildGmailClient() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Gmail.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Extracts the {@code From} header value from a message, or {@code null} if absent.
     */
    private String extractFromHeader(Message message) {
        if (message.getPayload() == null || message.getPayload().getHeaders() == null) {
            return null;
        }
        return message.getPayload().getHeaders().stream()
                .filter(h -> "From".equalsIgnoreCase(h.getName()))
                .map(MessagePartHeader::getValue)
                .findFirst()
                .orElse(null);
    }
}
