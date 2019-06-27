/*
 * Bytecode Analysis Framework
 * Copyright (C) 2005 University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package edu.umd.cs.findbugs.annotations;

import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.*;

/**
 * The annotated element should might be null, and uses of the element should
 * check for null.
 *
 * When this annotation is applied to a method it applies to the method return
 * value.
 *
 * @deprecated - use CheckForNull instead; the name of which more clearly
 *             indicates that not only could the value be null, but that good
 *             coding practice requires that the value be checked for null.
 **/
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.CLASS)
@javax.annotation.Nonnull(when = When.MAYBE)
@TypeQualifierNickname
@Deprecated
public @interface PossiblyNull {

}