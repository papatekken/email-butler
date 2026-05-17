package com.example.email_butler.service;



import com.example.email_butler.model.ScanEstimate;
import com.example.email_butler.model.SenderCount;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface EmailService {
    List<SenderCount> getTopSenders(int scanLimit) throws GeneralSecurityException, IOException;

    ScanEstimate estimateScanTime() throws GeneralSecurityException, IOException;
}
