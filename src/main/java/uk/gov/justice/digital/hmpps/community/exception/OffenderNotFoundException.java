package uk.gov.justice.digital.hmpps.community.exception;

import java.util.function.Supplier;

public class OffenderNotFoundException extends RuntimeException implements Supplier<OffenderNotFoundException> {

    private static final String DEFAULT_MESSAGE_FOR_ID_FORMAT = "Offender [%s] is not found by this service.";

    public static OffenderNotFoundException withId(final String id) {
        return new OffenderNotFoundException(String.format(DEFAULT_MESSAGE_FOR_ID_FORMAT, id));
    }

    public static OffenderNotFoundException withMessage(final String message) {
        return new OffenderNotFoundException(message);
    }

    public static OffenderNotFoundException withMessage(final String message, final Object... args) {
        return new OffenderNotFoundException(String.format(message, args));
    }

    public OffenderNotFoundException(final String message) {
        super(message);
    }

    @Override
    public OffenderNotFoundException get() {
        return new OffenderNotFoundException(getMessage());
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
