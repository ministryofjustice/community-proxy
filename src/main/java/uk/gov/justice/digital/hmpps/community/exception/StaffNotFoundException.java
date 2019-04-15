package uk.gov.justice.digital.hmpps.community.exception;

import java.util.function.Supplier;

public class StaffNotFoundException extends RuntimeException implements Supplier<StaffNotFoundException> {

    private static final String DEFAULT_MESSAGE_FOR_ID_FORMAT = "Staff [%s] is not found by this service.";

    public static StaffNotFoundException withId(String id) {
        return new StaffNotFoundException(String.format(DEFAULT_MESSAGE_FOR_ID_FORMAT, id));
    }

    public static StaffNotFoundException withMessage(String message) {
        return new StaffNotFoundException(message);
    }

    public static StaffNotFoundException withMessage(String message, Object... args) {
        return new StaffNotFoundException(String.format(message, args));
    }

    public StaffNotFoundException(String message) {
        super(message);
    }

    @Override
    public StaffNotFoundException get() {
        return new StaffNotFoundException(getMessage());
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
