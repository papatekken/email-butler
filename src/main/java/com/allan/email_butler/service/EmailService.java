package com.allan.email_butler.service;



import com.allan.email_butler.model.ScanEstimate;
import com.allan.email_butler.model.SenderCount;
import com.allan.email_butler.model.SenderSize;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface EmailService {
    List<SenderCount> getTopSenders(int scanLimit) throws GeneralSecurityException, IOException;

    List<SenderSize> getTopSendersBySize(int scanLimit) throws GeneralSecurityException, IOException;

    ScanEstimate estimateScanTime() throws GeneralSecurityException, IOException;
}
