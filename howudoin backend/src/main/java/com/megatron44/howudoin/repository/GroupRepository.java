package com.megatron44.howudoin.repository;

import com.megatron44.howudoin.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends MongoRepository<Group, String> {
    Optional<Group> findById(String groupId);
    List<Group> findByMembersContaining(String userId);
}
