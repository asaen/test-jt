/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Alexey Saenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.toptal.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * User entity.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 * @checkstyle HideUtilityClassConstructorCheck (200 lines)
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "users")
@SuppressWarnings({
    "PMD.UnusedPrivateField",
    "PMD.UseUtilityClass",
    "PMD.BeanMembersShouldSerialize"
    })
public class User {

    /**
     * Id.
     * @checkstyle MemberNameCheck (5 lines)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * User name.
     */
    @Column(unique = true)
    @NotBlank
    private String name;

    /**
     * Password.
     */
    @Column
    @NotBlank
    @Getter(onMethod = @__(@JsonIgnore))
    @Setter(onMethod = @__(@JsonProperty))
    private String password;

    /**
     * Role.
     */
    @Column
    @NotNull
    private Role role;

    /**
     * Entries.
     */
    @OneToMany(
        mappedBy = "user",
        fetch = FetchType.EAGER,
        cascade = CascadeType.REMOVE
        )
    @JsonIgnore
    private List<Entry> entries;

    /**
     * Encrypts the password.
     */
    @PrePersist
    @PreUpdate
    public final void encryptPassword() {
        if (this.password == null) {
            return;
        }
        this.password = new BCryptPasswordEncoder().encode(this.password);
    }

    /**
     * Checks if the user is a manager.
     * @return If manager {@code true}.
     */
    @Transient
    @JsonIgnore
    public final boolean isManager() {
        return this.role == Role.ROLE_MANAGER;
    }

    @Override
    public final String toString() {
        int size = 0;
        if (this.entries != null) {
            size = this.entries.size();
        }
        return String.format(
            "User [%s, %s, %s, %s entries]",
            this.id,
            this.name,
            this.role,
            size
        );
    }

    /**
     * Roles enumeration.
     * @author Alexey Saenko (alexey.saenko@gmail.com)
     * @version $Id$
     * @checkstyle JavadocVariableCheck (5 lines)
     */
    public static enum Role {
        ROLE_USER, ROLE_MANAGER;

        /**
         * Converts enumeration to text removing leading <code>ROLE_</code>.
         * @return String.
         */
        public String text() {
            return this.toString().substring("ROLE_".length());
        }

        /**
         * Finds role by its text representation.
         * @param text Text.
         * @return Role.
         */
        public static Role byText(final String text) {
            return Role.valueOf(String.format("ROLE_%s", text));
        }
    }

}
