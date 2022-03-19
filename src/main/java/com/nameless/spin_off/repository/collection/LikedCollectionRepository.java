package com.nameless.spin_off.repository.collection;

import com.nameless.spin_off.entity.collection.LikedCollection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedCollectionRepository extends JpaRepository<LikedCollection, Long> {
}
