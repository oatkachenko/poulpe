/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.validation;

import java.util.Collections;
import java.util.Set;

/**
 */
public class ValidationResult {

    public static final ValidationResult EMPTY = new ValidationResult(Collections.<ValidationError>emptySet());

    private final Set<ValidationError> errors;

    public ValidationResult(Set<ValidationError> errors) {
        this.errors = errors;
    }
    
    /**
     * Should be called only after {@link #validate(Entity)}
     * @return {@code false} if there's no error, {@code true} otherwise
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Should be called only after {@link #validate(Entity)}. Before calling,
     * make sure that there is a error by invoking {@link #hasErrors()}.
     * @return specified error message or null if there's none
     */
    public Set<ValidationError> getErrors() {
        return errors;
    }
}
