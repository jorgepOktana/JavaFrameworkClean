package Utils;

import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;

public class SelenideLogEvents implements LogEventListener {
    @Override
    public void afterEvent(LogEvent logEvent) {

        switch (logEvent.getStatus()){
            case PASS:
                Log.pass("Action: " + logEvent.getElement() + " - " + logEvent.getSubject() + " - Time(" + logEvent.getDuration() + ")ms");
                break;
            case FAIL:
                Log.fail("Action: " + logEvent.getElement() + " - " + logEvent.getSubject() + " - Time(" + logEvent.getDuration() + ")ms");
                Log.error(logEvent.getError().getMessage());
                break;
        }
        Log.info("Webdriver event End  : "+logEvent.getStatus().toString());
    }

    @Override
    public void beforeEvent(LogEvent logEvent) {
        Log.info("Webdriver event : "+logEvent.getStatus().toString());
        Log.info("Element detail: " + logEvent.getElement() + " -\"THEN\"- " + logEvent.getSubject());

    }
}
