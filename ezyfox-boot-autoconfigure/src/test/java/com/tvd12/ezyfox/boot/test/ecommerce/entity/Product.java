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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "ecommerce_products")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "secondary_code")
    private String secondaryCode;

    @Column(name = "icon_image_id")
    private long iconImageId;

    @Column(name = "banner_image_id")
    private long bannerImageId;

    @Column(name = "shop_id")
    private long shopId;

    @Column(name = "display_order")
    private long displayOrder;

    private BigInteger amount;

    @Column(name = "sold_amount")
    private BigInteger soldAmount;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "parent_id")
    private long parentId;

    @Column(name = "creator_user_id")
    private long creatorUserId;

    @Column(name = "creator_admin_id")
    private long creatorAdminId;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
