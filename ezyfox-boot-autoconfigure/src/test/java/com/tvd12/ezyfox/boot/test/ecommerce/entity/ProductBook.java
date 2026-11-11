/*
 * Copyright 2022 youngmonkeys.org
 * 
 * Licensed under the ezyplatform, Version 1.0.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://youngmonkeys.org/licenses/ezyplatform-1.0.0.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.tvd12.ezyfox.boot.test.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "ecommerce_product_books")
@AllArgsConstructor
@NoArgsConstructor
public class ProductBook {
    @Id
    @Column(name = "product_id")
    private long productId;

    private String author;

    @Column(name = "author_user_id")
    private long authorUserId;

    @Column(name = "author_url")
    private String authorUrl;

    @Column(name = "book_type")
    private String bookType;

    private int pages;

    @Column(name = "cover_type")
    private String coverType;

    private String affiliate;

    @Column(name = "preview_link")
    private String previewLink;

    @Column(name = "distribution_company")
    private String distributionCompany;

    @Column(name = "distribution_company_id")
    private long distributionCompanyId;

    private String publisher;

    @Column(name = "publisher_id")
    private long publisherId;

    @Column(name = "number_of_authors")
    private int numberOfAuthors;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;
}
