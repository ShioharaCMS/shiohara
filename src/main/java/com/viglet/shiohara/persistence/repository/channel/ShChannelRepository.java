package com.viglet.shiohara.persistence.repository.channel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.viglet.shiohara.persistence.model.channel.ShChannel;
import com.viglet.shiohara.persistence.model.site.ShSite;

public interface ShChannelRepository extends JpaRepository<ShChannel, Integer> {

	List<ShChannel> findAll();

	List<ShChannel> findByShSiteAndRootChannel(ShSite shSite, byte rootChannel);
	
	ShChannel findByShSiteAndName(ShSite shSite, String name);
	
	ShChannel findByShSiteAndParentChannelAndName(ShSite shSite, ShChannel parentChannel, String name);
	
	List<ShChannel> findByParentChannel(ShChannel parentChannel);
	
	ShChannel findById(int id);

	ShChannel save(ShChannel shChannel);

	@Modifying
	@Query("delete from ShChannel p where p.id = ?1")
	void delete(int shChannelId);
}