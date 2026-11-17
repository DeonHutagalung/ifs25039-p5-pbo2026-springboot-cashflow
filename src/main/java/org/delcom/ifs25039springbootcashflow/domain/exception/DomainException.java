package org.delcom.ifs25039springbootcashflow.domain.exception;

/** Checked exception dasar untuk semua kegagalan aturan bisnis domain. */
public class DomainException extends Exception {

    public DomainException(String message) {
        super(message);
    }
}
