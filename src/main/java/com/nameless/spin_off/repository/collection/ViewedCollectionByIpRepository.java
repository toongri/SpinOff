package com.nameless.spin_off.repository.collection;

import com.nameless.spin_off.entity.collection.ViewedCollectionByIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewedCollectionByIpRepository extends JpaRepository<ViewedCollectionByIp, Long> {
}
