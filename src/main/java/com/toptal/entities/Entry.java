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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.toptal.conf.Format;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entry class.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "entries")
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.BeanMembersShouldSerialize"})
public class Entry {

    /**
     * Id.
     * @checkstyle MemberNameCheck (5 lines)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Date.
     */
    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Format.DATE_FORMAT)
    private Date date;

    /**
     * Distance in meters.
     */
    @Column
    private Long distance;

    /**
     * Time in milliseconds.
     */
    @Column
    private Long time;

    /**
     * Entry owner.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    /**
     * Average speed in meter per second.
     * @return Speed value.
     */
    @Transient
    public final double getSpeed() {
        double result = 0.;
        if (!Objects.equals(this.time, 0L)) {
            result = this.distance / (this.time / (double) Time.MS_IN_SECOND);
        }
        return result;
    }
}
